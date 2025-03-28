package com.hussain.spark_expo.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hussain.spark_expo.R
import com.hussain.spark_expo.databinding.ActivityMainBinding
import com.hussain.spark_expo.databinding.DialogLogoutBinding
import com.hussain.spark_expo.utils.SharedPrefManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefManager=SharedPrefManager(this)
        val role=sharedPrefManager.getUser()?.role

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.ivCartIcon.setOnClickListener {
            navController.navigate(R.id.cartFragment)
        }

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
        val role = sharedPrefManager.getUser()?.role  // Fetch user role

        // Dashboard - Visible to both Admin and Cashier
        binding.dashboard.setOnClickListener {
            navController.navigate(R.id.dashboardFragment)
            closeDrawer()
        }
        binding.selectProducts.setOnClickListener {
            navController.navigate(R.id.productsViewPagerFragment)
            closeDrawer()
        }
        // Products - Visible only to Admin
        if (role == "admin") {

            binding.addProduct.setOnClickListener {
                navController.navigate(R.id.addProductFragment)
                closeDrawer()
            }
            binding.categories.setOnClickListener {
                navController.navigate(R.id.categoriesFragment)
                closeDrawer()
            }
        } else {
            binding.addProduct.visibility = View.GONE
            binding.categories.visibility = View.GONE
        }

        // Orders - Visible to both Admin and Cashier
        if (role == "admin" || role == "cashier") {
            binding.orders.setOnClickListener {
                navController.navigate(R.id.ordersFragment)
                closeDrawer()
            }
        } else {
            binding.orders.visibility = View.GONE
        }

        // Logout - Visible to all
        binding.logout.setOnClickListener {
            showLogoutDialog()
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
    private fun showLogoutDialog() {
        val dialogBinding = DialogLogoutBinding.inflate(LayoutInflater.from(this))

        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        if (dialog.window != null) {
            dialog.window!!.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        dialogBinding.yesBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        dialogBinding.noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}