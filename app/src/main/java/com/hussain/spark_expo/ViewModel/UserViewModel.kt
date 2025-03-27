package com.hussain.spark_expo.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hussain.spark_expo.data.repo
import com.hussain.spark_expo.model.UserModel

class UserViewModel : ViewModel() {
    private val userRepository = repo()

    fun login(email: String, password: String): LiveData<UserModel?> {
        return userRepository.loginUser(email, password)
    }
}
