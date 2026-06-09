package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kariainventoryapp.AppState
import com.example.kariainventoryapp.models.Product
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProductListScreen(
    navController: NavController,
    isBuyMode: Boolean = false
) {
    val firestore = FirebaseFirestore.getInstance()
    val branch = AppState.selectedBranch
    val currentUserRole = AppState.userRole

    // Using a mutable State list so Compose re-renders instantly on background change
    var products by remember { mutableStateOf(listOf<Product>()) }

    // ✅ FIXED: DisposableEffect attaches a live listener and cleans up gracefully when leaving
    DisposableEffect(branch?.branchId) {
        if (branch?.branchId == null) {
            onDispose { }
        } else {
            // Establish a live snapshot channel bound to this specific branch ID
            val listenerRegistration = firestore.collection("products")
                .whereEqualTo("branchId", branch.branchId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        return@addSnapshotListener
                    }

                    // Live data map: Automatically updates whenever quantities decrement!
                    products = snapshot.documents.mapNotNull { doc ->
                        val p = doc.toObject(Product::class.java)
                        p?.copy(productId = doc.id)
                    }
                }

            // Unbind listeners safely to avoid memory leaks or query churn
            onDispose {
                listenerRegistration.remove()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val titleText = if (currentUserRole.lowercase() == "admin") "Manage Products"
        else if (isBuyMode) "Purchase Items"
        else "View Product Catalog"

        Text(text = titleText, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Branch: ${branch?.branchName ?: "No branch selected"}")
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {
            items(products) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = product.name, style = MaterialTheme.typography.titleMedium)

                        // 📊 This text updates live on the UI as soon as changes occur in Firebase
                        Text(text = "Qty: ${product.quantity}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Price: KSh ${product.price}", style = MaterialTheme.typography.bodyMedium)

                        if (currentUserRole.lowercase() != "admin" && isBuyMode) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    navController.navigate("buy_product/${product.productId}/${product.quantity}/${product.price.toFloat()}/${product.name}")
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Buy Product")
                            }
                        }

                        if (currentUserRole.lowercase() == "admin") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Button(
                                    onClick = { navController.navigate("add_product_screen?productId=${product.productId}") },
                                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Edit")
                                }

                                Button(
                                    onClick = { firestore.collection("products").document(product.productId).delete() },
                                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}