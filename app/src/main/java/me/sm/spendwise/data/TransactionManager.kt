package me.sm.spendwise.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: String, // "Income", "Expense", "Transfer"
    val title: String,
    val category: String,
    val amount: String,
    val time: String,
    val date: String,
    val icon: Int,
    val backgroundColor: Color,
    val isIncome: Boolean = false
)

object TransactionManager {
    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> = _transactions

    fun addTransaction(transaction: Transaction) {
        _transactions.add(0, transaction)
    }

    fun getRecentTransactions(limit: Int = 10): List<Transaction> {
        return transactions.take(limit)
    }
}
