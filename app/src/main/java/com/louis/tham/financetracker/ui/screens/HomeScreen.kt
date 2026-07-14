package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        topBar = { TopAppBar(title = { Text("Transactions   ") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                state.transactionList.forEach { transactionEntity ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val modifier = Modifier.weight(1f)
                        Text(transactionEntity.title, modifier = modifier)
                        Text("RM ${String.format(Locale.US, "%.2f", transactionEntity.amount)}", modifier = modifier)
                        Text(transactionEntity.category, modifier = modifier)
                        Text(transactionEntity.date, modifier = modifier)
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick
            ) {
                Text("Add Transaction")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    FinanceTrackerTheme {
        HomeContent(
            HomeState(),
            onClick = {}
        )
    }
}