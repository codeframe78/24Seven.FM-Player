package com.codeframe78.twentyfourseven.player.ui

import com.codeframe78.twentyfourseven.player.domain.Station
import com.codeframe78.twentyfourseven.player.domain.StationId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerExperienceTest {
    @Test
    fun `adjacent stations wrap in both directions`() {
        val stations = listOf("sst", "1980s", "adagio", "death", "entranced").map(::station)

        assertEquals(StationId("1980s"), adjacentStationId(stations, StationId("sst"), 1))
        assertEquals(StationId("entranced"), adjacentStationId(stations, StationId("sst"), -1))
        assertEquals(StationId("sst"), adjacentStationId(stations, StationId("entranced"), 1))
    }

    @Test
    fun `double back opens confirmation only inside its window`() {
        val gate = DoubleBackExitGate(windowMillis = 2_000)

        assertFalse(gate.registerPress(1_000))
        assertTrue(gate.registerPress(2_500))
        assertFalse(gate.registerPress(2_600))
        assertFalse(gate.registerPress(5_000))
        assertTrue(gate.registerPress(7_000))
    }

    private fun station(id: String) = Station(
        id = StationId(id),
        name = id,
        shortName = id,
        description = id,
        websiteUrl = "https://example.invalid/",
    )
}
