package me.sm.spendwise

import ProfileScreen
import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.addCallback
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import me.sm.spendwise.ui.theme.SpendwiseTheme
import me.sm.spendwise.onboarding.OnboardingScreen
import me.sm.spendwise.auth.LoginScreen
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.auth.SignUpScreen
import me.sm.spendwise.auth.VerificationScreen
import me.sm.spendwise.auth.ForgotPasswordScreen
import me.sm.spendwise.auth.ForgotPasswordSentScreen
import me.sm.spendwise.ui.screens.*
import me.sm.spendwise.ui.components.BottomNavigationBar
import me.sm.spendwise.navigation.NavigationState
import me.sm.spendwise.navigation.Screen as NavScreen // Alias to avoid naming conflicts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle back press
        onBackPressedDispatcher.addCallback(this) {
            when (NavigationState.currentScreen) {
                NavScreen.Home -> showExitConfirmationDialog()
                NavScreen.Settings -> NavigationState.navigateBack()
                NavScreen.Expense, NavScreen.Income, NavScreen.Transfer, NavScreen.Transaction, NavScreen.Budget, NavScreen.Profile, NavScreen.ExpenseDetails -> NavigationState.navigateTo(NavScreen.Home)
                else -> NavigationState.navigateTo(NavScreen.Home)
            }
        }

        setContent {
            SpendwiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Assuming AppState.currentScreen is also using the NavScreen enum
                    when (AppState.currentScreen) {
                        Screen.Onboarding -> OnboardingScreen(onFinish = { AppState.currentScreen = Screen.Login })
                        Screen.Login -> LoginScreen(
                            onLoginSuccess = { AppState.currentScreen = Screen.Main },
                            onSignUpClick = { AppState.currentScreen = Screen.SignUp },
                            onForgotPasswordClick = { AppState.currentScreen = Screen.ForgotPassword }
                        )
                        Screen.SignUp -> SignUpScreen(
                            onSignUpSuccess = { email ->
                                AppState.currentScreen = Screen.Verification
                                AppState.verificationEmail = email
                            },
                            onLoginClick = { AppState.currentScreen = Screen.Login },
                            onBackClick = { AppState.currentScreen = Screen.Login }
                        )
                        Screen.Verification -> VerificationScreen(
                            email = AppState.verificationEmail,
                            onBackClick = { AppState.currentScreen = Screen.SignUp },
                            onVerificationComplete = { AppState.currentScreen = Screen.Main }
                        )
                        Screen.ForgotPassword -> ForgotPasswordScreen(
                            onBackClick = { AppState.currentScreen = Screen.Login },
                            onContinueClick = { email ->
                                AppState.verificationEmail = email
                                AppState.currentScreen = Screen.ForgotPasswordSent
                            }
                        )
                        Screen.ForgotPasswordSent -> ForgotPasswordSentScreen(
                            email = AppState.verificationEmail,
                            onBackToLoginClick = { AppState.currentScreen = Screen.Login }
                        )
                        Screen.Main -> MainScreen()
                    }
                }
            }
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AnimatedContent(
            targetState = NavigationState.currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) with
                        fadeOut(animationSpec = tween(300))
            }
        ) { screen ->
            when (screen) {
                NavScreen.Home -> HomeScreen { expenseId ->
                    NavigationState.currentExpenseId = expenseId
                    NavigationState.navigateTo(NavScreen.ExpenseDetails)
                }
                NavScreen.Transaction -> TransactionsScreen()
                NavScreen.Budget -> BudgetScreen()
                NavScreen.Profile -> ProfileScreen()
                NavScreen.Expense -> ExpenseScreen { NavigationState.navigateBack() }
                NavScreen.Income -> IncomeScreen { NavigationState.navigateBack() }
                NavScreen.Transfer -> TransferScreen { NavigationState.navigateBack() }
                NavScreen.ExpenseDetails -> ExpenseDetailScreen(
                    expenseTitle = NavigationState.currentExpenseId ?: "",
                    onBackPress = { NavigationState.navigateBack() },
                    onEditPress = { /* Handle edit action */ },
                    onDeletePress = { /* Handle delete action */ }
                )
                NavScreen.Settings -> SettingsScreen { NavigationState.navigateBack() }
                NavScreen.Theme -> ThemeScreen(
                    onBackPress = { NavigationState.navigateBack() },
                    onThemeSelected = { /* TODO */ }
                )
                NavScreen.Currency -> CurrencyScreen(
                    onBackPress = { NavigationState.navigateBack() },
                    onCurrencySelected = { /* TODO */ }
                )
                NavScreen.Language -> LanguageScreen(
                    onBackPress = { NavigationState.navigateBack() },
                    onLanguageSelected = { /* TODO */ }
                )
                NavScreen.Notifications -> NotificationScreen(
                    onBackPress = { NavigationState.navigateBack() },
//                    onNotificationSettingChanged = {/* TODO */}
                )
                NavScreen.Security -> SecurityScreen(
                    onBackPress = { NavigationState.navigateBack() },
                    onSecurityMethodSelected = { /* TODO */ }
                )
//                NavScreen.About -> AboutScreen(
//                    onBackPress = { NavigationState.navigateBack() },
//                    onAboutSelected = { /* TODO */ }
//                )
                    // ... other NavScreen enum values, including Login and SignUp
                    else -> HomeScreen { expenseId ->
                    NavigationState.currentExpenseId = expenseId
                    NavigationState.navigateTo(NavScreen.ExpenseDetails)
                }
            }
        }

        if (NavigationState.currentScreen in listOf(NavScreen.Home, NavScreen.Transaction, NavScreen.Budget, NavScreen.Profile)) {
            BottomNavigationBar()
        }
    }
}