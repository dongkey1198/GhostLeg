package com.example.ghostleg.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class DataStoreManager(
    private val context: Context
) {

    private val Context.ladderGameDataStore: DataStore<Preferences> by preferencesDataStore(name = LADDER_GAME_PREFERENCES_NAME)

    fun getLadderGameDataStore(): DataStore<Preferences> {
        return context.ladderGameDataStore
    }

    companion object {
        private const val LADDER_GAME_PREFERENCES_NAME = "LADDER_GAME_PREFERENCES"
    }
}