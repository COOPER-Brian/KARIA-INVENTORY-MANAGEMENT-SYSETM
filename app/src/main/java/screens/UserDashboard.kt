package com.example.kariainventoryapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kariainventoryapp.AppState

@Composable
fun UserDashboard(navController: NavController) {

    val branch = AppState.selectedBranch

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Screen edge padding
        horizontalAlignment = Alignment.CenterHorizontally, // Horizontal alignment
        verticalArrangement = Arrangement.Center // Vertical alignment centering
    ) {

        Text("User Dashboard", fontSize = 28.sp)

        Spacer(Modifier.height(6.dp))

        Text(
            text = branch?.branchName ?: "No branch selected",
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(30.dp))

        // 1. VIEW PRODUCTS
        DashboardCard("View Products") {
            navController.navigate("product_list?isBuyMode=false")
        }

        Spacer(Modifier.height(8.dp))

        // 2. BUY PRODUCTS
        DashboardCard("Buy Products") {
            navController.navigate("product_list?isBuyMode=true")
        }

        Spacer(Modifier.height(8.dp))

        // 3. PURCHASE HISTORY
        DashboardCard("Purchase History") {
            navController.navigate("history")
        }

        Spacer(Modifier.height(8.dp))

        // 4. SELECT BRANCH
        DashboardCard("Select Branch") {
            navController.navigate("select_branch")
        }

        Spacer(Modifier.height(40.dp))

        // 5. LOGOUT BUTTON
        Button(
            onClick = {
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }


    }
}