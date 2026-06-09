package com.example.kariainventoryapp.models

data class Sale(
    val saleId: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantitySold: Int = 0,
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val branchId: String = "",
    val soldAt: Long = System.currentTimeMillis()
)