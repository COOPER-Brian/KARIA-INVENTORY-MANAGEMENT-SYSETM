package navigation

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kariainventoryapp.AppState
import com.example.kariainventoryapp.auth.AuthRepository
import com.example.kariainventoryapp.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val authRepository = AuthRepository()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        //  LOGIN
        composable("login") {
            LoginScreen(
                onLoginClick = { email, password ->
                    authRepository.loginUser(email, password) { success, error ->
                        if (success) {
                            val uid = authRepository.auth.currentUser?.uid
                            if (uid != null) {
                                authRepository.getUserRole(uid) { role ->
                                    if (role != null) {
                                        AppState.userRole = role
                                    }

                                    when (role) {
                                        "admin" -> {
                                            Toast.makeText(context, "Welcome Admin", Toast.LENGTH_SHORT).show()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                navController.navigate("admin_dashboard") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            }, 300)
                                        }
                                        "user" -> {
                                            Toast.makeText(context, "Welcome User", Toast.LENGTH_SHORT).show()
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                navController.navigate("user_dashboard") {
                                                    popUpTo("login") { inclusive = true }
                                                }
                                            }, 300)
                                        }
                                        else -> {
                                            Toast.makeText(context, "Role not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, error ?: "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        //  REGISTER
        composable("register") {
            RegisterScreen(
                onRegisterClick = { name, email, password ->
                    authRepository.registerUser(name, email, password) { success, error ->
                        if (success) {
                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }, 300)
                        } else {
                            Toast.makeText(context, error ?: "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login")
                }
            )
        }

        // ADMIN DASHBOARD
        composable("admin_dashboard") {
            AdminDashboard(navController)
        }

        // USER DASHBOARD
        composable("user_dashboard") {
            UserDashboard(navController)
        }

        //  BRANCH
        composable("add_branch") {
            AddBranchScreen()
        }

        composable("branch_list") {
            BranchListScreen()
        }

        // SELECT BRANCH
        composable("select_branch") {
            SelectBranchScreen { branch ->
                AppState.selectedBranch = branch
                Toast.makeText(
                    context,
                    "Selected: ${branch.branchName}",
                    Toast.LENGTH_SHORT
                ).show()

                when (AppState.userRole) {
                    "admin" -> navController.navigate("admin_dashboard")
                    "user" -> navController.navigate("user_dashboard")
                    else -> navController.navigate("login")
                }
            }
        }

        // EDIT PRODUCT ROUTE
        composable(
            route = "add_product_screen?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val branch = AppState.selectedBranch
            val productId = backStackEntry.arguments?.getString("productId")

            if (branch == null) {
                Toast.makeText(context, "Please select a branch first", Toast.LENGTH_SHORT).show()
                navController.navigate("admin_dashboard")
            } else {
                AddProductScreen(
                    branchId = branch.branchId,
                    productId = productId
                )
            }
        }

    //  PRODUCT LIST
        composable(
            route = "product_list?isBuyMode={isBuyMode}",
            arguments = listOf(
                navArgument("isBuyMode") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isBuyMode = backStackEntry.arguments?.getBoolean("isBuyMode") ?: false
            ProductListScreen(navController = navController, isBuyMode = isBuyMode)
        }

        // HISTORY
        composable("history") {
            HistoryScreen()
        }

        // ANALYTICS
        composable("analytics") {
            AnalyticsScreen()
        }

        //  USER PURCHASE
        composable(
            route = "buy_product/{productId}/{currentQty}/{unitPrice}/{productName}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("currentQty") { type = NavType.IntType },
                navArgument("unitPrice") { type = NavType.FloatType },
                navArgument("productName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val currentQty = backStackEntry.arguments?.getInt("currentQty") ?: 0
            val unitPrice = backStackEntry.arguments?.getFloat("unitPrice")?.toDouble() ?: 0.0
            val productName = backStackEntry.arguments?.getString("productName") ?: ""

            BuyProductScreen(
                navController = navController,
                productId = productId,
                currentQty = currentQty,
                unitPrice = unitPrice,
                productName = productName
            )
        }

        // LEAVE FOR BACKWARDS COMPATIBILITY
        composable("sell_product") { }

        composable("sell_product/{productId}") { backStack ->
            val productId = backStack.arguments?.getString("productId") ?: ""
            SellProductScreen(
                productId = productId,
                onDone = {
                    navController.popBackStack()
                }
            )
        }

        // STOCK ROUTE
        composable(
            route = "update_stock/{productId}/{currentQty}",
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType },
                navArgument("currentQty") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            val currentQty = backStackEntry.arguments?.getInt("currentQty") ?: 0

            StockScreen(
                navController = navController,
                productId = productId,
                currentQty = currentQty
            )
        }
    }
}