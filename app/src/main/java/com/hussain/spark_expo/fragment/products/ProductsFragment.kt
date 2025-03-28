package com.hussain.spark_expo.fragment.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.hussain.spark_expo.R
import com.hussain.spark_expo.ViewModel.ProductViewModel
import com.hussain.spark_expo.adapter.ProductAdapter
import com.hussain.spark_expo.databinding.FragmentProductsBinding
import com.hussain.spark_expo.model.ProductModel
import com.hussain.spark_expo.utils.Utils

class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private lateinit var utils: Utils
    private lateinit var productAdapter: ProductAdapter
    private var productList: MutableList<ProductModel> = mutableListOf()
    private val productViewModel: ProductViewModel by viewModels()
    private lateinit var firestore: FirebaseFirestore
    private var productListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        utils = Utils(requireContext())
        firestore = FirebaseFirestore.getInstance()

        setupRecyclerView()
        setupSearch()
        fetchProductsLive()
    }

    private fun setupRecyclerView() {
        binding.orderItemsRecycler.layoutManager = LinearLayoutManager(requireContext())
        productAdapter = ProductAdapter(productList, ::showEditDialog, ::showDeleteDialog)
        binding.orderItemsRecycler.adapter = productAdapter
    }

    private fun fetchProductsLive() {
        productListener = firestore.collection("Products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error fetching products", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (value != null) {
                    productList.clear()
                    for (document in value.documents) {
                        val product = document.toObject(ProductModel::class.java)
                        if (product != null && product.quantity.toInt() > 1) {
                            productList.add(product)
                        }
                    }
                    productAdapter.updateList(productList)
                }
            }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { productAdapter.filterList(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { productAdapter.filterList(it) }
                return true
            }
        })
    }

    private fun showEditDialog(product: ProductModel) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_link, null)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val editText = dialogView.findViewById<EditText>(R.id.addCategory)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)

        titleTextView.text = "Edit Product Quantity"
        btnAdd.text = "Update"
        editText.setText(product.quantity)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val newQuantity = editText.text.toString().trim()
            if (newQuantity.isNotEmpty()) {
                updateProductQuantity(product, newQuantity.toInt())
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Quantity cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun updateProductQuantity(product: ProductModel, newQuantity: Int) {
        product.quantity = newQuantity.toString()
        utils.startLoadingAnimation()
        firestore.collection("Products").document(product.id)
            .set(product)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Product updated successfully", Toast.LENGTH_SHORT).show()
                }
                utils.endLoadingAnimation()
            }
            .addOnFailureListener {
                utils.endLoadingAnimation()
            }
    }

    private fun showDeleteDialog(product: ProductModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(product: ProductModel) {
        utils.startLoadingAnimation()
        firestore.collection("Products").document(product.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Product deleted successfully", Toast.LENGTH_SHORT).show()
                utils.endLoadingAnimation()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT).show()
                utils.endLoadingAnimation()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productListener?.remove()
    }
}