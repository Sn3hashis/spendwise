package me.sm.spendwise.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NavigationState {
    var currentScreen by mutableStateOf("Home")
    var currentExpenseId: String? = null
}

