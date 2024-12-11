package me.sm.spendwise.ui
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object AppState {
    var currentScreen by mutableStateOf(Screen.Onboarding)
    var verificationEmail by mutableStateOf("")
    var currentUser by mutableStateOf<FirebaseUser?>(null)
    
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        currentUser = null
        NavigationState.reset()
        currentScreen = Screen.Login
    }
}

enum class Screen {
    Onboarding,
    Login,
    SignUp,
    Verification,
    SecuritySetup,
    Main,
    ForgotPassword,
    ForgotPasswordSent
} 