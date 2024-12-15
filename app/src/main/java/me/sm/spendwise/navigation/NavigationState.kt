package me.sm.spendwise.navigation

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.data.Payee

object NavigationState {
    private var navController: NavController? = null
    var currentScreen by mutableStateOf<Screen?>(null)
        private set
    var currentExpenseId by mutableStateOf<String?>(null)
    var payeeToEdit by mutableStateOf<Payee?>(null)

    fun setNavController(controller: NavController) {
        navController = controller
    }

    fun navigateTo(screen: Screen) {
        Log.d("NavigationState", "Navigating to: ${screen.route}")
        navController?.navigate(screen.route)
        currentScreen = screen
    }

    fun navigateBack() {
        Log.d("NavigationState", "Navigating back from: ${currentScreen?.route}")
        navController?.popBackStack()
        // Update current screen based on back navigation
        currentScreen = when (currentScreen) {
            Screen.Settings -> Screen.Profile
            Screen.ManagePayee -> Screen.Profile
            Screen.Currency -> Screen.Settings
            Screen.Theme -> Screen.Settings
            Screen.Language -> Screen.Settings
            Screen.Security -> Screen.Settings
            Screen.Notifications -> Screen.Settings
            Screen.Haptics -> Screen.Settings
            Screen.ExpenseDetails, Screen.AttachmentOptions -> Screen.Expense
            Screen.TransactionFilter -> Screen.Home
            Screen.NotificationView -> Screen.Home
            Screen.IncomeCategoryScreen -> Screen.Income
            Screen.ExpenseCategoryScreen -> Screen.Expense
            Screen.AddNewPayee -> Screen.ManagePayee
            Screen.SecuritySetup -> Screen.Security
            else -> Screen.Home
        }
    }

    fun clearNavController() {
        navController = null
        currentScreen = null
        currentExpenseId = null
        payeeToEdit = null
    }
}