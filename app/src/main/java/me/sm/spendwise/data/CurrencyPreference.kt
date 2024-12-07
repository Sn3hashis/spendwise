package me.sm.spendwise.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "currency_settings")

class CurrencyPreference(private val context: Context) {
    private val currencyKey = stringPreferencesKey("currency_code")

    val currencyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[currencyKey] ?: "USD"
    }

    suspend fun saveCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[currencyKey] = currencyCode
        }
    }
}
