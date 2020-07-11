package com.example.food.data

import org.json.JSONArray

data class Order(
    val orderId: Int,
    val resName: String,
    val orderDate: String,
    val foodItem: JSONArray
)