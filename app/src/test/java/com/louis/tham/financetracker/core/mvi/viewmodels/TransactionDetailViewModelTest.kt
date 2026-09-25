package com.louis.tham.financetracker.core.mvi.viewmodels

import app.cash.turbine.test
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.contracts.TransactionDetailIntent
import com.louis.tham.financetracker.fakes.FakeTransactionDao
import com.louis.tham.financetracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TransactionDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepository
    private lateinit var viewModel: TransactionDetailViewModel

    @Before
    fun setUp() {
        fakeDao = FakeTransactionDao()
        repository = TransactionRepository(fakeDao)
        viewModel = TransactionDetailViewModel(repository)
    }

    @Test
    fun initialState_hasDefaultValues() {
        val state = viewModel.currentState
        assertFalse(state.isLoading)
        assertEquals(0L, state.transaction.id)
        assertEquals("", state.transaction.title)
    }

    @Test
    fun onGetTransactionById_loadsTransactionAndSetsLoadingFalse() = runTest {
        val targetTransaction = TransactionEntity(
            id = 99,
            title = "Gym Membership",
            amount = 60.0,
            category = "Entertainment",
            type = TransactionType.EXPENSE.name,
            date = "2026-03-01",
            note = "Annual subscription"
        )
        fakeDao.setInitialTransactions(listOf(targetTransaction))

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(0L, initial.transaction.id)

            viewModel.sendIntent(TransactionDetailIntent.OnGetTransactionById("99"))

            // State transition 1: loading
            val loadingState = awaitItem()
            org.junit.Assert.assertTrue(loadingState.isLoading)

            // State transition 2: loaded with transaction
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(99L, loadedState.transaction.id)
            assertEquals("Gym Membership", loadedState.transaction.title)
            assertEquals(60.0, loadedState.transaction.amount, 0.001)
            assertEquals("Entertainment", loadedState.transaction.category)
            assertEquals("Annual subscription", loadedState.transaction.note)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onGetTransactionById_whenNotFound_returnsEmptyTransaction() = runTest {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(TransactionDetailIntent.OnGetTransactionById("9999"))

            // State transition 1: loading
            val loadingState = awaitItem()
            org.junit.Assert.assertTrue(loadingState.isLoading)

            // State transition 2: loaded with default empty transaction
            val loadedState = awaitItem()
            assertFalse(loadedState.isLoading)
            assertEquals(0L, loadedState.transaction.id)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
