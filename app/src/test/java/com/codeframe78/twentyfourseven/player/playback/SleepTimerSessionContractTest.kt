package com.codeframe78.twentyfourseven.player.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SleepTimerSessionContractTest {
    @Test
    fun `restored timer uses the earliest consistent monotonic and wall deadline`() {
        val timer = PersistedSleepTimer(
            elapsedRealtimeDeadlineMillis = 1_600_000L,
            epochDeadlineMillis = 10_600_500L,
        )

        assertEquals(
            599_500L,
            timer.restoredRemainingMillis(
                elapsedRealtimeMillis = 1_000_000L,
                epochMillis = 10_001_000L,
            ),
        )
    }

    @Test
    fun `expired timer restores as elapsed`() {
        val timer = PersistedSleepTimer(
            elapsedRealtimeDeadlineMillis = 900L,
            epochDeadlineMillis = 9_000L,
        )

        assertEquals(0L, timer.restoredRemainingMillis(1_000L, 10_000L))
    }

    @Test
    fun `clock discontinuity invalidates persisted timer`() {
        val timer = PersistedSleepTimer(
            elapsedRealtimeDeadlineMillis = 1_600_000L,
            epochDeadlineMillis = 20_000_000L,
        )

        assertNull(
            timer.restoredRemainingMillis(
                elapsedRealtimeMillis = 1_000_000L,
                epochMillis = 10_000_000L,
            ),
        )
    }
}
