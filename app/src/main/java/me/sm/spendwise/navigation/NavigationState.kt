package me.sm.spendwise.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class Screen {
    Home,
    Expense,
    Income,
    Profile,
    Settings,
    Currency,
    Theme,
    Login,
    ExpenseDetails,
    AttachmentOptions,
    TransactionFilter,
    Notification,
    Language,
    Transfer,
    Transaction,
    Budget,
    Security,
    Notifications,
    About
}

object NavigationState {
    var currentScreen by mutableStateOf(Screen.Home)
    var currentExpenseId: String? = null

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun navigateBack() {
        currentScreen = when (currentScreen) {
            Screen.Settings,   -> Screen.Profile
            Screen.ExpenseDetails, Screen.AttachmentOptions -> Screen.Expense
            Screen.TransactionFilter -> Screen.Home
            Screen.Notification -> Screen.Settings
            Screen.Currency,Screen.Theme, Screen.Language -> Screen.Settings
            else -> Screen.Home
        }
    }

    fun navigateToLogin() {
        currentScreen = Screen.Login
    }
}
