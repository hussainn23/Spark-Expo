package com.hussain.spark_expo.model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String="",
    val price: String="",
    val productId:String="",
    val quantity:String="",
    )