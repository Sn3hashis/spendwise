package me.sm.spendwise.data

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.sm.spendwise.ui.screens.NotificationItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object NotificationManager {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    fun addTransactionNotification(type: String, amount: String, category: String) {
        val now = LocalDateTime.now()
        val newNotification = NotificationItem(
            title = "New $type Transaction",
            subtitle = "Added $amount in $category category",
            time = now.format(DateTimeFormatter.ofPattern("HH:mm")),
            date = now.format(DateTimeFormatter.ofPattern("dd MMM "))
        )
        _notifications.value = listOf(newNotification) + _notifications.value
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearAll() {
        _notifications.value = emptyList()
    }
}

