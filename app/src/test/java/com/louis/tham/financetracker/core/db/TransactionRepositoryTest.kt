package com.louis.tham.financetracker.core.db

import app.cash.turbine.test
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.fakes.FakeTransactionDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransactionRepositoryTest {

    private lateinit var fakeDao: FakeTransactionDao
    private lateinit var repository: TransactionRepository

    @Before
    fun setUp() {
        fakeDao = FakeTransactionDao()
        repository = TransactionRepository(fakeDao)
    }

    @Test
    fun insertTransaction_savesTransactionWithCorrectFields() = runTest {
        repository.insertTransaction(
            title = "Salary",
            amount = 5000.0,
            category = "Income",
            type = TransactionType.INCOME.name,
            date = "2026-03-01",
            note = "Monthly salary"
        )

        repository.getAllTransactions().test {
            val list = awaitItem()
            assertEquals(1, list.size)
            val item = list[0]
            assertEquals("Salary", item.title)
            assertEquals(5000.0, item.amount, 0.001)
            assertEquals("Income", item.category)
            assertEquals(TransactionType.INCOME.name, item.type)
            assertEquals("2026-03-01", item.date)
            assertEquals("Monthly salary", item.note)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getAllTransactions_emitsUpdatesWhenNewTransactionsInserted() = runTest {
        repository.getAllTransactions().test {
            assertEquals(emptyList<TransactionEntity>(), awaitItem())

            repository.insertTransaction(
                title = "Coffee",
                amount = 4.50,
                category = "Food",
                type = TransactionType.EXPENSE.name,
                date = "2026-03-02",
                note = ""
            )

            val updatedList = awaitItem()
            assertEquals(1, updatedList.size)
            assertEquals("Coffee", updatedList[0].title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getTransactionById_returnsCorrectTransaction() = runTest {
        fakeDao.setInitialTransactions(
            listOf(
                TransactionEntity(id = 10, title = "Rent", amount = 1200.0, type = TransactionType.EXPENSE.name),
                TransactionEntity(id = 20, title = "Bonus", amount = 800.0, type = TransactionType.INCOME.name)
            )
        )

        repository.getTransactionById("10").test {
            val item = awaitItem()
            assertEquals(10L, item.id)
            assertEquals("Rent", item.title)
            assertEquals(1200.0, item.amount, 0.001)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun updateTransaction_updatesFieldsSuccessfully() = runTest {
        fakeDao.setInitialTransactions(
            listOf(
                TransactionEntity(id = 1, title = "Dinner", amount = 30.0, category = "Food", type = "EXPENSE")
            )
        )

        repository.updateTransaction(
            id = 1,
            title = "Fancy Dinner",
            amount = 65.0,
            category = "Dining",
            type = "EXPENSE"
        )

        repository.getTransactionById("1").test {
            val updated = awaitItem()
            assertEquals("Fancy Dinner", updated.title)
            assertEquals(65.0, updated.amount, 0.001)
            assertEquals("Dining", updated.category)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun deleteTransaction_removesTransactionFromDatabase() = runTest {
        fakeDao.setInitialTransactions(
            listOf(
                TransactionEntity(id = 1, title = "Ticket", amount = 15.0)
            )
        )

        repository.deleteTransaction(1)

        repository.getAllTransactions().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
