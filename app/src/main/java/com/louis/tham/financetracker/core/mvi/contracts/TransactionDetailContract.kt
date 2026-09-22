package com.louis.tham.financetracker.core.mvi.contracts

import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.base.UiEffect
import com.louis.tham.financetracker.core.mvi.base.UiIntent
import com.louis.tham.financetracker.core.mvi.base.UiState

data class TransactionDetailState(
    val isLoading: Boolean = false,
    val transaction: TransactionEntity = TransactionEntity()
): UiState


sealed interface TransactionDetailIntent: UiIntent {
    data class OnGetTransactionById(val id: String): TransactionDetailIntent
}

sealed interface TransactionDetailEffect: UiEffect {}