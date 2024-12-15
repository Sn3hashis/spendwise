package me.sm.spendwise.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HapticsPreference(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("haptics_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val HAPTICS_ENABLED_KEY = "haptics_enabled"
    }

    fun isHapticsEnabled(): Boolean {
        return sharedPreferences.getBoolean(HAPTICS_ENABLED_KEY, true) // Default to true
    }

    fun setHapticsEnabled(enabled: Boolean) {
        Log.d("HapticsPreference", "Setting haptics enabled: $enabled")
        sharedPreferences.edit()
            .putBoolean(HAPTICS_ENABLED_KEY, enabled)
            .apply()
    }

    fun getHapticsEnabledFlow(): Flow<Boolean> = flow {
        emit(isHapticsEnabled())
    }
} 