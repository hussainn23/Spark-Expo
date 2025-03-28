package com.hussain.spark_expo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.R
import com.hussain.spark_expo.adapter.OrdersAdapter
import com.hussain.spark_expo.model.Item
import com.hussain.spark_expo.model.Temp
import com.hussain.spark_expo.utils.Utils

class OrdersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalSales: TextView
    private val ordersList = mutableListOf<Temp>()
    private lateinit var adapter: OrdersAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var utils: Utils  // For Loading Animation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        recyclerView = view.findViewById(R.id.orderRCV)
        tvTotalSales = view.findViewById(R.id.tvTotalSales)
        utils = Utils(requireContext()) // Initialize animation utility

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrdersAdapter(ordersList)
        recyclerView.adapter = adapter

        fetchOrders()

        return view
    }

    private fun fetchOrders() {
        utils.startLoadingAnimation() // Start Loading Animation

        db.collection("Orders").get()
            .addOnSuccessListener { ordersSnapshot ->
                ordersList.clear()
                var totalSales = 0.0
                val orderMap = mutableMapOf<String, MutableList<Item>>()

                if (ordersSnapshot.isEmpty) {
                    tvTotalSales.text = "Total Sales: Rs. 0.00"
                    adapter.notifyDataSetChanged()
                    utils.endLoadingAnimation() // Stop Animation
                    return@addOnSuccessListener
                }

                for (orderDoc in ordersSnapshot.documents) {
                    val orderId = orderDoc.id

                    val productsList = orderDoc.get("products") as? List<Map<String, Any>> ?: emptyList()
                    val itemList = mutableListOf<Item>()
                    var orderTotal = 0.0

                    for (product in productsList) {
                        val price = (product["price"] as? String)?.toDoubleOrNull() ?: 0.0
                        val quantity = (product["quantity"] as? String)?.toIntOrNull() ?: 0

                        val item = Item(
                            price = price.toString(),
                            productId = product["productId"] as? String ?: "",
                            quantity = quantity.toString()
                        )

                        itemList.add(item)
                        orderTotal += price * quantity
                    }

                    orderMap[orderId] = itemList
                    totalSales += orderTotal
                }

                updateUI(orderMap, totalSales)
            }
            .addOnFailureListener {
                tvTotalSales.text = "Failed to load orders"
                utils.endLoadingAnimation() // Stop Animation
            }
    }

    private fun updateUI(orderMap: Map<String, List<Item>>, totalSales: Double) {
        ordersList.clear()
        for ((orderId, items) in orderMap) {
            ordersList.add(Temp(orderId = "#SG-$orderId", items = items))
        }

        adapter.notifyDataSetChanged()
        tvTotalSales.text = "Total Sales: Rs. ${String.format("%.2f", totalSales)}"

        utils.endLoadingAnimation() // Stop Animation
    }
}
