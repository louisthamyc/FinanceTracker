package com.louis.tham.financetracker.core.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.louis.tham.financetracker.core.models.constants.TransactionType
import java.math.BigDecimal


@Entity(tableName = "transactions")
class TransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    val category: String = "",
    val type: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

fun List<TransactionEntity>.calculateTotalNetBalance(): BigDecimal {
    return sumOf { transaction ->
        val netAmount: BigDecimal = when (transaction.type) {
            TransactionType.INCOME.name -> transaction.amount
            TransactionType.EXPENSE.name -> transaction.amount.negate()
            else -> BigDecimal.ZERO
        }
        netAmount
    }
}
