package com.example.kariainventoryapp.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.models.Product
import com.example.kariainventoryapp.repository.ProductRepository

@Composable
fun SellProductScreen(
    productId: String,
    onDone: () -> Unit
) {
    val repo = ProductRepository()
    val context = LocalContext.current

    var product by remember { mutableStateOf<Product?>(null) }
    var sellQty by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the product details when screen opens
    LaunchedEffect(productId) {
        if (productId.isNotEmpty()) {
            repo.getProductById(productId) { fetchedProduct ->
                product = fetchedProduct
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (product == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text("Product not found or has been deleted.")
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onDone) { Text("Go Back") }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(text = "Sell Product", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Name: ${product?.name}", style = MaterialTheme.typography.titleLarge)
            Text(text = "Available Qty: ${product?.quantity}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Price: KSh ${product?.price}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = sellQty,
                onValueChange = { sellQty = it },
                label = { Text("Quantity to Sell") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val inputQty = sellQty.toIntOrNull() ?: 0
                    val currentStock = product?.quantity ?: 0

                    if (inputQty <= 0) {
                        Toast.makeText(context, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (inputQty > currentStock) {
                        Toast.makeText(context, "Not enough stock available!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // CALLS UPDATED REPOSITORY FUNCTION WITH CORRECT PARAMETERS
                    repo.sellProduct(
                        productId = productId,
                        sellQty = inputQty
                    ) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Sale successful!", Toast.LENGTH_SHORT).show()
                            onDone() // Navigates back to the product list screen
                        } else {
                            Toast.makeText(context, error ?: "Transaction failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Sale")
            }
        }
    }
}