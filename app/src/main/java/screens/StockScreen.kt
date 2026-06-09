package com.example.kariainventoryapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kariainventoryapp.AppState
import com.example.kariainventoryapp.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore

//  THE REAL-TIME STOCK UPDATE SCREEN
@Composable
fun StockScreen(
    navController: NavController,
    productId: String,
    initialQty: Int // Renamed to clarify it's just a fallback starting point
) {
    val repo = ProductRepository()
    val context = LocalContext.current
    val branch = AppState.selectedBranch
    val firestore = FirebaseFirestore.getInstance()

    // Dynamic state that hooks into our snapshot listener
    var liveQty by remember { mutableStateOf(initialQty) }
    var quantityInput by remember { mutableStateOf(initialQty.toString()) }

    // Listen to Firestore live updates for this product
    DisposableEffect(productId) {
        val listener = firestore.collection("products").document(productId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val dbQty = snapshot.getLong("quantity")?.toInt() ?: initialQty
                    liveQty = dbQty
                }
            }
        onDispose { listener.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Update Stock",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))
        Text("Current Live Stock: $liveQty")
        Text("Branch: ${branch?.branchName ?: "No branch selected"}")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = quantityInput,
            onValueChange = { quantityInput = it },
            label = { Text("New Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val newQty = quantityInput.toIntOrNull()

                if (branch == null) {
                    Toast.makeText(context, "Select branch first", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (newQty == null) {
                    Toast.makeText(context, "Enter valid quantity", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                repo.updateStock(
                    productId = productId,
                    newQuantity = newQty,
                    oldQuantity = liveQty,
                    branchId = branch.branchId,
                    onComplete = { success: Boolean, msg: String? ->
                        if (success) {
                            Toast.makeText(context, "Stock updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, msg ?: "Error updating stock", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Stock")
        }
    }
}

//  THE BUYING SCREEN
@Composable
fun BuyProductScreen(
    navController: NavController,
    productId: String,
    initialQty: Int,
    unitPrice: Double,
    productName: String
) {
    val repo = ProductRepository()
    val context = LocalContext.current
    val branch = AppState.selectedBranch
    val firestore = FirebaseFirestore.getInstance()

    // Dynamic variable linked directly to your database state
    var liveQty by remember { mutableStateOf(initialQty) }
    var buyQuantityInput by remember { mutableStateOf("1") }

    val enteredQty = buyQuantityInput.toIntOrNull() ?: 0
    val totalCost = enteredQty * unitPrice

    // Listen to Firestore live updates for this product
    DisposableEffect(productId) {
        val listener = firestore.collection("products").document(productId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val dbQty = snapshot.getLong("quantity")?.toInt() ?: initialQty
                    liveQty = dbQty
                }
            }
        onDispose { listener.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Buy Product: $productName",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(20.dp))
        Text("Available Live Stock: $liveQty units")
        Text("Unit Price: KSh $unitPrice")
        Text("Branch: ${branch?.branchName ?: "No branch selected"}")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = buyQuantityInput,
            onValueChange = { buyQuantityInput = it },
            label = { Text("Quantity to Purchase") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(25.dp))

        Text(
            text = "Total Cost: KSh $totalCost",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(30.dp))

        Button(
            onClick = {
                if (branch == null) {
                    Toast.makeText(context, "Select branch first", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                // Compares seamlessly against our real-time variable
                if (enteredQty <= 0 || enteredQty > liveQty) {
                    Toast.makeText(context, "Invalid quantity selection", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val remainingQty = liveQty - enteredQty

                repo.updateStock(
                    productId = productId,
                    newQuantity = remainingQty,
                    oldQuantity = liveQty,
                    branchId = branch.branchId,
                    onComplete = { success: Boolean, msg: String? ->
                        if (success) {
                            Toast.makeText(context, "Purchase completed successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, msg ?: "Transaction processing error", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            enabled = enteredQty in 1..liveQty,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Purchase")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                buyQuantityInput = ""
                Toast.makeText(context, "Purchase cancelled", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cancel & Go Back")
        }
    }
}