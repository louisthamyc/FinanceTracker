package com.louis.tham.financetracker.core.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.louis.tham.financetracker.core.models.constants.TransactionType


@Entity(tableName = "transactions")
class TransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

fun List<TransactionEntity>.calculateTotalNetBalance(): Double {
    return sumOf { transaction ->
        val netAmount: Double = when (transaction.type) {
            TransactionType.INCOME.name -> transaction.amount
            TransactionType.EXPENSE.name -> -transaction.amount
            else -> 0.0
        }
        netAmount
    }
}