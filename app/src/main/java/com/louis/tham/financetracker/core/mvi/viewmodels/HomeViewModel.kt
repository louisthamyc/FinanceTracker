package com.louis.tham.financetracker.core.mvi.viewmodels

import androidx.lifecycle.viewModelScope
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.mvi.base.BaseViewModel
import com.louis.tham.financetracker.core.mvi.contracts.HomeEffect
import com.louis.tham.financetracker.core.mvi.contracts.HomeIntent
import com.louis.tham.financetracker.core.mvi.contracts.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val transactionRepository: TransactionRepository) :
    BaseViewModel<HomeState, HomeIntent, HomeEffect>() {

    override fun createInitialState() = HomeState()

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnGetAllTransaction -> {
                queryAllTransaction()
            }
        }
    }

    init {
        queryAllTransaction()
    }

    private fun queryAllTransaction() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect {
                setState {
                    copy(isLoading = false, transactionList = it)
                }
            }
        }
    }


}