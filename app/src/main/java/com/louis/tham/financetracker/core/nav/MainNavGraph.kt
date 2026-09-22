package com.louis.tham.financetracker.core.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.louis.tham.financetracker.ui.screens.AddTransactionScreen
import com.louis.tham.financetracker.ui.screens.HomeScreen
import com.louis.tham.financetracker.ui.screens.TransactionDetailScreen

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeDestination
    ) {
        composable<HomeDestination> {
            HomeScreen(
                onNavigateToAddTransaction = {
                    navController.navigate(AddTransactionDestination)
                },
                onNavigateToTransactionDetails = {
                    navController.navigate(TransactionDetailDestination(it))
                }
            )
        }
        composable<AddTransactionDestination> {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<TransactionDetailDestination> { backStackEntry ->
            val args = backStackEntry.toRoute<TransactionDetailDestination>()
            TransactionDetailScreen(
                transactionId = args.transactionId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}