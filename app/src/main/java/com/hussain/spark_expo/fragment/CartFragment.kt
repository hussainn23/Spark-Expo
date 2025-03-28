package com.hussain.spark_expo.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.adapter.CartAdapter
import com.hussain.spark_expo.adapter.InvoiceAdapter
import com.hussain.spark_expo.databinding.DialogInvoiceBinding
import com.hussain.spark_expo.databinding.DialogPaymentBinding
import com.hussain.spark_expo.databinding.FragmentCartBinding
import com.hussain.spark_expo.model.CartItemDetailsModel
import com.hussain.spark_expo.model.CartModel
import com.hussain.spark_expo.model.ProductModel

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val selectedCartItems = mutableListOf<CartModel>()
    private val cartList = mutableListOf<CartModel>()
    private val productList = mutableListOf<ProductModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())

        cartAdapter = CartAdapter(requireContext(), productList, cartList) { selectedItems ->
            selectedCartItems.clear()
            selectedCartItems.addAll(selectedItems)
        }
        binding.recyclerViewCart.adapter = cartAdapter

        loadCartItems()

        binding.invoiceBtn.setOnClickListener {
            if (selectedCartItems.isEmpty()) {
                Toast.makeText(requireContext(), "No items selected!", Toast.LENGTH_SHORT).show()
            } else {
                showInvoiceDialog()
            }
        }
    }

    private fun loadCartItems() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Cart")
            .get()
            .addOnSuccessListener { documents ->
                cartList.clear()
                for (document in documents) {
                    val cartItem = document.toObject(CartModel::class.java)
                    cartItem.id = document.id
                    cartList.add(cartItem)
                }
                loadProducts()
            }
    }

    private fun loadProducts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (document in documents) {
                    val product = document.toObject(ProductModel::class.java)
                    productList.add(product)
                }
                cartAdapter.notifyDataSetChanged()
            }
    }

    private fun showInvoiceDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogInvoiceBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val selectedDetailsList = selectedCartItems.mapNotNull { cartItem ->
            val product = productList.find { it.id == cartItem.productId }
            if (product != null) {
                CartItemDetailsModel(cartItem, product)
            } else null
        }

        val invoiceAdapter = InvoiceAdapter(selectedDetailsList)
        dialogBinding.recyclerProducts.adapter = invoiceAdapter
        dialogBinding.recyclerProducts.layoutManager = LinearLayoutManager(requireContext())

        val totalAmount = selectedDetailsList.sumOf {
            val quantity = it.cart.quantity.toIntOrNull() ?: 1
            val price = it.product.salePrice.toDoubleOrNull() ?: 0.0
            quantity * price
        }

        dialogBinding.tvTotalAmount.text = "Rs. %.2f".format(totalAmount)
        dialogBinding.btnProceed.visibility = View.GONE

        dialogBinding.btnPrint.setOnClickListener {
            dialog.dismiss()
            showPaymentDialog(selectedDetailsList, totalAmount)
        }

        dialog.show()
    }

    private fun showPaymentDialog(selectedDetailsList: List<CartItemDetailsModel>, totalAmount: Double) {
        val paymentDialog = Dialog(requireContext())
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogBinding = DialogPaymentBinding.inflate(LayoutInflater.from(requireContext()))
        paymentDialog.setContentView(dialogBinding.root)
        paymentDialog.setCancelable(true)

        if (paymentDialog.window != null) {
            paymentDialog.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        paymentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.btnSubmitPayment.setOnClickListener {
            paymentDialog.dismiss()
            saveOrderToFirestore(selectedDetailsList, totalAmount)
        }

        paymentDialog.show()
    }

    private fun saveOrderToFirestore(selectedDetailsList: List<CartItemDetailsModel>, totalAmount: Double) {
        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("Orders").document()
        val orderId = orderRef.id

        val orderData = hashMapOf(
            "orderId" to orderId,
            "totalAmount" to totalAmount,
            "products" to selectedDetailsList.map { item ->
                hashMapOf(
                    "productId" to item.product.id,
                    "productName" to item.product.name,
                    "quantity" to item.cart.quantity,
                    "price" to item.product.salePrice
                )
            }
        )

        orderRef.set(orderData).addOnSuccessListener {
            Toast.makeText(requireContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show()

            // Remove items from cart after order is placed
            removeItemsFromCart(selectedDetailsList)

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error saving order!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to delete selected cart items from Firestore
    private fun removeItemsFromCart(selectedDetailsList: List<CartItemDetailsModel>) {
        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        for (item in selectedDetailsList) {
            val cartItemRef = db.collection("Cart").document(item.cart.id)
            batch.delete(cartItemRef)  // Add delete operation to batch
        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(requireContext(), "Cart items removed!", Toast.LENGTH_SHORT).show()
            loadCartItems()  // Refresh cart list after deletion
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to remove items from cart!", Toast.LENGTH_SHORT).show()
        }
    }


}