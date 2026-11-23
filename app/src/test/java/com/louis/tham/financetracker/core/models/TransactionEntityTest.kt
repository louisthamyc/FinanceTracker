package com.louis.tham.financetracker.core.models

import java.math.BigDecimal
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
        assertEquals(BigDecimal.ZERO, transaction.amount)
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
            amount = BigDecimal("125.50"),
            category = "Food",
            type = TransactionType.EXPENSE.name,
            date = "2026-03-15",
            timestamp = 1710500000000L,
            note = "Weekly supermarket shopping"
        )

        assertEquals(42L, transaction.id)
        assertEquals("Groceries", transaction.title)
        assertEquals(BigDecimal("125.50"), transaction.amount)
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

        assertEquals(BigDecimal.ZERO, netBalance)
    }

    @Test
    fun calculateTotalNetBalance_withIncomeOnly_returnsPositiveSum() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = BigDecimal("1000.0"), type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = BigDecimal("500.50"), type = TransactionType.INCOME.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(BigDecimal("1500.50"), netBalance)
    }

    @Test
    fun calculateTotalNetBalance_withExpenseOnly_returnsNegativeSum() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = BigDecimal("200.0"), type = TransactionType.EXPENSE.name),
            TransactionEntity(id = 2, amount = BigDecimal("50.25"), type = TransactionType.EXPENSE.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(BigDecimal("-250.25"), netBalance)
    }

    @Test
    fun calculateTotalNetBalance_withMixedIncomeAndExpense_returnsCorrectNet() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = BigDecimal("3000.0"), type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = BigDecimal("1200.0"), type = TransactionType.EXPENSE.name),
            TransactionEntity(id = 3, amount = BigDecimal("500.0"), type = TransactionType.INCOME.name),
            TransactionEntity(id = 4, amount = BigDecimal("300.0"), type = TransactionType.EXPENSE.name)
        )

        // 3000 - 1200 + 500 - 300 = 2000
        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(BigDecimal("2000.0"), netBalance)
    }

    @Test
    fun calculateTotalNetBalance_withUnknownOrEmptyType_ignoresUnrecognizedTypes() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = BigDecimal("100.0"), type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = BigDecimal("50.0"), type = "UNKNOWN_TYPE"),
            TransactionEntity(id = 3, amount = BigDecimal("25.0"), type = "")
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(BigDecimal("100.0"), netBalance)
    }

    @Test
    fun calculateTotalNetBalance_handlesDecimalsCorrectly() {
        val transactions = listOf(
            TransactionEntity(id = 1, amount = BigDecimal("10.99"), type = TransactionType.INCOME.name),
            TransactionEntity(id = 2, amount = BigDecimal("5.49"), type = TransactionType.EXPENSE.name)
        )

        val netBalance = transactions.calculateTotalNetBalance()
        assertEquals(BigDecimal("5.50"), netBalance)
    }
}
