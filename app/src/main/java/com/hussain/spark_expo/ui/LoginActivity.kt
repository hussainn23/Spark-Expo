package com.hussain.spark_expo.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hussain.spark_expo.ViewModel.UserViewModel
import com.hussain.spark_expo.databinding.ActivityLoginBinding
import com.hussain.spark_expo.model.UserModel
import com.hussain.spark_expo.utils.SharedPrefManager
import com.hussain.spark_expo.utils.Utils

class LoginActivity : AppCompatActivity() {


    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var utils: Utils

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        utils = Utils(this)
        sharedPrefManager = SharedPrefManager(this)



        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            utils.startLoadingAnimation()
            userViewModel.login(email, password).observe(this) { user ->
                if (user != null) {
                    utils.endLoadingAnimation()
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain(user)
                } else {
                    utils.endLoadingAnimation()
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMain(user: UserModel) {
        sharedPrefManager.clearWholeSharedPref()
        sharedPrefManager.saveUser(user)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
