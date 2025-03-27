package com.aviator.myapplication.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.aviator.myapplication.OrderAdapter
import com.aviator.myapplication.R
import com.aviator.myapplication.databinding.FragmentDashboardBinding
import com.aviator.myapplication.model.Order
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrderAdapter(getDummyOrders())
        binding.recyclerViewOrders.adapter = orderAdapter

        setupBarChart()
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
        binding.barChart.invalidate() // Refresh chart
    }

    private fun getDummyOrders(): List<Order> {
        return listOf(
            Order("1","#SG-0025", "Abu Junaid Medical Store", "5000"),
            Order("2","#SG-0028", "Clinix Medical Store", "3500"),
            Order("3","#SG-0026", "Abu Junaid Medical Store", "1300"),
            Order("4","#SG-0029", "Adviyaat Medical Store", "200000")
        )
    }

}