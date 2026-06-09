package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.AppState
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AnalyticsScreen() {

    val firestore = FirebaseFirestore.getInstance()
    val branch = AppState.selectedBranch

    var totalProducts by remember { mutableStateOf(0) }
    var totalStock by remember { mutableStateOf(0) }
    var lowStockCount by remember { mutableStateOf(0) }


    DisposableEffect(branch?.branchId) {
        if (branch?.branchId == null) {
            onDispose { }
        } else {
            // Filter the metrics so it only calculates products belonging to THIS branch
            val listenerRegistration = firestore.collection("products")
                .whereEqualTo("branchId", branch.branchId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        val products = snapshot.documents
                        totalProducts = products.size

                        var stockSum = 0
                        var lowStock = 0

                        products.forEach { doc ->
                            val qty = doc.getLong("quantity")?.toInt() ?: 0
                            stockSum += qty

                            // Low stock threshold rule (<= 5)
                            if (qty <= 5) {
                                lowStock++
                            }
                        }

                        totalStock = stockSum
                        lowStockCount = lowStock
                    }
                }


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
        Text(
            text = "Analytics Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(5.dp))
        // Show which branch metrics are being evaluated
        Text(
            text = "Branch Metrics: ${branch?.branchName ?: "Global Summary"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Total unique products card
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Total Product Types: $totalProducts",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        //  Total physical volume inventory sum
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Total Stock Units: $totalStock",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        //  Alert card critical items reorder status
        Card(
            Modifier.fillMaxWidth(),
            colors = if (lowStockCount > 0) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            else CardDefaults.cardColors()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Low Stock Items (< 5): $lowStockCount",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (lowStockCount > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
