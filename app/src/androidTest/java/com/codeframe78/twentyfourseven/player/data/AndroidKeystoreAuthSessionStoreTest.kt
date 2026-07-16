package com.codeframe78.twentyfourseven.player.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codeframe78.twentyfourseven.player.domain.StationId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpCookie

@RunWith(AndroidJUnit4::class)
class AndroidKeystoreAuthSessionStoreTest {
    @Test
    fun encryptedSessionRoundTripsAndClears() {
        val store = AndroidKeystoreAuthSessionStore(ApplicationProvider.getApplicationContext())
        val stationId = StationId("keystore-test")
        store.clear(stationId)
        val cookie = HttpCookie("session", "instrumentation-only-value").apply {
            path = "/"
            isHttpOnly = true
        }

        store.save(stationId, "streamingsoundtracks.com", listOf(cookie), "Listener")
        val restored = store.load(stationId, "streamingsoundtracks.com")

        assertEquals(1, restored.size)
        assertEquals("session", restored.single().name)
        assertEquals("instrumentation-only-value", restored.single().value)
        assertEquals("streamingsoundtracks.com", restored.single().domain)
        assertTrue(restored.single().secure)
        assertEquals("Listener", store.loadDisplayName(stationId))
        assertTrue(store.load(stationId, "example.com").isEmpty())
        store.clear(stationId)
        assertTrue(store.load(stationId, "streamingsoundtracks.com").isEmpty())
    }

    @Test
    fun clearingOneEncryptedStationSessionPreservesAnother() {
        val store = AndroidKeystoreAuthSessionStore(ApplicationProvider.getApplicationContext())
        val first = StationId("keystore-isolation-first")
        val second = StationId("keystore-isolation-second")
        store.clear(first)
        store.clear(second)
        try {
            store.save(first, "first.example", listOf(HttpCookie("session", "first-value")), "First listener")
            store.save(second, "second.example", listOf(HttpCookie("session", "second-value")), "Second listener")

            store.clear(first)

            assertTrue(store.load(first, "first.example").isEmpty())
            assertEquals("second-value", store.load(second, "second.example").single().value)
            assertEquals("Second listener", store.loadDisplayName(second))
        } finally {
            store.clear(first)
            store.clear(second)
        }
    }
}
