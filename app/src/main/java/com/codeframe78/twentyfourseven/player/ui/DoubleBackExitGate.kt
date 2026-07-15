package com.codeframe78.twentyfourseven.player.ui

internal class DoubleBackExitGate(
    private val windowMillis: Long = 2_000L,
) {
    private var firstPressMillis: Long? = null

    fun registerPress(nowMillis: Long): Boolean {
        val previous = firstPressMillis
        val isSecondPress = previous != null && nowMillis - previous in 0..windowMillis
        firstPressMillis = if (isSecondPress) null else nowMillis
        return isSecondPress
    }

    fun reset() {
        firstPressMillis = null
    }
}
