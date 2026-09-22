package com.louis.tham.financetracker.core.nav

import com.louis.tham.financetracker.core.models.entity.TransactionEntity
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination
@Serializable
object AddTransactionDestination

@Serializable
data class TransactionDetailDestination(val transactionId: String)