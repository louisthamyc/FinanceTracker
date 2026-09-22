package com.louis.tham.financetracker.core.mvi.viewmodels

import androidx.lifecycle.viewModelScope
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.mvi.base.BaseViewModel
import com.louis.tham.financetracker.core.mvi.contracts.TransactionDetailEffect
import com.louis.tham.financetracker.core.mvi.contracts.TransactionDetailIntent
import com.louis.tham.financetracker.core.mvi.contracts.TransactionDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(private val transactionRepository: TransactionRepository) :
    BaseViewModel<TransactionDetailState, TransactionDetailIntent, TransactionDetailEffect>() {

    override fun createInitialState() = TransactionDetailState()

    override fun handleIntent(intent: TransactionDetailIntent) {
        when (intent) {
            is TransactionDetailIntent.OnGetTransactionById -> {
                queryTransactionById(intent.id)
            }
        }
    }

    private fun queryTransactionById(id: String) {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            transactionRepository.getTransactionById(id).collect {
                setState {
                    copy(isLoading = false, transaction = it)
                }
            }
        }
    }


}