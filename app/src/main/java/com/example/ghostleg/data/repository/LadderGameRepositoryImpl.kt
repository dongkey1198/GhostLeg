package com.example.ghostleg.data.repository

import com.example.ghostleg.data.datastore.LadderGameDataStore
import kotlinx.coroutines.flow.Flow

class LadderGameRepositoryImpl(
    private val ladderGameDataStore: LadderGameDataStore
): LadderGameRepository {

    override val playerCount: Flow<Int> = ladderGameDataStore.getPlayerCountFlow()

    override suspend fun getPlayerCount(): Int {
        return ladderGameDataStore.getPlayerCount()
    }

    override suspend fun setPlayerCount(playerCount: Int) {
        ladderGameDataStore.setPlayerCount(playerCount)
    }
}
