package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.Flow

interface StationRepository {
    fun observeStations(): Flow<List<Station>>
    fun observeSelectedStation(): Flow<Station>
    fun observeStationPreferences(): Flow<LocalStationPreferences>
    suspend fun selectStation(stationId: StationId)
    suspend fun useLastStationAtStartup()
    suspend fun setStartupStation(stationId: StationId)
}

