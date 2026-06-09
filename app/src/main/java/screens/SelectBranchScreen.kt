package com.example.kariainventoryapp.screens

import androidx.compose.foundation.clickable
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
fun SelectBranchScreen(
    onBranchSelected: (Branch) -> Unit
) {

    val firestore = FirebaseFirestore.getInstance()

    var branches by remember { mutableStateOf(listOf<Branch>()) }

    // Load branches live
    LaunchedEffect(Unit) {

        firestore.collection("branches")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    branches = snapshot.documents.mapNotNull {
                        it.toObject(Branch::class.java)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Select Branch",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(branches) { branch ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            onBranchSelected(branch)
                        }
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(text = branch.branchName)
                        Text(text = branch.location)
                    }
                }
            }
        }
    }
}