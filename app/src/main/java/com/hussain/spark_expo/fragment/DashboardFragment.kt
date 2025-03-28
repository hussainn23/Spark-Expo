package com.hussain.spark_expo.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.adapter.OrdersAdapter
import com.hussain.spark_expo.databinding.FragmentDashboardBinding
import com.hussain.spark_expo.model.Item
import com.hussain.spark_expo.model.Temp
import com.hussain.spark_expo.utils.Utils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.hussain.spark_expo.adapter.HomeAdapter
import com.hussain.spark_expo.utils.SharedPrefManager

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var orderAdapter: HomeAdapter
    private val ordersList = mutableListOf<Temp>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var utils: Utils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(requireContext())
        Toast.makeText(requireContext(), "role:"+sharedPrefManager.getUser()?.role, Toast.LENGTH_SHORT).show()
        utils = Utils(requireContext())
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(context)
        orderAdapter = HomeAdapter(ordersList)
        binding.recyclerViewOrders.adapter = orderAdapter

        fetchOrders()
        setupBarChart()
    }

    private fun fetchOrders() {
        utils.startLoadingAnimation()

        db.collection("Orders").get()
            .addOnSuccessListener { ordersSnapshot ->
                ordersList.clear()
                var totalSales = 0.0
                val orderMap = mutableMapOf<String, MutableList<Item>>()

                if (ordersSnapshot.isEmpty) {
                    binding.totalSales.text = "Rs. 0.00"
                    orderAdapter.notifyDataSetChanged()
                    utils.endLoadingAnimation()
                    return@addOnSuccessListener
                }

                for (orderDoc in ordersSnapshot.documents) {
                    val orderId = orderDoc.id
                    val productsList =
                        orderDoc.get("products") as? List<Map<String, Any>> ?: emptyList()
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
                binding.totalSales.text = "Failed to load orders"
                utils.endLoadingAnimation()
            }
    }

    private fun updateUI(orderMap: Map<String, List<Item>>, totalSales: Double) {
        ordersList.clear()
        for ((orderId, items) in orderMap) {
            ordersList.add(Temp(orderId = "#SG-$orderId", items = items))
        }

        orderAdapter.notifyDataSetChanged()
        binding.totalSales.text = "Rs. ${String.format("%.2f", totalSales)}"
        utils.endLoadingAnimation()
    }

    private fun setupBarChart() {
        val entries = listOf(
            BarEntry(0f, 100f),
            BarEntry(1f, 200f),
            BarEntry(2f, 300f),
            BarEntry(3f, 400f),
            BarEntry(4f, 350f),
            BarEntry(5f, 500f)
        )

        val dataSet = BarDataSet(entries, "Monthly Sales")
        dataSet.color = Color.GREEN

        val data = BarData(dataSet)
        binding.barChart.data = data
        binding.barChart.description.isEnabled = false
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }
}
