package me.sm.spendwise.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object NavigationState {
    var currentScreen by mutableStateOf("Home")
    var currentExpenseId: String? = null
    var previousScreen: String? = null

    fun navigateTo(screen: String) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    fun navigateBack() {
        currentScreen = when (currentScreen) {
            "Settings" -> "Profile"
            else -> "Home"
        }
    }

    fun navigateToLogin() {
        currentScreen = "Login"
        // Reset any user-specific state here if needed
    }
}

