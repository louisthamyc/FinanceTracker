package com.louis.tham.financetracker.core.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.louis.tham.financetracker.ui.screens.AddTransactionScreen
import com.louis.tham.financetracker.ui.screens.HomeScreen

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
    }
}