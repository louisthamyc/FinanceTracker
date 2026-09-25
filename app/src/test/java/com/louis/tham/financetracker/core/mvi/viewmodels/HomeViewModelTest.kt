package com.louis.tham.financetracker.core.mvi.viewmodels

import app.cash.turbine.test
import com.louis.tham.financetracker.core.db.TransactionRepository
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.mvi.contracts.HomeIntent
import com.louis.tham.financetracker.fakes.FakeTransactionDao
import com.louis.tham.financetracker.utils.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepository

    @Before
    fun setUp() {
        fakeDao = FakeTransactionDao()
        repository = TransactionRepository(fakeDao)
    }

    @Test
    fun init_loadsTransactionsAndSetsLoadingFalse() = runTest {
        val sampleTransactions = listOf(
            TransactionEntity(id = 1, title = "Groceries", amount = 85.0, type = TransactionType.EXPENSE.name),
            TransactionEntity(id = 2, title = "Salary", amount = 3000.0, type = TransactionType.INCOME.name)
        )
        fakeDao.setInitialTransactions(sampleTransactions)

        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.transactionList.size)
            assertEquals("Groceries", state.transactionList[0].title)
            assertEquals("Salary", state.transactionList[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun init_withEmptyDatabase_setsEmptyList() = runTest {
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(emptyList<TransactionEntity>(), state.transactionList)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun onGetAllTransactionIntent_triggersQueryAndUpdatesList() = runTest {
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.transactionList.size)

            fakeDao.insertTransaction(
                TransactionEntity(id = 1, title = "Electric Bill", amount = 95.0, type = TransactionType.EXPENSE.name)
            )

            viewModel.sendIntent(HomeIntent.OnGetAllTransaction)

            val updatedState = awaitItem()
            assertEquals(1, updatedState.transactionList.size)
            assertEquals("Electric Bill", updatedState.transactionList[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun repositoryFlowEmission_automaticallyUpdatesHomeState() = runTest {
        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(0, initial.transactionList.size)

            // Insert new transaction to repository
            repository.insertTransaction(
                title = "Freelance",
                amount = 500.0,
                category = "Other",
                type = TransactionType.INCOME.name,
                date = "2026-03-24",
                note = ""
            )

            val updated = awaitItem()
            assertEquals(1, updated.transactionList.size)
            assertEquals("Freelance", updated.transactionList[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
