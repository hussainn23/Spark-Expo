package com.hussain.spark_expo.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.hussain.spark_expo.ViewModel.ProductViewModel
import com.hussain.spark_expo.databinding.FragmentAddProductBinding
import com.hussain.spark_expo.model.ProductModel
import com.hussain.spark_expo.utils.SharedPrefManager
import com.hussain.spark_expo.utils.Utils
import kotlinx.coroutines.launch


class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val productViewModel: ProductViewModel by viewModels()
    private var categoryList = mutableListOf<String>()
    private var productList = listOf<ProductModel>()

    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var utils: Utils


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        utils = Utils(requireContext())
        sharedPrefManager = SharedPrefManager(requireContext())

        loadCategories()
        binding.btnAddProduct.setOnClickListener {
            addProductToFirebase()
        }
    }

    private fun loadCategories() {
        productViewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                categoryList.clear()
                categoryList.addAll(categories.map { it.name })
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categoryList
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.categorySpinner.adapter = adapter
            } else {
                Toast.makeText(requireContext(), "No categories found", Toast.LENGTH_SHORT).show()
            }
        }

        productViewModel.loadCategories() // Call this to fetch data
    }

    private fun addProductToFirebase() {
        val name = binding.productName.text.toString().trim()
        val categoryName = binding.categorySpinner.selectedItem?.toString()?.trim() ?: ""
        val description = binding.description.text.toString().trim()
        val quantity = binding.quantity.text.toString().trim()
        val itemCode = binding.itemCode.text.toString().trim()
        val purchasePrice = binding.purchasePrice.text.toString().trim().toDoubleOrNull() ?: 0.0
        val salePrice = binding.saleprice.text.toString().trim().toDoubleOrNull() ?: 0.0

        // 🛑 VALIDATIONS
        if (name.isEmpty() || categoryName.isEmpty() || itemCode.isEmpty() || salePrice == 0.0) {
            Toast.makeText(
                requireContext(),
                "Please fill in all required fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (itemCode.length < 5) {
            Toast.makeText(
                requireContext(),
                "Item code must be at least 5 characters long",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (purchasePrice < 5) {
            Toast.makeText(
                requireContext(),
                "Purchase price must be at least 5",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (salePrice < purchasePrice) {
            Toast.makeText(
                requireContext(),
                "Sale price must be greater than purchase price",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // ✅ CREATE PRODUCT OBJECT
        val product = ProductModel(
            name = name,
            categoryName = categoryName,
            description = description,
            quantity = quantity,
            itemCode = itemCode,
            purchasePrice = purchasePrice.toString(),
            salePrice = salePrice.toString()
        )

        // ✅ CALL FUNCTION TO VALIDATE AND ADD TO FIREBASE
        lifecycleScope.launch {
            productViewModel.validateAndAddProduct(product)

        }

        // ✅ OBSERVE THE RESULT AND SHOW MESSAGE
        utils.startLoadingAnimation()
        productViewModel.addProductResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                utils.endLoadingAnimation()
                Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_SHORT)
                    .show()
                getProducts()
                clearFields()
            }.onFailure {
                utils.endLoadingAnimation()
                Toast.makeText(requireContext(), "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getProducts() {

        productViewModel.getProduct().observe(viewLifecycleOwner, Observer { product ->
            if (product != null) {
                productList = product
                sharedPrefManager.saveProductsList(productList)
            }
        })


    }

    // ✅ CLEAR INPUT FIELDS AFTER SUCCESSFUL ADDITION
    private fun clearFields() {
        binding.productName.text.clear()
        binding.description.text.clear()
        binding.quantity.text.clear()
        binding.itemCode.text.clear()
        binding.purchasePrice.text.clear()
        binding.saleprice.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}