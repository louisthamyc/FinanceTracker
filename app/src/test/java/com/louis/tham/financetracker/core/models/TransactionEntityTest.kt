package com.louis.tham.financetracker.core.models

import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import com.louis.tham.financetracker.core.models.entity.calculateTotalNetBalance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TransactionEntityTest {

    @Test
    fun defaultConstructor_setsExpectedDefaults() {
        val transaction = TransactionEntity()

        assertEquals(0L, transaction.id)
        assertEquals("", transaction.title)
        assertEquals(0.0, transaction.amount, 0.001)
        assertEquals("", transaction.category)
        assertEquals("", transaction.type)
        assertEquals("", transaction.date)
        assertEquals("", transaction.note)
        assertNotNull(transaction.timestamp)
    }

    @Test
    fun customConstructor_setsCustomValues() {
        val transaction = TransactionEntity(
            id = 42L,
            title = "Groceries",
            amount = 125.50,
            category = "Food",
            type = TransactionType.EXPENSE.name,
            date = "2026-03-15",
            timestamp = 1710500000000L,
            note = "Weekly supermarket shopping"
        )

        assertEquals(42L, transaction.id)
        assertEquals("Groceries", transaction.title)
        assertEquals(125.50, transaction.amount, 0.001)
        assertEquals("Food", transaction.category)
        assertEquals(TransactionType.EXPENSE.name, transaction.type)
        assertEquals("2026-03-15", transaction.date)
        assertEquals(1710500000000L, transaction.timestamp)
        assertEquals("Weekly supermarket shopping", transaction.note)
    }

    @Test
    fun calculateTotalNetBalance_withEmptyList_returnsZero() {
        val transactions = emptyList<TransactionEntity>()
        val netBalance = transactions.calculateTotalNetBalance()

        assertEquals(0.0, netBalance, 0.001)
    }

    @Test
    fun calculateTotalNetBalance_withIncomeOnly_returnsPositiveSum() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = 1000.0, type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = 500.50, type = TransactionType.INCOME.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(1500.50, netBalance, 0.001)
    }

    @Test
    fun calculateTotalNetBalance_withExpenseOnly_returnsNegativeSum() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = 200.0, type = TransactionType.EXPENSE.name),
            TransactionEntity(id = 2, amount = 50.25, type = TransactionType.EXPENSE.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(-250.25, netBalance, 0.001)
    }

    @Test
    fun calculateTotalNetBalance_withMixedIncomeAndExpense_returnsCorrectNet() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = 3000.0, type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = 1200.0, type = TransactionType.EXPENSE.name),
            TransactionEntity(id = 3, amount = 500.0, type = TransactionType.INCOME.name),
            TransactionEntity(id = 4, amount = 300.0, type = TransactionType.EXPENSE.name)
        )

        // 3000 - 1200 + 500 - 300 = 2000
        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(2000.0, netBalance, 0.001)
    }

    @Test
    fun calculateTotalNetBalance_withUnknownOrEmptyType_ignoresUnrecognizedTypes() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = 100.0, type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = 50.0, type = "UNKNOWN_TYPE"),
            TransactionEntity(id = 3, amount = 25.0, type = "")
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(100.0, netBalance, 0.001)
    }

    @Test
    fun calculateTotalNetBalance_handlesDecimalsCorrectly() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = 10.99, type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = 5.49, type = TransactionType.EXPENSE.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(5.50, netBalance, 0.001)
    }
}
