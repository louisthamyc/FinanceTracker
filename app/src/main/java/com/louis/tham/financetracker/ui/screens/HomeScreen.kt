package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.contracts.HomeState
import com.louis.tham.financetracker.core.mvi.viewmodels.HomeViewModel
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddTransaction: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                else -> {}
            }
        }
    }

    HomeContent(
        state = state,
        onClick = onNavigateToAddTransaction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Transactions   ") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Financial Summary")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Total Balance")
                        Text("RM 5000.00")
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("+2.1%", fontSize = 12.sp)
                        Text("this month", fontSize = 12.sp)
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 8.dp)
            ) {

            }
            Text("Recent Transactions", modifier = Modifier.padding(bottom = 4.dp))
            state.transactionList.forEach { transactionEntity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val modifier = Modifier.weight(1f)
                        Text(transactionEntity.category)
                        Column(
                            modifier = modifier.padding(horizontal = 6.dp)
                        ) {
                            Text(transactionEntity.title)
                            Text(transactionEntity.date)
                        }
                        Text(
                            "RM ${String.format(Locale.US, "%.2f", transactionEntity.amount)}",
                            modifier = modifier
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Details"
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    FinanceTrackerTheme {
        HomeContent(
            HomeState(
                transactionList = listOf(
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 12.12,
                        category = "testing",
                        date = "16-07-2026"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 12.12,
                        category = "testing",
                        date = "16-07-2026"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 12.12,
                        category = "testing",
                        date = "16-07-2026"
                    )
                )
            ),
            onClick = {}
        )
    }
}