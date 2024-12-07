package me.sm.spendwise.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.sm.spendwise.navigation.NavigationState.currentScreen

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
    ExpenseScreen,
    IncomeScreen,
    ExpenseCategoryScreen,
    IncomeCategoryScreen,
    Language,
    Transfer,
    Transaction,
    Budget,
    Security,
    Notifications,
    About,
    NotificationView
}

object NavigationState {
    var currentScreen by mutableStateOf(Screen.Home)
    var currentExpenseId: String? = null

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }
fun navigateBack() {
    currentScreen = when (currentScreen) {
        Screen.Settings -> Screen.Profile
        Screen.Currency -> Screen.Settings
        Screen.Theme -> Screen.Settings  
        Screen.Language -> Screen.Settings
        Screen.Security -> Screen.Settings
        Screen.Notifications -> Screen.Settings
        Screen.ExpenseDetails, Screen.AttachmentOptions -> Screen.Expense
        Screen.TransactionFilter -> Screen.Home
        Screen.NotificationView -> Screen.Home
        Screen.IncomeCategoryScreen ->Screen.IncomeScreen
        Screen.ExpenseCategoryScreen -> Screen.ExpenseScreen
        else -> Screen.Home
    }
}
    fun reset() {
        currentScreen = Screen.Home
    }
}



    // fun navigateToLogin() {
    //     currentScreen = Screen.Login
    // }

