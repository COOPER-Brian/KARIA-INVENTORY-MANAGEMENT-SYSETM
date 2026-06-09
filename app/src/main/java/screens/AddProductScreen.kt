package com.example.kariainventoryapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AddProductScreen(
    branchId: String,
    productId: String? = null // 🔑 Added to check if we are updating an existing product
) {
    val repo = ProductRepository()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    val isEditMode = productId != null

    // 🔄 If an ID is passed, fetch the product information once when the screen opens
    LaunchedEffect(productId) {
        if (isEditMode && productId != null) {
            FirebaseFirestore.getInstance().collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        name = document.getString("name") ?: ""
                        qty = document.getLong("quantity")?.toString() ?: ""
                        price = document.getDouble("price")?.toString() ?: ""
                    }
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // Dynamic Title text based on mode
        Text(
            text = if (isEditMode) "Edit Product" else "Add Product",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))

        Text("Branch ID: $branchId")

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = qty,
            onValueChange = { qty = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (branchId.isBlank()) {
                    Toast.makeText(context, "No branch selected", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Call the updated repository function
                repo.saveOrUpdateProduct(
                    productId = productId, // Passes the ID (or null) to decide operation
                    name = name,
                    quantity = qty.toIntOrNull() ?: 0,
                    price = price.toDoubleOrNull() ?: 0.0,
                    branchId = branchId
                ) { success, msg ->
                    if (success) {
                        if (!isEditMode) {
                            // Only clear fields if it's a completely new product addition
                            name = ""
                            qty = ""
                            price = ""
                        }
                        val successMessage = if (isEditMode) "Product Updated" else "Product Saved"
                        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, msg ?: "Error saving product", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditMode) "Update Product" else "Save Product")
        }
    }
}