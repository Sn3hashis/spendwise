package me.sm.spendwise.ui
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppState {
    var currentScreen by mutableStateOf(Screen.Onboarding)
    var verificationEmail by mutableStateOf("")

    fun logout() {
         NavigationState.reset() 
        currentScreen = Screen.Login
    }
}

enum class Screen {
    Onboarding,
    Login,
    SignUp,
    Verification,
    ForgotPassword,
    ForgotPasswordSent,
    Main
} 