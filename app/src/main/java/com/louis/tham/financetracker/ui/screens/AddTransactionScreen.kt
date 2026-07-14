package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionEffect
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionIntent
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionState
import com.louis.tham.financetracker.core.mvi.contracts.TransactionField
import com.louis.tham.financetracker.core.mvi.viewmodels.AddTransactionViewModel
import com.louis.tham.financetracker.ui.theme.FinanceTrackerTheme
import com.louis.tham.financetracker.utils.DateUtil

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect {
            when (it) {
                is AddTransactionEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    AddTransactionContent(
        state,
        onIntent = { viewModel.sendIntent(it) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionContent(
    state: AddTransactionState,
    onIntent: (AddTransactionIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDateTimePress by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isDateTimePress) {
        if (isDateTimePress) {
            onIntent(AddTransactionIntent.OnDateTimeClicked)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Transaction") }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }) },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    onIntent(AddTransactionIntent.OnSaveClicked)
                }
            ) {
                Text("Save")
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        if (state.errorMessage != null) {
            AlertDialog(
                onDismissRequest = {
                    onIntent(AddTransactionIntent.OnDismissError)
                },
                title = { Text(text = "Error") },
                text = { Text(text = state.errorMessage) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(AddTransactionIntent.OnDismissError)
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val isTitleError = state.invalidFields.contains(TransactionField.TITLE)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                label = { Text("Transaction Title") },
                onValueChange = {
                    onIntent(AddTransactionIntent.OnTitleChanged(it))
                },
                singleLine = true,
                isError = isTitleError
            )
            val isAmountError = state.invalidFields.contains(TransactionField.AMOUNT)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.amount,
                label = { Text("Amount (RM)") },
                onValueChange = {
                    onIntent(AddTransactionIntent.OnAmountChanged(it))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                isError = isAmountError
            )
            val isCategoryError = state.invalidFields.contains(TransactionField.CATEGORY)
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.category,
                label = { Text("Category") },
                onValueChange = {
                    onIntent(AddTransactionIntent.OnCategoryChanged(it))
                },
                singleLine = true,
                isError = isCategoryError
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.date,
                label = { Text("Date & Time") },
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                interactionSource = interactionSource
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(150.dp),
                value = "",
                label = { Text("Note (Optional)") },
                onValueChange = {
                    onIntent(AddTransactionIntent.OnNoteChanged(it))
                },
            )
        }
        TransactionDatePickerDialog(state, onIntent)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDatePickerDialog(
    state: AddTransactionState,
    onIntent: (AddTransactionIntent) -> Unit
) {
    if (state.showDatePicker) {
        val initialMillis = remember(state.date) {
            DateUtil.convertStringToMillis(state.date)
        }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { onIntent(AddTransactionIntent.OnDismissDatePicker) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTimestamp = datePickerState.selectedDateMillis
                            ?: System.currentTimeMillis()
                        onIntent(AddTransactionIntent.OnDateSelected(selectedTimestamp))
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onIntent(AddTransactionIntent.OnDismissDatePicker) }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AddTransactionPreview() {
    FinanceTrackerTheme {
        AddTransactionContent(
            state = AddTransactionState(),
            onIntent = {},
            onNavigateBack = {}
        )
    }
}