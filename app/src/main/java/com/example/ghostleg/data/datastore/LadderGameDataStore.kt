package com.example.ghostleg.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LadderGameDataStore(
    private val dataStore: DataStore<Preferences>
) {

    private val PLAYER_COUNT_KEY = intPreferencesKey("PLAYER_COUNT")

    fun getPlayerCountFlow(): Flow<Int> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { it[PLAYER_COUNT_KEY] ?: DEFAULT_PLAYER_COUNT }
    }

    suspend fun getPlayerCount(): Int {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { it[PLAYER_COUNT_KEY] ?: DEFAULT_PLAYER_COUNT }
            .first()
    }

    suspend fun setPlayerCount(playerCount: Int) {
        dataStore.edit { preferences ->
            preferences[PLAYER_COUNT_KEY] = playerCount
        }
    }

    companion object {
        private const val DEFAULT_PLAYER_COUNT = 6
    }
}
