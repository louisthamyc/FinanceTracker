package com.louis.tham.financetracker.ui.screens

import java.math.BigDecimal
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeScreenLogicTest {

    @Test
    fun calculateCurrentMonthTotal_withEmptyList_returnsZero() {
        val total = calculateCurrentMonthTotal(emptyList(), "2026-03")
        assertEquals(BigDecimal.ZERO, total)
    }

    @Test
    fun calculateCurrentMonthTotal_filtersCurrentMonthAndCalculatesNet() {
        val targetMonth = "2026-03"
        val transactions = listOf(
            TransactionEntity(date = "2026-03-01", amount = BigDecimal("2000.20"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-03-15", amount = BigDecimal("450.10"), type = TransactionType.EXPENSE.name),
            TransactionEntity(date = "2026-03-20", amount = BigDecimal("150.20"), type = TransactionType.EXPENSE.name),
            // Other month - should be ignored:
            TransactionEntity(date = "2026-02-28", amount = BigDecimal("1000.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-04-01", amount = BigDecimal("800.0"), type = TransactionType.EXPENSE.name)
        )

        // Target month: 2000.20 - 450.10 - 150.20 = 1399.90
        val total = calculateCurrentMonthTotal(transactions, targetMonth)
        assertEquals(BigDecimal("1399.90"), total)
    }

    @Test
    fun calculateCurrentMonthTotal_withOnlyExpensesInMonth_returnsNegativeNet() {
        val targetMonth = "2026-03"
        val transactions = listOf(
            TransactionEntity(date = "2026-03-05", amount = BigDecimal("80.0"), type = TransactionType.EXPENSE.name),
            TransactionEntity(date = "2026-03-10", amount = BigDecimal("120.0"), type = TransactionType.EXPENSE.name)
        )

        val total = calculateCurrentMonthTotal(transactions, targetMonth)
        assertEquals(BigDecimal("-200.0"), total)
    }

    @Test
    fun calculateCurrentMonthTotal_withNoMatchingMonthTransactions_returnsZero() {
        val transactions = listOf(
            TransactionEntity(date = "2025-12-15", amount = BigDecimal("500.0"), type = TransactionType.INCOME.name)
        )

        val total = calculateCurrentMonthTotal(transactions, "2026-01")
        assertEquals(BigDecimal.ZERO, total)
    }
}
