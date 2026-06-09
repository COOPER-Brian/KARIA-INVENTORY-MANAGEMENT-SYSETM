package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnalyticsScreen() {

    val firestore = FirebaseFirestore.getInstance()

    var totalProducts by remember { mutableStateOf(0) }
    var totalStock by remember { mutableStateOf(0) }
    var lowStockCount by remember { mutableStateOf(0) }

    // 🔥 Load analytics once
    LaunchedEffect(Unit) {

        firestore.collection("products")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    val products = snapshot.documents

                    totalProducts = products.size

                    var stockSum = 0
                    var lowStock = 0

                    products.forEach { doc ->

                        val qty = doc.getLong("quantity")?.toInt() ?: 0

                        stockSum += qty

                        if (qty <= 5) {
                            lowStock++
                        }
                    }

                    totalStock = stockSum
                    lowStockCount = lowStock
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Analytics Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Products: $totalProducts")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Stock Units: $totalStock")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Low Stock Items: $lowStockCount")
            }
        }
    }
}