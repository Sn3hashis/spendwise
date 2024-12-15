package me.sm.spendwise.ui

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import me.sm.spendwise.data.SecurityPreference
import me.sm.spendwise.navigation.NavigationState

object AppState {
    var currentUser: FirebaseUser? by mutableStateOf(null)
    var currentScreen: Screen by mutableStateOf(Screen.Home)
    var verificationEmail: String by mutableStateOf("")

    fun navigateToLogin() {
        currentScreen = Screen.Login
    }

    suspend fun logout(context: Context) {
        try {
            // Clear security preferences
            val securityPreference = SecurityPreference(context)
            securityPreference.clearSession()

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Clear current user
            currentUser = null

            // Navigate to login screen
            NavigationState.navigateTo(Screen.Login)

            Log.d("AppState", "Logout successful")
        } catch (e: Exception) {
            Log.e("AppState", "Error during logout", e)
        }
    }

    fun clearSession() {
        currentUser = null
        currentScreen = Screen.Login
        verificationEmail = ""
    }
} 