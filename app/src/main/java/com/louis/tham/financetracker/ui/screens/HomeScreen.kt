package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.contracts.HomeState
import com.louis.tham.financetracker.core.mvi.viewmodels.HomeViewModel
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import com.louis.tham.financetracker.utils.BarChartUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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

private fun calculateCurrentMonthTotal(transactions: List<TransactionEntity>): Double {
    val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    return transactions
        .filter { it.date.startsWith(currentMonth) }
        .sumOf { it.amount }
}

private fun calculatePreviousMonthTotal(transactions: List<TransactionEntity>): Double {
    val cal = Calendar.getInstance().apply {
        add(Calendar.MONTH, -1)
    }
    val prevMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.time)
    return transactions
        .filter { it.date.startsWith(prevMonth) }
        .sumOf { it.amount }
}

private fun calculateMonthOverMonthDiffPercentage(
    currentMonthTotal: Double,
    prevMonthTotal: Double
): String {
    if (prevMonthTotal == 0.0) {
        return if (currentMonthTotal > 0.0) "+100.0%" else "+0.0%"
    }
    val diff = ((currentMonthTotal - prevMonthTotal) / prevMonthTotal) * 100.0
    return String.format(Locale.US, "%+.1f%%", diff)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onClick: () -> Unit
) {
    val currentMonthTotal = calculateCurrentMonthTotal(state.transactionList)
    val prevMonthTotal = calculatePreviousMonthTotal(state.transactionList)
    val monthDiffPercentage = calculateMonthOverMonthDiffPercentage(currentMonthTotal, prevMonthTotal)

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
                .verticalScroll(rememberScrollState())
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
                        Text("RM ${String.format(Locale.US, "%.2f", currentMonthTotal)}")
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(monthDiffPercentage, fontSize = 12.sp)
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
                val barColorStart = MaterialTheme.colorScheme.primary
                val barColorEnd = MaterialTheme.colorScheme.secondary
                val axisColor = MaterialTheme.colorScheme.outlineVariant
                val textColor = MaterialTheme.colorScheme.onSurfaceVariant
                val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    BarChartUtil.drawBarChart(
                        drawScope = this,
                        transactions = state.transactionList,
                        barColorStart = barColorStart,
                        barColorEnd = barColorEnd,
                        axisColor = axisColor,
                        textColor = textColor,
                        gridLineColor = gridColor
                    )
                }
            }
            Text("Recent Transactions", modifier = Modifier.padding(bottom = 4.dp))
            state.transactionList.takeLast(5).forEach { transactionEntity ->
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
                            textAlign = TextAlign.End
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
                        date = "2026-07-16"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 22.12,
                        category = "testing",
                        date = "2026-07-21"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 16.12,
                        category = "testing",
                        date = "2026-07-12"
                    )
                )
            ),
            onClick = {}
        )
    }
}