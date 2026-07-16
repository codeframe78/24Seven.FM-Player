package com.codeframe78.twentyfourseven.player.playback

internal class NetworkPlaybackRecovery(initialNetworkUsable: Boolean) {
    var isNetworkUsable: Boolean = initialNetworkUsable
        private set

    var isWaitingForNetwork: Boolean = false
        private set

    fun onNetworkStateChanged(isUsable: Boolean): Boolean {
        isNetworkUsable = isUsable
        return isUsable && isWaitingForNetwork
    }

    fun onPlaybackError(shouldResume: Boolean): Boolean {
        isWaitingForNetwork = shouldResume && !isNetworkUsable
        return isWaitingForNetwork
    }

    fun markRetryStarted() {
        isWaitingForNetwork = false
    }

    fun cancel() {
        isWaitingForNetwork = false
    }
}
