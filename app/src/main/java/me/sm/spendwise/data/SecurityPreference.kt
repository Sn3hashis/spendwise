package me.sm.spendwise.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "security_prefs")

class SecurityPreference(private val context: Context) {
    private val securityMethodKey = stringPreferencesKey("security_method")
    private val pinKey = stringPreferencesKey("pin")
    private val enrolledMethodsKey = stringSetPreferencesKey("enrolled_methods")

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

    suspend fun addEnrolledMethod(method: SecurityMethod) {
        context.dataStore.edit { preferences ->
            val currentMethods = preferences[enrolledMethodsKey]?.toMutableSet() ?: mutableSetOf()
            currentMethods.add(method.name)
            preferences[enrolledMethodsKey] = currentMethods
        }
    }

    fun getEnrolledMethodsFlow(): Flow<Set<SecurityMethod>> {
        return context.dataStore.data.map { preferences ->
            preferences[enrolledMethodsKey]?.mapNotNull { methodName ->
                try {
                    SecurityMethod.valueOf(methodName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }?.toSet() ?: emptySet()
        }
    }

    suspend fun removeEnrolledMethod(method: SecurityMethod) {
        context.dataStore.edit { preferences ->
            val currentMethods = preferences[enrolledMethodsKey]?.toMutableSet() ?: mutableSetOf()
            currentMethods.remove(method.name)
            preferences[enrolledMethodsKey] = currentMethods
        }
    }
}

enum class SecurityMethod {
    NONE, PIN, FINGERPRINT
}