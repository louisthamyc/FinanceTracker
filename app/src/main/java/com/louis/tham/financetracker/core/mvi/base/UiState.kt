package com.louis.tham.financetracker.core.mvi.base

// Represents the current state of the UI (e.g., Loading, Success, Error)
interface UiState

// Represents user actions or system events intent to change the state
interface UiIntent

// Represents one-time side-effects (e.g., Navigating, showing a Toast/Snackbar)
interface UiEffect