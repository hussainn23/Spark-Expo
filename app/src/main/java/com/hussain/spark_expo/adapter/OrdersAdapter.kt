package com.hussain.spark_expo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hussain.spark_expo.R
import com.hussain.spark_expo.model.Temp

class OrdersAdapter(private val ordersList: List<Temp>) :
    RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSrNo: TextView = view.findViewById(R.id.tvSrNo)
        val tvOrderId: TextView = view.findViewById(R.id.tvOrderNo)
        val tvOrderTotal: TextView = view.findViewById(R.id.tvPrices)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus) // Delivered
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]

        // Assigning Serial Number (starting from 1)
        holder.tvSrNo.text = (position + 1).toString()

        // Assigning Order ID in format #SG-0025
        holder.tvOrderId.text = "#SG-${String.format("%04d", position + 25)}"

        // Calculating Order Total
        var orderTotal = 0.0
        for (item in order.items) {
            val price = item.price.toDoubleOrNull() ?: 0.0
            val quantity = item.quantity.toIntOrNull() ?: 0
            orderTotal += price * quantity
        }

        // Formatting total price
        holder.tvOrderTotal.text = "Total: Rs. ${String.format("%.2f", orderTotal)}"

        // Hardcoded "Delivered" status
        holder.tvStatus.text = "Delivered"
    }

    override fun getItemCount(): Int {
        return ordersList.size
    }
}
