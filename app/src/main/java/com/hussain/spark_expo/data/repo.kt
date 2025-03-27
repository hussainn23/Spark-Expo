package com.hussain.spark_expo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.hussain.spark_expo.model.UserModel
import com.hussain.spark_expo.utils.Constants

class repo {

    private val db = FirebaseFirestore.getInstance()
    private val constants = Constants()
    private val adminCollection = constants.USER_COLLECTION

    fun loginUser(email: String, password: String): LiveData<UserModel?> {
        val loginResult = MutableLiveData<UserModel?>()

        db.collection(adminCollection)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val user = documents.documents[0].toObject(UserModel::class.java)
                    loginResult.value = user
                } else {
                    loginResult.value = null // User not found
                }
            }
            .addOnFailureListener {
                loginResult.value = null
            }

        return loginResult
    }




}
