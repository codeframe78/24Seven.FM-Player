package com.codeframe78.twentyfourseven.player.playback

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media3.session.SessionCommand
import com.codeframe78.twentyfourseven.player.domain.SleepTimerState
import kotlin.math.abs
import kotlin.math.min

internal object SleepTimerSessionContract {
    const val MINIMUM_DURATION_MILLIS = 60_000L
    const val MAXIMUM_DURATION_MILLIS = 12L * 60L * 60L * 1_000L

    private const val ACTION_PREFIX = "com.codeframe78.twentyfourseven.player.sleep_timer."
    private const val KEY_DURATION_MILLIS = "duration_millis"
    private const val KEY_DEADLINE_ELAPSED_REALTIME_MILLIS = "deadline_elapsed_realtime_millis"
    private const val KEY_REMAINING_MILLIS = "remaining_millis"

    val setCommand = SessionCommand("${ACTION_PREFIX}SET", Bundle.EMPTY)
    val cancelCommand = SessionCommand("${ACTION_PREFIX}CANCEL", Bundle.EMPTY)
    val expiredCommand = SessionCommand("${ACTION_PREFIX}EXPIRED", Bundle.EMPTY)

    fun setArguments(durationMillis: Long): Bundle = bundleOf(KEY_DURATION_MILLIS to durationMillis)

    fun durationMillis(arguments: Bundle): Long? = arguments
        .getLong(KEY_DURATION_MILLIS, -1L)
        .takeIf { it in MINIMUM_DURATION_MILLIS..MAXIMUM_DURATION_MILLIS }

    fun extras(deadlineElapsedRealtimeMillis: Long?, remainingMillis: Long): Bundle = bundleOf(
        KEY_DEADLINE_ELAPSED_REALTIME_MILLIS to (deadlineElapsedRealtimeMillis ?: 0L),
        KEY_REMAINING_MILLIS to remainingMillis.coerceAtLeast(0L),
    )

    fun state(extras: Bundle): SleepTimerState {
        val deadline = extras.getLong(KEY_DEADLINE_ELAPSED_REALTIME_MILLIS, 0L).takeIf { it > 0L }
        val remaining = extras.getLong(KEY_REMAINING_MILLIS, 0L).coerceAtLeast(0L)
        return SleepTimerState(
            endsAtElapsedRealtimeMillis = deadline.takeIf { remaining > 0L },
            remainingMillis = remaining.takeIf { deadline != null } ?: 0L,
        )
    }
}

internal data class PersistedSleepTimer(
    val elapsedRealtimeDeadlineMillis: Long,
    val epochDeadlineMillis: Long,
) {
    fun restoredRemainingMillis(
        elapsedRealtimeMillis: Long,
        epochMillis: Long,
        clockToleranceMillis: Long = DEFAULT_CLOCK_TOLERANCE_MILLIS,
    ): Long? {
        val elapsedRemaining = elapsedRealtimeDeadlineMillis - elapsedRealtimeMillis
        val epochRemaining = epochDeadlineMillis - epochMillis
        if (elapsedRemaining <= 0L || epochRemaining <= 0L) return 0L
        if (abs(elapsedRemaining - epochRemaining) > clockToleranceMillis) return null
        return min(elapsedRemaining, epochRemaining)
    }

    companion object {
        const val DEFAULT_CLOCK_TOLERANCE_MILLIS = 5L * 60L * 1_000L
    }
}
