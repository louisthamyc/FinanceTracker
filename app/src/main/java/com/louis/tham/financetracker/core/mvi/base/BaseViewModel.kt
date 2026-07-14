package com.louis.tham.financetracker.core.mvi.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<State: UiState, Intent: UiIntent, Effect: UiEffect>: ViewModel() {
    // 1. Initial State
    abstract fun createInitialState(): State

    // 2. Read-Only State for the UI
    private val _uiState: MutableStateFlow<State> by lazy { MutableStateFlow(createInitialState()) }
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    // 3. Intents pipeline
    private val _intent: MutableSharedFlow<Intent> = MutableSharedFlow()

    // 4. One-time side-effects pipeline (Channels are perfect because they ensure single-delivery)
    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    val currentState: State
        get() = uiState.value

    init {
        subscribeIntents()
    }

    // Handle incoming UI actions
    fun sendIntent(intent: Intent) {
        viewModelScope.launch { _intent.emit(intent) }
    }

    private fun subscribeIntents() {
        viewModelScope.launch {
            _intent.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    // Features must override this to handle their specific intents
    protected abstract fun handleIntent(intent: Intent)

    // Helper to update the state safely
    protected fun setState(reduce: State.() -> State) {
        val newState = currentState.reduce()
        _uiState.value = newState
    }

    // Helper to trigger a side-effect
    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.send(effectValue) }
    }
}