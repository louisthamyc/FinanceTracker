package com.louis.tham.financetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.louis.tham.financetracker.R
import com.louis.tham.financetracker.core.models.constants.TransactionType
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
    val radioOptions = listOf(TransactionType.EXPENSE, TransactionType.INCOME)

    LaunchedEffect(isDateTimePress) {
        if (isDateTimePress) {
            onIntent(AddTransactionIntent.OnDateTimeClicked)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.add_transaction)) }, navigationIcon = {
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
                Text(stringResource(R.string.save))
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
                title = { Text(text = stringResource(R.string.error)) },
                text = { Text(text = state.errorMessage) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onIntent(AddTransactionIntent.OnDismissError)
                        }
                    ) {
                        Text(stringResource(R.string.ok))
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
                label = { Text(stringResource(R.string.transaction_title)) },
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
                label = { Text(stringResource(R.string.amount)) },
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
            DropdownMenuOutlinedTextField(
                category = state.category,
                isCategoryError = isCategoryError,
                onIntent = onIntent
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.date,
                label = { Text(stringResource(R.string.date_time)) },
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                interactionSource = interactionSource
            )
            Row(
                modifier = Modifier.fillMaxWidth().selectableGroup()
            ) {
                radioOptions.forEach { type ->
                    Row(
                        Modifier
                            .weight(1f)
                            .selectable(
                                selected = (type == state.type),
                                onClick = { onIntent(AddTransactionIntent.OnTypeSelected(type)) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (type == state.type),
                            onClick = null
                        )
                        Text(
                            text = type.name,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(150.dp),
                value = "",
                label = { Text(stringResource(R.string.note)) },
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
fun DropdownMenuOutlinedTextField(
    category: String,
    isCategoryError: Boolean,
    onIntent: (AddTransactionIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf(
        "Food",
        "Transport",
        "Shopping",
        "Entertainment",
        "Utilities",
        "Bills",
        "Salary",
        "Other"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(
                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                enabled = true
            ).fillMaxWidth(),
            value = category,
            label = { Text(stringResource(R.string.category)) },
            onValueChange = {},
            singleLine = true,
            isError = isCategoryError,
            readOnly = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            modifier = Modifier.height(250.dp),
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onIntent(AddTransactionIntent.OnCategoryChanged(option))
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
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