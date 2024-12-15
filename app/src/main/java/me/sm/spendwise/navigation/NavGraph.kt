package me.sm.spendwise.navigation

import ProfileScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.ui.screens.*
import me.sm.spendwise.onboarding.OnboardingScreen
import me.sm.spendwise.auth.LoginScreen
import me.sm.spendwise.auth.SignUpScreen
import me.sm.spendwise.auth.ForgotPasswordScreen
import me.sm.spendwise.auth.ForgotPasswordSentScreen
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.data.ThemePreference
import me.sm.spendwise.data.ExpenseCategory
import me.sm.spendwise.data.ExpenseCategories
import me.sm.spendwise.data.IncomeCategory
import me.sm.spendwise.data.IncomeCategories
import androidx.compose.ui.platform.LocalContext
import me.sm.spendwise.data.FilterType
import me.sm.spendwise.data.SortType

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val themePreference = remember { ThemePreference(context) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth & Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = { NavigationState.navigateTo(Screen.Login) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { NavigationState.navigateTo(Screen.Home) },
                onSignUpClick = { NavigationState.navigateTo(Screen.SignUp) },
                onForgotPasswordClick = { NavigationState.navigateTo(Screen.ForgotPassword) }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { NavigationState.navigateTo(Screen.Home) },
                onLoginClick = { NavigationState.navigateTo(Screen.Login) },
                onBackClick = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onEmailSent = { email -> 
                    AppState.verificationEmail = email
                    NavigationState.navigateTo(Screen.ForgotPasswordSent) 
                },
                onBackClick = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.ForgotPasswordSent.route) {
            ForgotPasswordSentScreen(
                email = AppState.verificationEmail,
                onBackToLoginClick = { NavigationState.navigateTo(Screen.Login) }
            )
        }

        // Main Navigation
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToExpenseDetail = { expenseId ->
                    NavigationState.currentExpenseId = expenseId
                    NavigationState.navigateTo(Screen.ExpenseDetails)
                }
            )
        }

        composable(Screen.Transaction.route) {
            TransactionsScreen()
        }

        composable(Screen.Budget.route) {
            BudgetScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // Features
        composable(Screen.Expense.route) {
            ExpenseScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.Income.route) {
            IncomeScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.Transfer.route) {
            TransferScreen(
                payees = emptyList(),
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.ExpenseDetails.route) {
            ExpenseDetailScreen(
                expenseTitle = NavigationState.currentExpenseId ?: "",
                onBackPress = { NavigationState.navigateBack() },
                onEditPress = { /* Handle edit action */ },
                onDeletePress = { /* Handle delete action */ }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.Theme.route) {
            ThemeScreen(
                onBackPress = { NavigationState.navigateBack() },
                themePreference = themePreference
            )
        }

        composable(Screen.Currency.route) {
            CurrencyScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.Language.route) {
            LanguageScreen(
                onBackPress = { NavigationState.navigateBack() },
                onLanguageSelected = { /* Handle language selection */ }
            )
        }

        composable(Screen.Security.route) {
            SecurityScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.SecuritySetup.route) {
            SecuritySetupScreen(
                onSetupComplete = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.SecurityVerification.route) {
            SecurityVerificationScreen(
                onVerificationSuccess = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.ManagePayee.route) {
            PayeeScreen(
                payees = emptyList(),
                onAddPayeeClick = { NavigationState.navigateTo(Screen.AddNewPayee) },
                onEditPayeeClick = { payee ->
                    NavigationState.payeeToEdit = payee
                    NavigationState.navigateTo(Screen.AddNewPayee)
                },
                onDeletePayeeClick = { /* Handle delete */ },
                onBackPress = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.AddNewPayee.route) {
            AddNewPayeeScreen(
                payeeToEdit = NavigationState.payeeToEdit,
                onPayeeAdded = { /* Handle payee added */ }
            )
        }

        composable(Screen.ExpenseCategoryScreen.route) {
            ExpenseCategoryScreen(
                onBackPress = { NavigationState.navigateBack() },
                onCategorySelected = { category ->
                    // Handle category selection
                    NavigationState.navigateBack()
                },
                initialCategory = ExpenseCategories.categories.firstOrNull()
            )
        }

        composable(Screen.IncomeCategoryScreen.route) {
            IncomeCategoryScreen(
                onBackPress = { NavigationState.navigateBack() },
                onCategorySelected = { category ->
                    // Handle category selection
                    NavigationState.navigateBack()
                },
                initialCategory = IncomeCategories.categories.firstOrNull()
            )
        }

        composable(Screen.NotificationView.route) {
            NotificationViewScreen(
                onBackClick = { NavigationState.navigateBack() }
            )
        }

        composable(Screen.AttachmentOptions.route) {
            AttachmentOptionsScreen(
                onDismiss = { NavigationState.navigateBack() },
                onCameraClick = { /* Handle camera click */ },
                onGalleryClick = { /* Handle gallery click */ },
                onDocumentClick = { /* Handle document click */ },
                onImagesSelected = { images -> 
                    // Handle selected images
                    NavigationState.navigateBack()
                }
            )
        }

        composable(Screen.TransactionFilter.route) {
            TransactionFilterScreen(
                onDismiss = { NavigationState.navigateBack() },
                initialFilterType = "ALL",
                initialSortType = "DATE_DESC",
                onApplyFilters = { filterType, sortType, dateRange ->
                    // Handle filter application
                    NavigationState.navigateBack()
                }
            )
        }

        composable(Screen.Haptics.route) {
            HapticsScreen(
                onBackPress = { NavigationState.navigateBack() }
            )
        }

//        composable(Screen.About.route) {
//            AboutScreen(
//                onBackPress = { NavigationState.navigateBack() }
//            )
//        }
    }
} 