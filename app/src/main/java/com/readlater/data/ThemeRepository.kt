package com.readlater.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemeRepository(private val context: Context) {

    private val useDarkThemeKey = booleanPreferencesKey("use_dark_theme")

    val useDarkTheme: Flow<Boolean> = context.themeDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[useDarkThemeKey] ?: true
        }
        .distinctUntilChanged()

    suspend fun setUseDarkTheme(useDark: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[useDarkThemeKey] = useDark
        }
    }
}
