package com.hussain.spark_expo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hussain.spark_expo.R
import com.hussain.spark_expo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        handleToolbar()
        setupCustomDrawerIcon()
        setupCustomNavigationView()

    }
    private fun handleToolbar(){
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            if (destination.id == R.id.profileFragment || destination.id == R.id.editProfileFragment || destination.id == R.id.chatsDetailFragment) {
//                window.statusBarColor = ContextCompat.getColor(this, R.color.dark_green)
//                supportActionBar?.hide()
//            } else {
//                window.statusBarColor = ContextCompat.getColor(this, R.color.white)
//                supportActionBar?.show()
//            }
//        }
    }

    private fun setupCustomDrawerIcon() {
        binding.ivDrawerIcon.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
    }

    private fun setupCustomNavigationView() {
        binding.dashboard.setOnClickListener {
            navController.navigate(R.id.dashboardFragment)
            closeDrawer()
        }
        binding.selectProducts.setOnClickListener {
            navController.navigate(R.id.productsViewPagerFragment)
            closeDrawer()
        }
        binding.addProduct.setOnClickListener {
            navController.navigate(R.id.addProductFragment)
            closeDrawer()
        }
        binding.categories.setOnClickListener {
            navController.navigate(R.id.categoriesFragment)
            closeDrawer()
        }
        binding.paymentHistory.setOnClickListener {
//            navController.navigate(R.id.paymentHistoryFragment)
            closeDrawer()
        }
    }

    private fun closeDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}