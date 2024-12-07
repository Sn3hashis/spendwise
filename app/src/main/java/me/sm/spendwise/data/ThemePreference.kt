// Create new file: app/src/main/java/me/sm/spendwise/data/ThemePreference.kt
package me.sm.spendwise.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sm.spendwise.ui.screens.ThemeMode

private val Context.dataStore by preferencesDataStore(name = "theme_settings")

class ThemePreference(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme_mode")

    val themeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[themeKey] ?: ThemeMode.SYSTEM.name
    }

    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = themeMode.name
        }
    }
}
