package com.codeframe78.twentyfourseven.player.data

import android.content.Context
import com.codeframe78.twentyfourseven.player.domain.LocalStationPreferences
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.StationPreferencesRepository
import com.codeframe78.twentyfourseven.player.domain.StartupStationMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedPreferencesStationPreferencesRepository(
    context: Context,
    preferencesName: String = PREFERENCES_NAME,
) : StationPreferencesRepository {
    private val preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    private val state = MutableStateFlow(readPreferences())

    override val current: LocalStationPreferences
        get() = state.value

    override fun observePreferences(): Flow<LocalStationPreferences> = state.asStateFlow()

    override suspend fun recordLastStation(stationId: StationId) {
        preferences.edit().putString(KEY_LAST_STATION, stationId.value).apply()
        state.value = state.value.copy(lastStationId = stationId)
    }

    override suspend fun setStartupPreference(mode: StartupStationMode, defaultStationId: StationId?) {
        preferences.edit()
            .putString(KEY_STARTUP_MODE, mode.name)
            .apply {
                if (defaultStationId == null) remove(KEY_DEFAULT_STATION)
                else putString(KEY_DEFAULT_STATION, defaultStationId.value)
            }
            .apply()
        state.value = state.value.copy(startupMode = mode, defaultStationId = defaultStationId)
    }

    private fun readPreferences() = LocalStationPreferences(
        startupMode = preferences.getString(KEY_STARTUP_MODE, null)
            ?.let { stored -> StartupStationMode.entries.firstOrNull { it.name == stored } }
            ?: StartupStationMode.LastSelected,
        defaultStationId = preferences.getString(KEY_DEFAULT_STATION, null)?.takeIf(String::isNotBlank)?.let(::StationId),
        lastStationId = preferences.getString(KEY_LAST_STATION, null)?.takeIf(String::isNotBlank)?.let(::StationId),
    )

    private companion object {
        const val PREFERENCES_NAME = "local_station_preferences"
        const val KEY_STARTUP_MODE = "startup_mode"
        const val KEY_DEFAULT_STATION = "default_station_id"
        const val KEY_LAST_STATION = "last_station_id"
    }
}

class InMemoryStationPreferencesRepository(
    initial: LocalStationPreferences = LocalStationPreferences(),
) : StationPreferencesRepository {
    private val state = MutableStateFlow(initial)

    override val current: LocalStationPreferences
        get() = state.value

    override fun observePreferences(): Flow<LocalStationPreferences> = state.asStateFlow()

    override suspend fun recordLastStation(stationId: StationId) {
        state.value = state.value.copy(lastStationId = stationId)
    }

    override suspend fun setStartupPreference(mode: StartupStationMode, defaultStationId: StationId?) {
        state.value = state.value.copy(startupMode = mode, defaultStationId = defaultStationId)
    }
}
