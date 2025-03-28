package com.hussain.spark_expo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hussain.spark_expo.databinding.ItemProductBinding
import com.hussain.spark_expo.model.CartItemDetailsModel

class InvoiceAdapter(private val invoiceList: List<CartItemDetailsModel>) :
    RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder>() {

    class InvoiceViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val item = invoiceList[position]

        val productName = item.product.name
        val quantity = item.cart.quantity.toIntOrNull() ?: 1  // Default to 1 if null
        val salePrice = item.product.salePrice.toDoubleOrNull() ?: 0.0  // Default to 0.0 if null
        val totalPrice = quantity * salePrice

        holder.binding.tvProductName.text = productName
        holder.binding.tvQuantity.text = "x$quantity"
        holder.binding.tvPrice.text = "Rs. %.2f".format(totalPrice)  // Display with two decimal places
    }

    override fun getItemCount(): Int = invoiceList.size
}
