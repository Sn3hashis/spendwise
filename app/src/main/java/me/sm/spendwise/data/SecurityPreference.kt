package me.sm.spendwise.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "security_prefs")

class SecurityPreference(private val context: Context) {
    private val securityMethodKey = stringPreferencesKey("security_method")
    private val pinKey = stringPreferencesKey("pin")

    fun getSecurityMethodFlow(): Flow<SecurityMethod?> {
        return context.dataStore.data.map { preferences ->
            preferences[securityMethodKey]?.let { SecurityMethod.valueOf(it) }
        }
    }

    suspend fun saveSecurityMethod(method: SecurityMethod) {
        context.dataStore.edit { preferences ->
            preferences[securityMethodKey] = method.name
        }
    }

    suspend fun savePin(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[pinKey] = pin
        }
    }

    fun getPin(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[pinKey]
        }
    }
}

enum class SecurityMethod {
    NONE, PIN, FINGERPRINT, FACE_ID
}