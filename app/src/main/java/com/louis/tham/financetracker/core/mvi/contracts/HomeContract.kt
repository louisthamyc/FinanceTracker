package com.louis.tham.financetracker.core.mvi.contracts

import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.base.UiEffect
import com.louis.tham.financetracker.core.mvi.base.UiIntent
import com.louis.tham.financetracker.core.mvi.base.UiState

data class HomeState(
    val isLoading: Boolean = false,
    val transactionList: List<TransactionEntity> = emptyList()
): UiState


sealed interface HomeIntent: UiIntent {
    object OnGetAllTransaction: HomeIntent
}

sealed interface HomeEffect: UiEffect {}
