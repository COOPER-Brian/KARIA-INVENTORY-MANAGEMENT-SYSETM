package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.kariainventoryapp.models.Branch
import com.example.kariainventoryapp.repository.BranchRepository


@Composable
fun AddBranchScreen() {

    val branchRepository = remember { BranchRepository() }

    var branchName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(text = "Add Branch", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = branchName,
            onValueChange = { branchName = it },
            label = { Text("Branch Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val branch = Branch(
                    branchName = branchName,
                    location = location
                )

                branchRepository.addBranch(branch) { success, error ->

                    message = if (success) {
                        "Branch added successfully"
                    } else {
                        error ?: "Failed to add branch"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Branch")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = message)
    }
}