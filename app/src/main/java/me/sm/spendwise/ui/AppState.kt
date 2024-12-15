package me.sm.spendwise.ui
import me.sm.spendwise.navigation.NavigationState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.content.Context
import me.sm.spendwise.data.SecurityPreference

object AppState {
    var currentScreen by mutableStateOf(Screen.Onboarding)
    var verificationEmail by mutableStateOf("")
    var currentUser by mutableStateOf<FirebaseUser?>(null)
    
    suspend fun logout(context: Context) {
        val securityPreference = SecurityPreference(context)
        
        // Clear session data
        securityPreference.clearSession()
        
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()
        
        // Clear current user
        currentUser = null
        
        // Reset navigation
        NavigationState.reset()
        
        // Return to login screen
        currentScreen = Screen.Login
    }
} 