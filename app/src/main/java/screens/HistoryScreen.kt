package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.models.StockHistory
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen() {

    val firestore = FirebaseFirestore.getInstance()

    var historyList by remember { mutableStateOf<List<StockHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // REAL-TIME LISTENER
    LaunchedEffect(Unit) {

        firestore.collection("stock_history")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {

                    historyList = snapshot.documents.mapNotNull {
                        it.toObject(StockHistory::class.java)
                    }.sortedByDescending { it.changedAt }

                    isLoading = false
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Stock History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        //  Loading state
        if (isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        // Empty state
        if (historyList.isEmpty()) {
            Text("No stock history found")
            return@Column
        }
         //  History list
        LazyColumn {

            items(historyList) { item ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Product ID: ${item.productId}")
                        Text("Branch ID: ${item.branchId}")
                        Text("Change: ${item.oldQuantity} → ${item.newQuantity}")
                        Text("Type: ${item.changeType}")

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Time: ${
                                formatTime(item.changedAt)
                            }",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}


 // Convert timestamp to readable format

fun formatTime(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown time"
    }
}