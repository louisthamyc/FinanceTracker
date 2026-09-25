package com.louis.tham.financetracker.ui.screens

import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeScreenLogicTest {

    @Test
    fun calculateCurrentMonthTotal_withEmptyList_returnsZero() {
        val total = calculateCurrentMonthTotal(emptyList(), "2026-03")
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun calculateCurrentMonthTotal_filtersCurrentMonthAndCalculatesNet() {
        val targetMonth = "2026-03"
        val transactions = listOf(
            TransactionEntity(date = "2026-03-01", amount = 2000.0, type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-03-15", amount = 450.0, type = TransactionType.EXPENSE.name),
            TransactionEntity(date = "2026-03-20", amount = 150.0, type = TransactionType.EXPENSE.name),
            // Other month - should be ignored:
            TransactionEntity(date = "2026-02-28", amount = 1000.0, type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-04-01", amount = 800.0, type = TransactionType.EXPENSE.name)
        )

        // Target month: 2000 - 450 - 150 = 1400
        val total = calculateCurrentMonthTotal(transactions, targetMonth)
        assertEquals(1400.0, total, 0.001)
    }

    @Test
    fun calculateCurrentMonthTotal_withOnlyExpensesInMonth_returnsNegativeNet() {
        val targetMonth = "2026-03"
        val transactions = listOf(
            TransactionEntity(date = "2026-03-05", amount = 80.0, type = TransactionType.EXPENSE.name),
            TransactionEntity(date = "2026-03-10", amount = 120.0, type = TransactionType.EXPENSE.name)
        )

        val total = calculateCurrentMonthTotal(transactions, targetMonth)
        assertEquals(-200.0, total, 0.001)
    }

    @Test
    fun calculateCurrentMonthTotal_withNoMatchingMonthTransactions_returnsZero() {
        val transactions = listOf(
            TransactionEntity(date = "2025-12-15", amount = 500.0, type = TransactionType.INCOME.name)
        )

        val total = calculateCurrentMonthTotal(transactions, "2026-01")
        assertEquals(0.0, total, 0.001)
    }
}
