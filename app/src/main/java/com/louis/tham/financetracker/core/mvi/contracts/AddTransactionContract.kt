package com.louis.tham.financetracker.core.mvi.contracts

import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.mvi.base.UiEffect
import com.louis.tham.financetracker.core.mvi.base.UiIntent
import com.louis.tham.financetracker.core.mvi.base.UiState
import com.louis.tham.financetracker.utils.DateUtil

enum class TransactionField {
    TITLE,
    AMOUNT,
    CATEGORY
}

data class AddTransactionState(
    val isLoading: Boolean = false,
    val title: String = "",
    val amount: String = "",
    val category: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val date: String = DateUtil.getCurrentDateDisplay(),
    val note: String = "",
    val invalidFields: Set<TransactionField> = emptySet(),
    val errorMessage: String? = null,
    val showDatePicker: Boolean = false
): UiState

sealed interface AddTransactionIntent: UiIntent {
    data class OnTitleChanged(val title: String): AddTransactionIntent
    data class OnAmountChanged(val amount: String): AddTransactionIntent
    data class OnCategoryChanged(val category: String): AddTransactionIntent
    data class OnNoteChanged(val note: String): AddTransactionIntent
    data class OnTypeSelected(val type: TransactionType): AddTransactionIntent
    object OnDateTimeClicked: AddTransactionIntent
    object OnSaveClicked: AddTransactionIntent
    object OnDismissError: AddTransactionIntent
    data class OnDateSelected(val timestamp: Long) : AddTransactionIntent
    object OnDismissDatePicker : AddTransactionIntent
}

sealed interface AddTransactionEffect: UiEffect {
    object NavigateBack: AddTransactionEffect
}