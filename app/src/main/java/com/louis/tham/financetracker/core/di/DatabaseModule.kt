package com.louis.tham.financetracker.core.di

import android.content.Context
import androidx.room.Room
import com.louis.tham.financetracker.core.db.AppDatabase
import com.louis.tham.financetracker.core.db.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "finance_database.db"
        ).build()
    }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}