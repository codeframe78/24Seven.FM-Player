package com.codeframe78.twentyfourseven.player.playback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkPlaybackRecoveryTest {
    @Test
    fun `offline playback failure waits for one network restoration retry`() {
        val recovery = NetworkPlaybackRecovery(initialNetworkUsable = false)

        assertTrue(recovery.onPlaybackError(shouldResume = true))
        assertTrue(recovery.isWaitingForNetwork)
        assertTrue(recovery.onNetworkStateChanged(isUsable = true))

        recovery.markRetryStarted()

        assertFalse(recovery.isWaitingForNetwork)
        assertFalse(recovery.onNetworkStateChanged(isUsable = true))
    }

    @Test
    fun `online server failure does not schedule automatic retry`() {
        val recovery = NetworkPlaybackRecovery(initialNetworkUsable = true)

        assertFalse(recovery.onPlaybackError(shouldResume = true))
        assertFalse(recovery.isWaitingForNetwork)
    }

    @Test
    fun `paused playback and explicit cancellation suppress recovery`() {
        val recovery = NetworkPlaybackRecovery(initialNetworkUsable = false)

        assertFalse(recovery.onPlaybackError(shouldResume = false))
        assertFalse(recovery.onNetworkStateChanged(isUsable = true))

        recovery.onNetworkStateChanged(isUsable = false)
        assertTrue(recovery.onPlaybackError(shouldResume = true))
        recovery.cancel()

        assertFalse(recovery.onNetworkStateChanged(isUsable = true))
    }
}
