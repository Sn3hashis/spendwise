package me.sm.spendwise

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import me.sm.spendwise.ui.theme.SpendwiseTheme
import me.sm.spendwise.onboarding.OnboardingScreen
import me.sm.spendwise.auth.LoginScreen
import me.sm.spendwise.ui.AppState
import me.sm.spendwise.ui.Screen
import me.sm.spendwise.auth.SignUpScreen
import me.sm.spendwise.auth.VerificationScreen
import me.sm.spendwise.auth.ForgotPasswordScreen
import me.sm.spendwise.auth.ForgotPasswordSentScreen
import me.sm.spendwise.ui.screens.HomeScreen
import me.sm.spendwise.ui.screens.TransactionsScreen
import me.sm.spendwise.ui.screens.BudgetScreen
import me.sm.spendwise.ui.screens.ProfileScreen
import me.sm.spendwise.ui.components.BottomNavigationBar
import me.sm.spendwise.navigation.NavigationState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SpendwiseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (AppState.currentScreen) {
                        Screen.Onboarding -> {
                            OnboardingScreen(
                                onFinish = {
                                    AppState.currentScreen = Screen.Login
                                }
                            )
                        }
                        Screen.Login -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    AppState.currentScreen = Screen.Main
                                },
                                onSignUpClick = {
                                    AppState.currentScreen = Screen.SignUp
                                },
                                onForgotPasswordClick = {
                                    AppState.currentScreen = Screen.ForgotPassword
                                }
                            )
                        }
                        Screen.SignUp -> {
                            SignUpScreen(
                                onSignUpSuccess = { email ->
                                    AppState.currentScreen = Screen.Verification
                                    // Store email for verification screen
                                    AppState.verificationEmail = email
                                },
                                onLoginClick = {
                                    AppState.currentScreen = Screen.Login
                                },
                                onBackClick = {
                                    AppState.currentScreen = Screen.Login
                                }
                            )
                        }
                        Screen.Verification -> {
                            VerificationScreen(
                                email = AppState.verificationEmail,
                                onBackClick = {
                                    AppState.currentScreen = Screen.SignUp
                                },
                                onVerificationComplete = {
                                    AppState.currentScreen = Screen.Main
                                }
                            )
                        }
                        Screen.Main -> {
                            MainScreen()
                        }
                        Screen.ForgotPassword -> {
                            ForgotPasswordScreen(
                                onBackClick = {
                                    AppState.currentScreen = Screen.Login
                                },
                                onContinueClick = { email ->
                                    AppState.verificationEmail = email  // Store email for confirmation screen
                                    AppState.currentScreen = Screen.ForgotPasswordSent
                                }
                            )
                        }
                        Screen.ForgotPasswordSent -> {
                            ForgotPasswordSentScreen(
                                email = AppState.verificationEmail,
                                onBackToLoginClick = {
                                    AppState.currentScreen = Screen.Login
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    var showingFilter by remember { mutableStateOf(false) }

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
                "Home" -> HomeScreen()
                "Transaction" -> TransactionsScreen(
                    onShowFilter = { showing -> showingFilter = showing }
                )
                "Budget" -> BudgetScreen()
                "Profile" -> ProfileScreen()
                else -> HomeScreen()
            }
        }
        
        // Only show navbar when filter is not active
        if (!showingFilter) {
            BottomNavigationBar()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SpendwiseTheme {
        Greeting("Android")
    }
}
