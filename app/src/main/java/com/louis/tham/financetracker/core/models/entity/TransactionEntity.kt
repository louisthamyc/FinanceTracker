package com.louis.tham.financetracker.core.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
class TransactionEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "",
    val timestamp: Long = System.currentTimeMillis()
)