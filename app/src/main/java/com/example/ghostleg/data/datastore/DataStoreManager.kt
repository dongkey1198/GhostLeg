package com.example.ghostleg.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.ladderGameDataStore: DataStore<Preferences> by preferencesDataStore(name = DataStoreManager.LADDER_GAME_PREFERENCES_NAME)
class DataStoreManager(
    private val context: Context
) {

    fun getLadderGameDataStore(): DataStore<Preferences> {
        return context.ladderGameDataStore
    }

    companion object {
        const val LADDER_GAME_PREFERENCES_NAME = "LADDER_GAME_PREFERENCES"
    }
}