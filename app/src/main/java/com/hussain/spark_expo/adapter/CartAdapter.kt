package com.hussain.spark_expo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hussain.spark_expo.databinding.ItemCartBinding
import com.hussain.spark_expo.model.CartModel
import com.hussain.spark_expo.model.ProductModel
class CartAdapter(
    private val context: Context,
    private val productList: List<ProductModel>,
    private val cartList: List<CartModel>,
    private val onSelectionChanged: (List<CartModel>) -> Unit // Pass selected CartModel list
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val selectedCartItems = mutableListOf<CartModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]
        val product = productList.find { it.id == cartItem.productId }

        product?.let {
            holder.binding.itemName.text = it.name
            holder.binding.itemQuantity.text = "Qty: ${cartItem.quantity}"

            val quantity = cartItem.quantity.toDoubleOrNull() ?: 0.0
            val salePrice = it.salePrice.toDoubleOrNull() ?: 0.0
            val totalPrice = quantity * salePrice

            holder.binding.itemPrice.text = "Rs. $totalPrice"

            // Prevent unwanted checkbox state changes
            holder.binding.itemCheckbox.setOnCheckedChangeListener(null)

            // Restore checkbox state correctly
            holder.binding.itemCheckbox.isChecked = selectedCartItems.contains(cartItem)

            // Set proper checkbox listener
            holder.binding.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!selectedCartItems.contains(cartItem)) {
                        selectedCartItems.add(cartItem)
                    }
                } else {
                    selectedCartItems.remove(cartItem)
                }
                onSelectionChanged(selectedCartItems)  // Pass selected cart items
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)
}
