package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.R
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.contracts.TransactionDetailIntent
import com.louis.tham.financetracker.core.mvi.viewmodels.TransactionDetailViewModel
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import java.util.Locale

@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    transactionId: String,
    onNavigateBack: () -> Unit,
) {
    val transaction by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.sendIntent(TransactionDetailIntent.OnGetTransactionById(transactionId))
    }
    TransactionDetailContent(transaction.transaction, onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailContent(
    transaction: TransactionEntity,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.transaction_detail))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        val trxDetailModifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_transaction_id),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.transaction_id))
                                Text(transaction.id.toString())
                            }
                        }
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_transaction_title),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.transaction_title))
                                Text(transaction.title)
                            }
                        }
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_date),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.date_time))
                                Text(transaction.date)
                            }
                        }
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_amount),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.amount))
                                Text(String.format(Locale.US, "%.2f", transaction.amount))
                            }
                        }
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_category),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.category))
                                Text(transaction.category)
                            }
                        }
                        Row(
                            modifier = trxDetailModifier
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_note),
                                contentDescription = "Back"
                            )
                            Column {
                                Text(stringResource(R.string.note))
                                Text(transaction.note)
                            }
                        }
                    }
                }
            }
            Button(
                onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                Text(stringResource(R.string.close))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailPreview() {
    FinanceTrackerTheme {
        TransactionDetailContent(
            TransactionEntity(),
            onNavigateBack = {}
        )
    }
}