package com.example.kariainventoryapp.repository

import com.example.kariainventoryapp.models.StockHistory
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class HistoryRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun addHistory(
        productId: String,
        branchId: String,
        changeType: String,
        oldQty: Int,
        newQty: Int
    ) {

        val history = StockHistory(
            historyId = UUID.randomUUID().toString(),
            productId = productId,
            branchId = branchId,
            changeType = changeType,
            oldQuantity = oldQty,
            newQuantity = newQty
        )

        firestore.collection("stock_history")
            .document(history.historyId)
            .set(history)
    }
}