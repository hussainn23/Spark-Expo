package com.hussain.spark_expo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hussain.spark_expo.model.Order

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serialNo: TextView = itemView.findViewById(R.id.tvSerialNo)
        private val orderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val productName: TextView = itemView.findViewById(R.id.tvProductName)
        private val price: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(order: Order) {
            serialNo.text = order.serialNo
            orderId.text = order.orderId
            productName.text = order.pharmacyName
            price.text = order.price
        }
    }
}
