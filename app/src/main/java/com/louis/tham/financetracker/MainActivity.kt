package com.louis.tham.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.louis.tham.financetracker.core.nav.MainNavGraph
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTrackerTheme {
                MainNavGraph()
            }
        }
    }
}