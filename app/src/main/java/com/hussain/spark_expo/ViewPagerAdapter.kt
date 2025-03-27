package com.hussain.spark_expo

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hussain.spark_expo.fragment.products.OutOfStockProductFragment
import com.hussain.spark_expo.fragment.products.ProductsFragment

class ViewPagerAdapter(fragmentActivity: Fragment) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2  // Number of fragments

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductsFragment()  // First fragment
            1 -> OutOfStockProductFragment()  // Second fragment
            else -> ProductsFragment() // Default case
        }
    }
}