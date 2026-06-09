package com.example.kariainventoryapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kariainventoryapp.AppState
import com.example.kariainventoryapp.repository.ProductRepository

// 1. THE STOCK UPDATE SCREEN
@Composable
fun StockScreen(
    navController: NavController,
    productId: String,
    currentQty: Int
) {
    val repo = ProductRepository()
    val context = LocalContext.current
    val branch = AppState.selectedBranch

    var quantity by remember { mutableStateOf(currentQty.toString()) }

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
        Text("Current Stock: $currentQty")
        Text("Branch: ${branch?.branchName ?: "No branch selected"}")
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("New Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val newQty = quantity.toIntOrNull()

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
                    oldQuantity = currentQty,
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

// 2. THE BUY/CANCEL PRODUCT SCREEN
@Composable
fun BuyProductScreen(
    navController: NavController,
    productId: String,
    currentQty: Int,
    unitPrice: Double,
    productName: String
) {
    val repo = ProductRepository()
    val context = LocalContext.current
    val branch = AppState.selectedBranch

    var buyQuantityInput by remember { mutableStateOf("1") }

    val enteredQty = buyQuantityInput.toIntOrNull() ?: 0
    val totalCost = enteredQty * unitPrice

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
        Text("Available Stock: $currentQty units")
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

        //  CONFIRM AND BUY THE PRODUCT
        Button(
            onClick = {
                if (branch == null) {
                    Toast.makeText(context, "Select branch first", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (enteredQty <= 0 || enteredQty > currentQty) {
                    Toast.makeText(context, "Invalid quantity selection", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val remainingQty = currentQty - enteredQty

                repo.updateStock(
                    productId = productId,
                    newQuantity = remainingQty,
                    oldQuantity = currentQty,
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
            enabled = enteredQty in 1..currentQty,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm Purchase")
        }

        Spacer(Modifier.height(12.dp))

        // CANCEL THE PRODUCT SELECTED
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