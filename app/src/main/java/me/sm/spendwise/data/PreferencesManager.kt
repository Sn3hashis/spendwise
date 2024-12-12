package me.sm.spendwise.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "spendwise_preferences")

class PreferencesManager(private val context: Context) {
    private val verifiedEmailsKey = stringSetPreferencesKey("verified_emails")

    suspend fun addVerifiedEmail(email: String) {
        val currentEmails = getVerifiedEmails().toMutableSet()
        currentEmails.add(email)
        context.dataStore.edit { preferences ->
            preferences[verifiedEmailsKey] = currentEmails
        }
    }

    suspend fun isEmailVerified(email: String): Boolean {
        return getVerifiedEmails().contains(email)
    }

    private suspend fun getVerifiedEmails(): Set<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[verifiedEmailsKey] ?: emptySet()
            }
            .first()
    }
} 