package com.notsatria.poms.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")
class SettingPreference(private val dataStore: DataStore<Preferences>) {

    companion object {
        val WORKING_TIME = intPreferencesKey("working_time")
        val BREAK_TIME = intPreferencesKey("break_tim")
        val WORKING_SESSION = intPreferencesKey("working_session")
    }

    suspend fun saveWorkingTime(time: Int) {
        dataStore.edit { preferences ->
            preferences[WORKING_TIME] = time
        }
    }

    val workingTime: Flow<Int?> = dataStore.data
        .map { preferences -> preferences[WORKING_TIME] }
        .catch { emit(null) }

    suspend fun saveBreakTime(time: Int) {
        dataStore.edit { preferences ->
            preferences[BREAK_TIME] = time
        }
    }

    val breakTime: Flow<Int?> = dataStore.data
        .map { preferences -> preferences[BREAK_TIME] }
        .catch { emit(null) }

    suspend fun saveWorkingSession(session: Int) {
        dataStore.edit { preferences ->
            preferences[WORKING_SESSION] = session
        }
    }

    val workingSession: Flow<Int?> = dataStore.data
        .map { preferences -> preferences[WORKING_SESSION] }
        .catch { emit(null) }
}