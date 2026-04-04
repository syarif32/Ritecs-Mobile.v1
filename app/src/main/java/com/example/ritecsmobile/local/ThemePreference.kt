package com.example.ritecsmobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore by preferencesDataStore("theme_prefs")

class ThemePreferences(private val context: Context) {
    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
    }
    val isDarkMode: Flow<Boolean?> = context.themeDataStore.data.map { preferences ->
        preferences[IS_DARK_MODE]
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }
}