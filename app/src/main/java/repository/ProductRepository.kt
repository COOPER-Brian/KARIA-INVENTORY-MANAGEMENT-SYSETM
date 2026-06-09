package com.example.kariainventoryapp.repository

import com.example.kariainventoryapp.models.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()

    //UPDATE PRODUCT
    fun saveOrUpdateProduct(
        productId: String?,
        name: String,
        quantity: Int,
        price: Double,
        branchId: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val productMap = hashMapOf(
            "name" to name,
            "quantity" to quantity,
            "price" to price,
            "branchId" to branchId
        )

        if (productId != null) {
            firestore.collection("products")
                .document(productId)
                .set(productMap)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        } else {
            firestore.collection("products")
                .add(productMap)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { onComplete(false, it.message) }
        }
    }

    // FETCH SINGLE PRODUCT BY ID
    fun getProductById(productId: String, onComplete: (Product?) -> Unit) {
        firestore.collection("products")
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val product = document.toObject(Product::class.java)
                    onComplete(product?.copy(productId = document.id))
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    //  SELL PRODUCT METHOD
    fun sellProduct(productId: String, sellQty: Int, onComplete: (Boolean, String?) -> Unit) {
        val docRef = firestore.collection("products").document(productId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentQty = snapshot.getLong("quantity") ?: 0L

            if (currentQty >= sellQty) {
                transaction.update(docRef, "quantity", currentQty - sellQty)
                true
            } else {
                throw Exception("Insufficient stock available!")
            }
        }.addOnSuccessListener {
            onComplete(true, null)
        }.addOnFailureListener { exception ->
            onComplete(false, exception.message)
        }
    }

    // STOCK SCREEN MANIPULATION METHOD (Fills the Unresolved Reference)
    fun updateStock(
        productId: String,
        newQuantity: Int,
        oldQuantity: Int,
        branchId: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firestore.collection("products")
            .document(productId)
            .update("quantity", newQuantity)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                onComplete(false, exception.message)
            }
    }
}