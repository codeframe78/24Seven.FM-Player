package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.StationId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.HttpCookie

class InMemoryAuthSessionStoreTest {
    @Test
    fun `sessions remain isolated and are copied`() {
        val store = InMemoryAuthSessionStore()
        val sst = StationId("sst")
        val cookie = HttpCookie("session", "sensitive").apply {
            domain = "streamingsoundtracks.com"
            path = "/"
            secure = true
            isHttpOnly = true
        }

        store.save(sst, "streamingsoundtracks.com", listOf(cookie), "Listener")
        val restored = store.load(sst, "streamingsoundtracks.com").single()

        assertEquals("sensitive", restored.value)
        assertNotSame(cookie, restored)
        assertEquals("Listener", store.loadDisplayName(sst))
        assertTrue(store.load(StationId("adagio"), "adagio.fm").isEmpty())

        store.clear(sst)
        assertTrue(store.load(sst, "streamingsoundtracks.com").isEmpty())
    }

    @Test
    fun `clearing one of five station sessions preserves every other session`() {
        val store = InMemoryAuthSessionStore()
        val stations = listOf(
            StationId("sst") to "streamingsoundtracks.com",
            StationId("1980s") to "1980s.fm",
            StationId("adagio") to "adagio.fm",
            StationId("death") to "death.fm",
            StationId("entranced") to "entranced.fm",
        )

        stations.forEachIndexed { index, (stationId, domain) ->
            store.save(
                stationId,
                domain,
                listOf(HttpCookie("session", "value-$index").apply { this.domain = domain }),
                "Listener $index",
            )
        }

        store.clear(StationId("death"))

        stations.forEachIndexed { index, (stationId, domain) ->
            if (stationId == StationId("death")) {
                assertTrue(store.load(stationId, domain).isEmpty())
                assertEquals(null, store.loadDisplayName(stationId))
            } else {
                assertEquals("value-$index", store.load(stationId, domain).single().value)
                assertEquals("Listener $index", store.loadDisplayName(stationId))
            }
        }
    }
}
