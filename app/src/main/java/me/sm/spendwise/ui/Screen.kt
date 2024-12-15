package me.sm.spendwise.ui

sealed class Screen(val route: String) {
    // Auth & Onboarding
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Verification : Screen("verification")
    object SecuritySetup : Screen("security_setup")
    object SecurityVerification : Screen("security_verification")
    object ForgotPassword : Screen("forgot_password")
    object ForgotPasswordSent : Screen("forgot_password_sent")
    
    // Main Navigation
    object Main : Screen("main")
    object Home : Screen("home")
    object Transaction : Screen("transaction")
    object Budget : Screen("budget")
    object Profile : Screen("profile")
    
    // Features
    object Expense : Screen("expense")
    object Income : Screen("income")
    object Transfer : Screen("transfer")
    object ExpenseDetails : Screen("expense_details")
    object Settings : Screen("settings")
    object Theme : Screen("theme")
    object Currency : Screen("currency")
    object Language : Screen("language")
    object Security : Screen("security")
    object Notifications : Screen("notifications")
    object ManagePayee : Screen("manage_payee")
    object AddNewPayee : Screen("add_new_payee")
    object ExpenseCategoryScreen : Screen("expense_category")
    object IncomeCategoryScreen : Screen("income_category")
    object NotificationView : Screen("notification_view")
    object AttachmentOptions : Screen("attachment_options")
    object TransactionFilter : Screen("transaction_filter")
    object ExpenseScreen : Screen("expense_screen")
    object IncomeScreen : Screen("income_screen")
    object About : Screen("about")
    object Haptics : Screen("haptics")
}