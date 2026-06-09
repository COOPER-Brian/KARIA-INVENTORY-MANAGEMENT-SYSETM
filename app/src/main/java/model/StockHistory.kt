package com.example.kariainventoryapp.models

data class StockHistory(

    val historyId: String = "",
    val productId: String = "",
    val branchId: String = "",

    // "Add" or "Remove" or "Update"
    val changeType: String = "",
    val oldQuantity: Int = 0,
    val newQuantity: Int = 0,

    val changedAt: Long = System.currentTimeMillis()
)