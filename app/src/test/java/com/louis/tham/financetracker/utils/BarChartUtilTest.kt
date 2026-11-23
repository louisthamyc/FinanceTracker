package com.louis.tham.financetracker.utils

import java.math.BigDecimal
import com.louis.tham.financetracker.core.models.constants.TransactionType
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BarChartUtilTest {

    @Test
    fun getSignedAmount_forIncome_returnsPositiveValue() {
        val uppercaseIncome = TransactionEntity(amount = BigDecimal("250.0"), type = "INCOME")
        val lowercaseIncome = TransactionEntity(amount = BigDecimal("150.0"), type = "income")
        val enumIncome = TransactionEntity(amount = BigDecimal("300.0"), type = TransactionType.INCOME.name)

        assertEquals(BigDecimal("250.0"), BarChartUtil.getSignedAmount(uppercaseIncome))
        assertEquals(BigDecimal("150.0"), BarChartUtil.getSignedAmount(lowercaseIncome))
        assertEquals(BigDecimal("300.0"), BarChartUtil.getSignedAmount(enumIncome))
    }

    @Test
    fun getSignedAmount_forExpense_returnsNegativeValue() {
        val uppercaseExpense = TransactionEntity(amount = BigDecimal("100.0"), type = "EXPENSE")
        val lowercaseExpense = TransactionEntity(amount = BigDecimal("75.0"), type = "expense")
        val enumExpense = TransactionEntity(amount = BigDecimal("50.0"), type = TransactionType.EXPENSE.name)

        assertEquals(BigDecimal("-100.0"), BarChartUtil.getSignedAmount(uppercaseExpense))
        assertEquals(BigDecimal("-75.0"), BarChartUtil.getSignedAmount(lowercaseExpense))
        assertEquals(BigDecimal("-50.0"), BarChartUtil.getSignedAmount(enumExpense))
    }

    @Test
    fun getSignedAmount_forUnknownType_handlesNegativeAndPositiveAmounts() {
        val unknownNegative = TransactionEntity(amount = BigDecimal("-45.0"), type = "OTHER")
        val unknownPositive = TransactionEntity(amount = BigDecimal("80.0"), type = "OTHER")

        assertEquals(BigDecimal("-45.0"), BarChartUtil.getSignedAmount(unknownNegative))
        assertEquals(BigDecimal("-80.0"), BarChartUtil.getSignedAmount(unknownPositive))
    }

    @Test
    fun formatAmountLabel_formatsCorrectlyForVariousValues() {
        assertEquals("0", BarChartUtil.formatAmountLabel(0.0))
        assertEquals("+500", BarChartUtil.formatAmountLabel(500.0))
        assertEquals("-250", BarChartUtil.formatAmountLabel(-250.0))
        assertEquals("+1.5k", BarChartUtil.formatAmountLabel(1500.0))
        assertEquals("-2.4k", BarChartUtil.formatAmountLabel(-2400.0))
        assertEquals("+1.0k", BarChartUtil.formatAmountLabel(1000.0))
    }

    @Test
    fun formatYLabel_formatsCorrectlyForVariousValues() {
        assertEquals("0", BarChartUtil.formatYLabel(0.0))
        assertEquals("0", BarChartUtil.formatYLabel(0.0005))
        assertEquals("+100", BarChartUtil.formatYLabel(100.0))
        assertEquals("-350", BarChartUtil.formatYLabel(-350.0))
        assertEquals("+3.2k", BarChartUtil.formatYLabel(3200.0))
        assertEquals("-4.5k", BarChartUtil.formatYLabel(-4500.0))
    }

    @Test
    fun groupTransactionsByMonth_withEmptyList_returnsEmpty() {
        val result = BarChartUtil.groupTransactionsByMonth(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun groupTransactionsByMonth_groupsAndSumsByMonth() {
        val transactions = listOf(
            TransactionEntity(date = "2026-01-05", amount = BigDecimal("1000.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-01-20", amount = BigDecimal("200.0"), type = TransactionType.EXPENSE.name),
            TransactionEntity(date = "2026-02-10", amount = BigDecimal("500.0"), type = TransactionType.EXPENSE.name)
        )

        val result = BarChartUtil.groupTransactionsByMonth(transactions)

        assertEquals(2, result.size)
        // 2026-01: 1000 - 200 = 800
        assertEquals("2026-01", result[0].first)
        assertEquals(BigDecimal("800.0"), result[0].second)

        // 2026-02: -500
        assertEquals("2026-02", result[1].first)
        assertEquals(BigDecimal("-500.0"), result[1].second)
    }

    @Test
    fun groupTransactionsByMonth_sumsFractionalAmountsExactly() {
        val transactions = listOf(
            TransactionEntity(date = "2026-06-01", amount = BigDecimal("0.10"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-06-02", amount = BigDecimal("0.20"), type = TransactionType.INCOME.name)
        )

        val result = BarChartUtil.groupTransactionsByMonth(transactions)

        assertEquals(BigDecimal("0.30"), result.single().second)
    }

    @Test
    fun groupTransactionsByMonth_limitsToLatestThreeMonthsSorted() {
        val transactions = listOf(
            TransactionEntity(date = "2026-01-10", amount = BigDecimal("100.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-02-10", amount = BigDecimal("200.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-03-10", amount = BigDecimal("300.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-04-10", amount = BigDecimal("400.0"), type = TransactionType.INCOME.name),
            TransactionEntity(date = "2026-05-10", amount = BigDecimal("500.0"), type = TransactionType.INCOME.name)
        )

        val result = BarChartUtil.groupTransactionsByMonth(transactions)

        assertEquals(3, result.size)
        assertEquals("2026-03", result[0].first)
        assertEquals(BigDecimal("300.0"), result[0].second)
        assertEquals("2026-04", result[1].first)
        assertEquals(BigDecimal("400.0"), result[1].second)
        assertEquals("2026-05", result[2].first)
        assertEquals(BigDecimal("500.0"), result[2].second)
    }

    @Test
    fun groupTransactionsByMonth_handlesShortDatesWithoutCrashing() {
        val transactions = listOf(
            TransactionEntity(date = "2026", amount = BigDecimal("50.0"), type = TransactionType.INCOME.name)
        )

        val result = BarChartUtil.groupTransactionsByMonth(transactions)
        assertEquals(1, result.size)
        assertEquals("2026", result[0].first)
        assertEquals(BigDecimal("50.0"), result[0].second)
    }
}
