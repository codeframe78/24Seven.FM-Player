package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.Flow

enum class StartupStationMode {
    LastSelected,
    Fixed,
}

data class LocalStationPreferences(
    val startupMode: StartupStationMode = StartupStationMode.LastSelected,
    val defaultStationId: StationId? = null,
    val lastStationId: StationId? = null,
)

interface StationPreferencesRepository {
    val current: LocalStationPreferences

    fun observePreferences(): Flow<LocalStationPreferences>
    suspend fun recordLastStation(stationId: StationId)
    suspend fun setStartupPreference(mode: StartupStationMode, defaultStationId: StationId? = null)
}
