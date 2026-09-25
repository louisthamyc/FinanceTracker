package com.louis.tham.financetracker.fakes

import com.louis.tham.financetracker.core.db.TransactionDao
import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeTransactionDao : TransactionDao {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())

    var shouldThrowError: Boolean = false
    var errorToThrow: Exception = RuntimeException("Database error")

    private var currentId: Long = 1L

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        if (shouldThrowError) {
            throw errorToThrow
        }
        val currentList = _transactions.value.toMutableList()
        val finalTransaction = if (transaction.id == 0L) {
            TransactionEntity(
                id = currentId++,
                title = transaction.title,
                amount = transaction.amount,
                category = transaction.category,
                type = transaction.type,
                date = transaction.date,
                timestamp = transaction.timestamp,
                note = transaction.note
            )
        } else {
            transaction
        }
        currentList.removeAll { it.id == finalTransaction.id }
        currentList.add(finalTransaction)
        _transactions.value = currentList.sortedByDescending { it.date }
    }

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return _transactions.asStateFlow()
    }

    override fun getTransactionById(id: String): Flow<TransactionEntity> {
        val longId = id.toLongOrNull()
        return _transactions.map { list ->
            list.firstOrNull { it.id == longId } ?: TransactionEntity()
        }
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        if (shouldThrowError) {
            throw errorToThrow
        }
        val currentList = _transactions.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == transaction.id }
        if (index != -1) {
            currentList[index] = transaction
            _transactions.value = currentList.sortedByDescending { it.date }
        }
    }

    override suspend fun deleteTransactionById(id: Long) {
        if (shouldThrowError) {
            throw errorToThrow
        }
        val currentList = _transactions.value.toMutableList()
        currentList.removeAll { it.id == id }
        _transactions.value = currentList
    }

    fun setInitialTransactions(list: List<TransactionEntity>) {
        _transactions.value = list.sortedByDescending { it.date }
        val maxId = list.maxOfOrNull { it.id } ?: 0L
        currentId = maxId + 1
    }
}
