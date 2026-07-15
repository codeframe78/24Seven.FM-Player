package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.StreamFormat
import com.codeframe78.twentyfourseven.player.domain.LocalStationPreferences
import com.codeframe78.twentyfourseven.player.domain.StartupStationMode
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class BootstrapStationRepositoryTest {
    private val repository = BootstrapStationRepository()

    @Test
    fun `catalog contains the five network stations`() = runTest {
        val stations = repository.observeStations().first()

        assertEquals(
            listOf("sst", "1980s", "adagio", "death", "entranced"),
            stations.map { it.id.value },
        )
    }

    @Test
    fun `station selection updates observed station`() = runTest {
        repository.selectStation(StationId("adagio"))

        assertEquals("adagio", repository.observeSelectedStation().first().id.value)
    }

    @Test
    fun `every station has relay and source fallbacks`() = runTest {
        repository.observeStations().first().forEach { station ->
            assertEquals(listOf("Primary relay", "Source stream"), station.streams.map { it.label })
            assertEquals(listOf(0, 1), station.streams.map { it.priority })
            assertEquals(listOf(StreamFormat.Aac, StreamFormat.Aac), station.streams.map { it.format })
            assertEquals(listOf(128, 128), station.streams.map { it.bitrateKbps })
        }
    }

    @Test
    fun `fixed startup station is selected before the repository emits`() = runTest {
        val preferences = InMemoryStationPreferencesRepository(
            LocalStationPreferences(
                startupMode = StartupStationMode.Fixed,
                defaultStationId = StationId("death"),
                lastStationId = StationId("adagio"),
            ),
        )

        val selected = BootstrapStationRepository(preferences).observeSelectedStation().first()

        assertEquals(StationId("death"), selected.id)
    }

    @Test
    fun `last selected startup falls back across removed or corrupt station ids`() = runTest {
        val preferences = InMemoryStationPreferencesRepository(
            LocalStationPreferences(
                startupMode = StartupStationMode.Fixed,
                defaultStationId = StationId("removed-station"),
                lastStationId = StationId("entranced"),
            ),
        )

        val selected = BootstrapStationRepository(preferences).observeSelectedStation().first()

        assertEquals(StationId("entranced"), selected.id)
    }

    @Test
    fun `selection and startup actions persist only valid station ids`() = runTest {
        val preferences = InMemoryStationPreferencesRepository()
        val repository = BootstrapStationRepository(preferences)

        repository.selectStation(StationId("adagio"))
        repository.setStartupStation(StationId("death"))
        repository.selectStation(StationId("unknown"))

        assertEquals(StationId("adagio"), preferences.current.lastStationId)
        assertEquals(StartupStationMode.Fixed, preferences.current.startupMode)
        assertEquals(StationId("death"), preferences.current.defaultStationId)

        repository.useLastStationAtStartup()
        assertEquals(StartupStationMode.LastSelected, preferences.current.startupMode)
        assertEquals(null, preferences.current.defaultStationId)
    }

    @Test
    fun `every station exposes the administrator authorized queue and history capabilities`() = runTest {
        val stations = repository.observeStations().first()
        stations.forEach { station ->
            assertEquals(true, station.capabilities.supportsQueue)
            assertEquals(true, station.capabilities.supportsHistory)
            assertEquals(true, station.capabilities.supportsRequests)
        }
        assertEquals(
            listOf("sst"),
            stations.filter { it.capabilities.supportsRequestMessages }.map { it.id.value },
        )
    }
}
