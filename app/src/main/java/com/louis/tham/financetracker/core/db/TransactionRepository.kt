package com.louis.tham.financetracker.core.db

import androidx.room.*
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    // CREATE
    suspend fun insertTransaction(title: String, amount: Double, category: String, date: String) {
        val newTransaction = TransactionEntity(
            title = title,
            amount = amount,
            category = category,
            date = date
        )
        transactionDao.insertTransaction(newTransaction)
    }

    // READ (No 'ResultsChange' wrapper needed! Just raw lists)
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    // UPDATE
    suspend fun updateTransaction(id: Long, title: String, amount: Double, category: String) {
        val updatedTransaction = TransactionEntity(
            id = id,
            title = title,
            amount = amount,
            category = category
        )
        transactionDao.updateTransaction(updatedTransaction)
    }

    // DELETE
    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }
}

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date ASC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}