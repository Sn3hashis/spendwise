package me.sm.spendwise.data

import android.content.Context
import android.util.Log
import android.view.View
import android.view.HapticFeedbackConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class HapticsPreference(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("haptics_prefs", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    
    companion object {
        private const val HAPTICS_ENABLED_KEY = "haptics_enabled"
    }

    fun isHapticsEnabled(): Boolean {
        return sharedPreferences.getBoolean(HAPTICS_ENABLED_KEY, true) // Default to true
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        Log.d("HapticsPreference", "Setting haptics enabled: $enabled")
        
        // Save locally
        sharedPreferences.edit()
            .putBoolean(HAPTICS_ENABLED_KEY, enabled)
            .apply()

        // Save to Firebase
        val user = auth.currentUser
        if (user != null) {
            try {
                database.child("users")
                    .child(user.uid)
                    .child("settings")
                    .child("haptics_enabled")
                    .setValue(enabled)
                    .await()
                Log.d("HapticsPreference", "Haptics preference saved to Firebase")
            } catch (e: Exception) {
                Log.e("HapticsPreference", "Error saving haptics preference to Firebase", e)
            }
        }
    }

    suspend fun syncWithFirebase() {
        val user = auth.currentUser
        if (user != null) {
            try {
                val snapshot = database.child("users")
                    .child(user.uid)
                    .child("settings")
                    .child("haptics_enabled")
                    .get()
                    .await()

                val firebaseEnabled = snapshot.value as? Boolean
                if (firebaseEnabled != null) {
                    sharedPreferences.edit()
                        .putBoolean(HAPTICS_ENABLED_KEY, firebaseEnabled)
                        .apply()
                    Log.d("HapticsPreference", "Synced haptics preference from Firebase: $firebaseEnabled")
                }
            } catch (e: Exception) {
                Log.e("HapticsPreference", "Error syncing haptics preference from Firebase", e)
            }
        }
    }

    fun getHapticsEnabledFlow(): Flow<Boolean> = flow {
        emit(isHapticsEnabled())
    }

    fun performHapticFeedback(view: View, feedbackType: Int = HapticFeedbackConstants.VIRTUAL_KEY) {
        if (isHapticsEnabled()) {
            view.performHapticFeedback(feedbackType)
        }
    }

    fun performPinEntryHapticFeedback(view: View) {
        if (isHapticsEnabled()) {
            view.performHapticFeedback(HapticFeedbackConstants.TEXT_HANDLE_MOVE)
        }
    }
} 