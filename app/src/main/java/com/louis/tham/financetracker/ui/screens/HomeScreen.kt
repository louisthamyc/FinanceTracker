package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.models.entity.calculateTotalNetBalance
import com.louis.tham.financetracker.core.mvi.contracts.HomeState
import com.louis.tham.financetracker.core.mvi.viewmodels.HomeViewModel
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import com.louis.tham.financetracker.ui.theme.expense
import com.louis.tham.financetracker.ui.theme.income
import com.louis.tham.financetracker.utils.BarChartUtil
import java.text.SimpleDateFormat
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
    val currentMonthTransactions = transactions.filter { it.date.startsWith(currentMonth) }
    val totalIncome = currentMonthTransactions.filter { it.type == TransactionType.INCOME.name }
        .sumOf { it.amount }
    val totalExpenses = currentMonthTransactions.filter { it.type == TransactionType.EXPENSE.name }
        .sumOf { it.amount }
    return totalIncome - totalExpenses
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeState,
    onClick: () -> Unit
) {
    val currentMonthTotal = calculateCurrentMonthTotal(state.transactionList)
    val totalNetBalance = state.transactionList.calculateTotalNetBalance()

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
                    tint = Color.Black
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text("Total Net Balance")
                    Text(
                        "RM ${String.format(Locale.US, "%.2f", totalNetBalance)}",
                        color = if (totalNetBalance > 0)
                            MaterialTheme.colorScheme.income
                        else
                            MaterialTheme.colorScheme.expense

                    )
                    Text("Current Month", modifier = Modifier.padding(top = 12.dp))
                    Text("RM ${String.format(Locale.US, "%.2f", currentMonthTotal)}")
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(vertical = 8.dp)
            ) {
                val incomeColor = MaterialTheme.colorScheme.income
                val expenseColor = MaterialTheme.colorScheme.expense
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
                        positiveBarColorStart = incomeColor,
                        positiveBarColorEnd = incomeColor.copy(alpha = 0.7f),
                        negativeBarColorStart = expenseColor,
                        negativeBarColorEnd = expenseColor.copy(alpha = 0.7f),
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
                        Icon(
                            imageVector = mapCategoryToIcon(transactionEntity.category),
                            modifier = Modifier
                                .height(34.dp)
                                .width(34.dp),
                            contentDescription = "Category"
                        )
                        Column(
                            modifier = modifier.padding(horizontal = 12.dp)
                        ) {
                            Text(transactionEntity.title)
                            Text(transactionEntity.date)
                        }
                        Text(
                            "RM ${String.format(Locale.US, "%.2f", transactionEntity.amount)}",
                            textAlign = TextAlign.End,
                            color = if (transactionEntity.type == TransactionType.INCOME.name) MaterialTheme.colorScheme.income else MaterialTheme.colorScheme.expense
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

fun mapCategoryToIcon(category: String): ImageVector {
    return when (category) {
        "Food" -> Icons.Default.Restaurant
        "Transport" -> Icons.Default.DirectionsBus
        "Shopping" -> Icons.Default.ShoppingCart
        "Entertainment" -> Icons.Default.Movie
        "Utilities" -> Icons.Default.Lightbulb
        "Bills" -> Icons.Default.Receipt
        "Salary" -> Icons.Default.AttachMoney
        else -> Icons.Default.Category
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
                        type = TransactionType.EXPENSE.name,
                        date = "2026-07-16"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 22.12,
                        category = "testing",
                        type = TransactionType.EXPENSE.name,
                        date = "2026-07-21"
                    ),
                    TransactionEntity(
                        id = 12321312312,
                        title = "test",
                        amount = 16.12,
                        category = "testing",
                        type = TransactionType.EXPENSE.name,
                        date = "2026-07-12"
                    )
                )
            ),
            onClick = {}
        )
    }
}