package com.hussain.spark_expo.utils

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.hussain.spark_expo.model.UserModel

class SharedPrefManager(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val gson = Gson()
    private val constants = Constants()





    fun saveUser(user: UserModel) {
        editor.putString("user", Gson().toJson(user))
        editor.apply()
    }

    fun getUser(): UserModel? {
        val json = sharedPref.getString("user", null) // Changed default value to null
        return if (json.isNullOrEmpty()) {
            null
        } else {
            try {
                Gson().fromJson(json, UserModel::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace() // Handle JSON parsing errors
                null
            }
        }
    }












    fun saveToken() {
        editor.putString("login", "yes")
        editor.apply() // Use apply() instead of commit() for better performance
    }

    fun checkLogin(): String? {
        return sharedPref.getString("login", null) // Return null if no login status is found
    }

    fun clearWholeSharedPref() {
        editor.clear().apply() // Use apply() instead of commit() for better performance
    }

//    fun saveMeditationsList(list: List<Meditation>) {
//        val json = gson.toJson(list)
//        editor.putString(constants.MEDITATION_COLLECTION, json)
//        editor.apply() // apply() is asynchronous; use commit() for synchronous saving
//    }
//
//
//    fun getMeditationsList(): List<Meditation>? {
//        val json = sharedPref.getString(constants.MEDITATION_COLLECTION, null)
//        val type = object : TypeToken<List<Meditation>>() {}.type
//        return if (json != null) {
//            gson.fromJson(json, type)
//        } else {
//            emptyList()
//        }
//    }








}
