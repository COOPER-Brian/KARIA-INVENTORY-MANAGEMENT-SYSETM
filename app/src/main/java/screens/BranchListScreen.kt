package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.models.Branch
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BranchListScreen() {

    val firestore = FirebaseFirestore.getInstance()

    var branches by remember { mutableStateOf(listOf<Branch>()) }

    // Load branches once
    LaunchedEffect(Unit) {

        firestore.collection("branches")
            .addSnapshotListener { snapshot, error ->

                if (snapshot != null) {

                    val list = snapshot.documents.mapNotNull {

                        it.toObject(Branch::class.java)
                    }

                    branches = list
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Branches",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(branches) { branch ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(text = "Name: ${branch.branchName}")
                        Text(text = "Location: ${branch.location}")
                    }
                }
            }
        }
    }
}