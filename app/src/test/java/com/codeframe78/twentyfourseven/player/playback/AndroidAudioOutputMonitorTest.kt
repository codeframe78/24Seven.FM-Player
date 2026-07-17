package com.codeframe78.twentyfourseven.player.playback

import android.media.AudioDeviceInfo
import com.codeframe78.twentyfourseven.player.domain.AudioOutputKind
import org.junit.Assert.assertEquals
import org.junit.Test

class AndroidAudioOutputMonitorTest {
    @Test
    fun `system route types map to stable user-facing categories`() {
        assertEquals(
            AudioOutputKind.Device,
            outputKind(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER),
        )
        assertEquals(
            AudioOutputKind.Bluetooth,
            outputKind(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP),
        )
        assertEquals(
            AudioOutputKind.Wired,
            outputKind(AudioDeviceInfo.TYPE_USB_HEADSET),
        )
    }

    @Test
    fun `remote output uses the remote category`() {
        val state = audioOutputState(
            routeName = "Living room",
            deviceType = AudioDeviceInfo.TYPE_REMOTE_SUBMIX,
        )

        assertEquals("Living room", state.displayName)
        assertEquals(AudioOutputKind.Remote, state.kind)
    }

    @Test
    fun `blank route names use a privacy-safe device fallback`() {
        val state = audioOutputState(
            routeName = "  ",
            deviceType = AudioDeviceInfo.TYPE_UNKNOWN,
        )

        assertEquals("This device", state.displayName)
        assertEquals(AudioOutputKind.Device, state.kind)
    }

    private fun outputKind(deviceType: Int) = audioOutputState(
        routeName = "Output",
        deviceType = deviceType,
    ).kind
}
