package com.example.ghostleg.data.repository

import kotlinx.coroutines.flow.Flow

interface LadderGameRepository {

    val playerCount: Flow<Int>
    suspend fun getPlayerCount(): Int
    suspend fun setPlayerCount(playerCount: Int)
}
