package me.sm.spendwise


import ProfileScreen
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator

import me.sm.spendwise.ui.theme.SpendwiseTheme
import me.sm.spendwise.onboarding.OnboardingScreen
import me.sm.spendwise.auth.LoginScreen
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.auth.SignUpScreen
import me.sm.spendwise.auth.VerificationScreen
import me.sm.spendwise.auth.ForgotPasswordScreen
import me.sm.spendwise.auth.ForgotPasswordSentScreen
import me.sm.spendwise.data.CurrencyPreference
import me.sm.spendwise.data.CurrencyState
import me.sm.spendwise.data.ThemePreference
import me.sm.spendwise.ui.screens.*
import me.sm.spendwise.ui.components.BottomNavigationBar
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.mutableStateListOf
import me.sm.spendwise.data.Payee
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.ui.screens.SecuritySetupScreen
import me.sm.spendwise.ui.screens.SecurityVerificationScreen
import me.sm.spendwise.data.SecurityMethod
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

import kotlinx.coroutines.cancel


class MainActivity : FragmentActivity() {
    private lateinit var themePreference: ThemePreference
    private lateinit var currencyPreference: CurrencyPreference
    private lateinit var securityPreference: SecurityPreference

    private val activityScope = CoroutineScope(Dispatchers.Main + Job())

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MainContent() {
        var needsPinValidation by remember { mutableStateOf(false) }
        var needsPinSetup by remember { mutableStateOf(false) }
        var needsSecurityValidation by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(true) }
        val context = LocalContext.current
        val securityPreference = remember { SecurityPreference(context) }
        val scope = rememberCoroutineScope()
        val payees = remember { mutableStateListOf<Payee>() }

        LaunchedEffect(Unit) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                AppState.currentUser = user
                try {
                    // Initialize security method first
                    securityPreference.initializeSecurityMethod()
                    
                    // Then check security setup status
                    val currentMethod = securityPreference.getCurrentSecurityMethod()
                    when {
                        currentMethod == SecurityMethod.FINGERPRINT -> {
                            needsSecurityValidation = true
                            needsPinSetup = false
                            needsPinValidation = false
                        }
                        !securityPreference.hasPinSetup() -> {
                            needsPinSetup = true
                            needsPinValidation = false
                            needsSecurityValidation = false
                        }
                        else -> {
                            needsPinValidation = true
                            needsPinSetup = false
                            needsSecurityValidation = false
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error initializing security", e)
                    needsPinValidation = true
                    needsPinSetup = false
                    needsSecurityValidation = false
                }
            } else {
                AppState.currentScreen = Screen.Login
            }
            isLoading = false
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                needsPinSetup -> {
                    SecuritySetupScreen(
                        onSetupComplete = {
                            needsPinSetup = false
                            needsPinValidation = false
                            needsSecurityValidation = false
                            NavigationState.navigateTo(Screen.Home)
                        }
                    )
                }
                needsSecurityValidation || needsPinValidation -> {
                    SecurityVerificationScreen(
                        onVerificationSuccess = {
                            needsPinValidation = false
                            needsSecurityValidation = false
                            NavigationState.navigateTo(Screen.Home)
                        }
                    )
                }
                AppState.currentUser == null -> {
                    when (AppState.currentScreen) {
                        Screen.Onboarding -> OnboardingScreen(
                            onFinish = { AppState.currentScreen = Screen.Login }
                        )
                        Screen.Login -> LoginScreen(
                            onLoginSuccess = { /* handled in LoginScreen */ },
                            onSignUpClick = { AppState.currentScreen = Screen.SignUp },
                            onForgotPasswordClick = { AppState.currentScreen = Screen.ForgotPassword }
                        )
                        Screen.SignUp -> SignUpScreen(
                            onSignUpSuccess = { /* handled in SignUpScreen */ },
                            onLoginClick = { AppState.currentScreen = Screen.Login },
                            onBackClick = TODO()
                        )
                        Screen.ForgotPassword -> ForgotPasswordScreen(


                            onEmailSent = { email ->
                                AppState.verificationEmail = email
                                AppState.currentScreen = Screen.ForgotPasswordSent
                            },
                            onBackClick = TODO()
                        )
                        Screen.ForgotPasswordSent -> ForgotPasswordSentScreen(
                            email = AppState.verificationEmail,
                            onBackToLoginClick = { AppState.currentScreen = Screen.Login }
                        )
                        else -> {
                            // Default to login screen if no user
                            AppState.currentScreen = Screen.Login
                        }
                    }
                }
                else -> {
                    // Main content - only show when user is logged in and PIN is verified
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) {
                        // Main content with navigation
                        AnimatedContent(
                            targetState = NavigationState.currentScreen,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) with
                                        fadeOut(animationSpec = tween(300))
                            }
                        ) { screen ->
                            when (screen) {
                                Screen.Home -> HomeScreen { expenseId ->
                                    NavigationState.currentExpenseId = expenseId
                                    NavigationState.navigateTo(Screen.ExpenseDetails)
                                }
                                Screen.NotificationView -> NotificationViewScreen(
                                    onBackClick = { NavigationState.navigateBack() }
                                )
                                Screen.Transaction -> TransactionsScreen()
                                Screen.Budget -> BudgetScreen()
                                Screen.Profile -> ProfileScreen()
                                Screen.Expense -> ExpenseScreen { NavigationState.navigateBack() }
                                Screen.Income -> IncomeScreen { NavigationState.navigateBack() }
                                Screen.Transfer -> TransferScreen(
                                    payees = payees,
                                    onBackPress = { NavigationState.navigateBack() }
                                )
                                Screen.ExpenseDetails -> ExpenseDetailScreen(
                                    expenseTitle = NavigationState.currentExpenseId ?: "",
                                    onBackPress = { NavigationState.navigateBack() },
                                    onEditPress = { /* Handle edit action */ },
                                    onDeletePress = { /* Handle delete action */ }
                                )
                                Screen.Settings -> SettingsScreen { NavigationState.navigateBack() }
                                Screen.Theme -> ThemeScreen(
                                    onBackPress = { NavigationState.navigateBack() },
                                    themePreference = themePreference
                                )
                                Screen.Currency -> CurrencyScreen(
                                    onBackPress = { NavigationState.navigateBack() }
                                )
                                Screen.Language -> LanguageScreen(
                                    onBackPress = { NavigationState.navigateBack() },
                                    onLanguageSelected = { /* TODO */ }
                                )
                                Screen.Notifications -> NotificationScreen(
                                    onBackPress = { NavigationState.navigateBack() }
                                )
                                Screen.Security -> SecurityScreen(
                                    onBackPress = { NavigationState.navigateBack() }
                                )
                                Screen.ManagePayee -> PayeeScreen(
                                    payees = payees,
                                    onAddPayeeClick = { NavigationState.navigateTo(Screen.AddNewPayee) },
                                    onEditPayeeClick = { payee ->
                                        NavigationState.payeeToEdit = payee
                                        NavigationState.navigateTo(Screen.AddNewPayee)
                                    },
                                    onDeletePayeeClick = { payee -> payees.remove(payee) },
                                    onBackPress = { NavigationState.navigateBack() }
                                )
                                Screen.AddNewPayee -> AddNewPayeeScreen(
                                    payeeToEdit = NavigationState.payeeToEdit,
                                    onPayeeAdded = { newPayee ->
                                        if (NavigationState.payeeToEdit != null) {
                                            payees.remove(NavigationState.payeeToEdit)
                                            payees.add(newPayee)
                                            NavigationState.payeeToEdit = null
                                        } else {
                                            payees.add(newPayee)
                                        }
                                        NavigationState.navigateTo(Screen.ManagePayee)
                                    }
                                )
                                else -> HomeScreen { expenseId ->
                                    NavigationState.currentExpenseId = expenseId
                                    NavigationState.navigateTo(Screen.ExpenseDetails)
                                }
                            }
                        }

                        if (NavigationState.currentScreen in listOf(
                                Screen.Home,
                                Screen.Transaction,
                                Screen.Budget,
                                Screen.Profile
                            )
                        ) {
                            BottomNavigationBar()
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themePreference = ThemePreference(this)
        currencyPreference = CurrencyPreference(this)
        securityPreference = SecurityPreference(this)

        // Check if user is already logged in
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            AppState.currentUser = auth.currentUser
        } else {
            // No user logged in, go to login screen
            AppState.currentScreen = Screen.Login
        }

        onBackPressedDispatcher.addCallback(this) {
            Log.d("MainActivity", "Back pressed, current screen: ${NavigationState.currentScreen}")
            when (NavigationState.currentScreen) {
                Screen.Home -> showExitConfirmationDialog()
                Screen.Settings,
                Screen.Currency,
                Screen.Theme,
                Screen.Language,
                Screen.Security,
                Screen.AddNewPayee,
                Screen.Notifications -> NavigationState.navigateBack()
                Screen.ManagePayee -> NavigationState.navigateTo(Screen.Profile)
                Screen.Expense,
                Screen.Income,
                Screen.Transfer,
                Screen.Transaction,
                Screen.Budget,
                Screen.Profile,
                Screen.ExpenseDetails -> NavigationState.navigateTo(Screen.Home)
                Screen.ExpenseCategoryScreen -> NavigationState.navigateTo(Screen.Expense)
                Screen.IncomeCategoryScreen -> NavigationState.navigateTo(Screen.Income)
                Screen.SecuritySetup -> NavigationState.navigateBack()
                else -> NavigationState.navigateTo(Screen.Home)
            }
        }

        setContent {
            val themeMode by themePreference.themeFlow.collectAsState(initial = ThemeMode.SYSTEM.name)
            val currency by currencyPreference.currencyFlow.collectAsState(initial = "USD")
            CurrencyState.currentCurrency = currency

            SpendwiseTheme(
                themeMode = ThemeMode.valueOf(themeMode)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the activity scope when the activity is destroyed
        activityScope.cancel()
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