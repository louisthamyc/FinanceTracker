package com.louis.tham.financetracker.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.louis.tham.financetracker.core.models.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}