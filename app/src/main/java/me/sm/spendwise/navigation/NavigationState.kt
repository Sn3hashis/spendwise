package me.sm.spendwise.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.sm.spendwise.data.Payee

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
    ManagePayee,
    Security,
    Notifications,
    About,
    AddNewPayee,
    NotificationView,
    SecuritySetup,

}

object NavigationState {
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set
    var currentExpenseId by mutableStateOf<String?>(null)
    var payeeToEdit by mutableStateOf<Payee?>(null)
    // ... other properties

    fun navigateTo(screen: Screen) {
        Log.d("NavigationState", "Navigating to: $screen from: $currentScreen")
        currentScreen = screen
    }

    fun navigateBack() {
        Log.d("NavigationState", "Navigating back from: $currentScreen")
        currentScreen = when (currentScreen) {
            Screen.Settings -> Screen.Profile
            Screen.ManagePayee -> Screen.Profile
            Screen.Currency -> Screen.Settings
            Screen.Theme -> Screen.Settings
            Screen.Language -> Screen.Settings
            Screen.Security -> Screen.Settings
            Screen.Notifications -> Screen.Settings
            Screen.ExpenseDetails, Screen.AttachmentOptions -> Screen.Expense
            Screen.TransactionFilter -> Screen.Home
            Screen.NotificationView -> Screen.Home
            Screen.IncomeCategoryScreen -> Screen.IncomeScreen
            Screen.ExpenseCategoryScreen -> Screen.ExpenseScreen
            Screen.AddNewPayee -> Screen.ManagePayee
            Screen.SecuritySetup -> Screen.Security
            Screen.Security -> Screen.Settings
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