package model

data class Receipt(
    val saleId: String,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double,
    val date: String,
    val branchName: String
)