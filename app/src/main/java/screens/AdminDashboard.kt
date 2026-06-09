package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kariainventoryapp.AppState

@Composable
fun AdminDashboard(navController: NavController) {

    val branch = AppState.selectedBranch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER
        Text(
            text = "Admin Dashboard",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = branch?.branchName ?: "No branch selected",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // DASHBOARD BUTTONS AS CARDS
        DashboardCard("Add Branch") {
            navController.navigate("add_branch")
        }

        DashboardCard("View Branches") {
            navController.navigate("branch_list")
        }

        DashboardCard("Select Branch") {
            navController.navigate("select_branch")
        }

      //  PRODUCT MANAGEMENT
        DashboardCard("Add Product") {
            navController.navigate("add_product_screen")
        }

        DashboardCard("View Products") {
            navController.navigate("product_list")
        }

        DashboardCard("Stock History") {
            navController.navigate("history")
        }

        DashboardCard("Analytics") {
            navController.navigate("analytics")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // LOGOUT
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}


@Composable
fun DashboardCard(title: String, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp
            )
        }
    }
}