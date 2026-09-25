package com.louis.tham.financetracker.core.mvi.viewmodels

import androidx.lifecycle.viewModelScope
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.mvi.base.BaseViewModel
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionEffect
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionIntent
import com.louis.tham.financetracker.core.mvi.contracts.AddTransactionState
import com.louis.tham.financetracker.core.mvi.contracts.TransactionField
import com.louis.tham.financetracker.utils.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : BaseViewModel<AddTransactionState, AddTransactionIntent, AddTransactionEffect>() {

    override fun createInitialState(): AddTransactionState = AddTransactionState()

    override fun handleIntent(intent: AddTransactionIntent) {
        when (intent) {
            is AddTransactionIntent.OnTitleChanged -> {
                setState {
                    copy(
                        title = intent.title,
                        invalidFields = invalidFields - TransactionField.TITLE,
                        errorMessage = null
                    )
                }
            }

            is AddTransactionIntent.OnAmountChanged -> {
                if (intent.amount.all { it.isDigit() }) {
                    setState {
                        copy(
                            amount = intent.amount,
                            invalidFields = invalidFields - TransactionField.AMOUNT,
                            errorMessage = null
                        )
                    }
                } else {
                    setState {
                        copy(
                            invalidFields = invalidFields + TransactionField.AMOUNT,
                            errorMessage = null
                        )
                    }
                }
            }

            is AddTransactionIntent.OnCategoryChanged -> {
                setState {
                    copy(
                        category = intent.category,
                        invalidFields = invalidFields - TransactionField.CATEGORY,
                        errorMessage = null
                    )
                }
            }

            is AddTransactionIntent.OnDateTimeClicked -> {
                setState {
                    copy(showDatePicker = true)
                }
            }

            is AddTransactionIntent.OnNoteChanged -> {
                setState {
                    copy(note = intent.note, errorMessage = null)
                }
            }

            is AddTransactionIntent.OnTypeSelected -> {
                setState {
                    copy(
                        type = intent.type,
                        errorMessage = null
                    )
                }
            }

            is AddTransactionIntent.OnSaveClicked -> {
                saveTransaction()
            }
            is AddTransactionIntent.OnDismissError -> {
                setState {
                    copy(errorMessage = null)
                }
            }
            is AddTransactionIntent.OnDateSelected -> {
                setState {
                    copy(
                        showDatePicker = false,
                        date = DateUtil.convertMillisToString(intent.timestamp)
                    )
                }
            }
            is AddTransactionIntent.OnDismissDatePicker -> {
                setState {
                    copy(
                        showDatePicker = false
                    )
                }
            }
        }
    }

    private fun saveTransaction() {
        val errors = mutableSetOf<TransactionField>()

        if (currentState.title.trim().isEmpty()) {
            errors.add(TransactionField.TITLE)
        }

        if (currentState.amount.trim().isEmpty() || currentState.amount.toDoubleOrNull() == 0.0) {
            errors.add(TransactionField.AMOUNT)
        }

        if (currentState.category.trim().isEmpty()) {
            errors.add(TransactionField.CATEGORY)
        }

        if (errors.isNotEmpty()) {
            setState {
                copy(
                    invalidFields = errors
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            try {
                transactionRepository.insertTransaction(
                    title = currentState.title,
                    amount = currentState.amount.toDouble(),
                    category = currentState.category,
                    date = currentState.date,
                    type = currentState.type.name,
                    note = currentState.note
                )
                setEffect {
                    AddTransactionEffect.NavigateBack
                }
            } catch (e: Exception) {
                onError(e.localizedMessage)
            }
        }
    }

    private fun onError(e: String?) {
        setState {
            copy(
                isLoading = false,
                errorMessage = e
            )
        }
    }
}