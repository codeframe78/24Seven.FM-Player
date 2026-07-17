package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.StateFlow

enum class PlaybackStatus {
    Idle,
    Connecting,
    Buffering,
    Playing,
    Paused,
    Retrying,
    WaitingForNetwork,
    Error,
}

data class PlaybackState(
    val stationId: StationId? = null,
    val status: PlaybackStatus = PlaybackStatus.Idle,
    val errorMessage: String? = null,
    val sleepTimer: SleepTimerState = SleepTimerState(),
)

data class SleepTimerState(
    val endsAtElapsedRealtimeMillis: Long? = null,
    val remainingMillis: Long = 0L,
) {
    val isActive: Boolean
        get() = endsAtElapsedRealtimeMillis != null && remainingMillis > 0L
}

interface PlaybackController {
    val state: StateFlow<PlaybackState>

    fun selectStation(station: Station)
    fun play()
    fun pause()
    fun stop()
    fun setSleepTimer(durationMillis: Long)
    fun cancelSleepTimer()
}
