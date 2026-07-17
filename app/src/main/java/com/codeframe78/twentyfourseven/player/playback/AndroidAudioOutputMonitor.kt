package com.codeframe78.twentyfourseven.player.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import androidx.mediarouter.media.MediaRouter
import com.codeframe78.twentyfourseven.player.domain.AudioOutputKind
import com.codeframe78.twentyfourseven.player.domain.AudioOutputState

@Suppress("DEPRECATION")
internal class AndroidAudioOutputMonitor(
    context: Context,
    private val onOutputChanged: (AudioOutputState) -> Unit,
) {
    private val audioManager = context.getSystemService(AudioManager::class.java)
    private val mediaRouter = MediaRouter.getInstance(context)
    private val selector = MediaRouteSelector.Builder()
        .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
        .build()
    private val mediaRouterCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) = refresh()
        override fun onRouteUnselected(router: MediaRouter, route: MediaRouter.RouteInfo, reason: Int) = refresh()
        override fun onRouteChanged(router: MediaRouter, route: MediaRouter.RouteInfo) = refresh()
    }
    private val audioDeviceCallback = object : AudioDeviceCallback() {
        override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) = refresh()
        override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) = refresh()
    }

    init {
        mediaRouter.addCallback(selector, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS)
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, Handler(Looper.getMainLooper()))
        refresh()
    }

    fun refresh() {
        val device = currentMediaOutput(audioManager)
        onOutputChanged(
            audioOutputState(
                routeName = device?.productName,
                deviceType = device?.type ?: AudioDeviceInfo.TYPE_UNKNOWN,
            ),
        )
    }
}

@Suppress("DEPRECATION")
private fun currentMediaOutput(audioManager: AudioManager): AudioDeviceInfo? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        return currentMediaOutputApi33(audioManager)
    }
    val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
    return when {
        audioManager.isBluetoothA2dpOn -> outputs.firstOrNull { it.type in bluetoothDeviceTypes }
        audioManager.isWiredHeadsetOn -> outputs.firstOrNull { it.type in wiredDeviceTypes }
        else -> outputs.firstOrNull { it.type in localDeviceTypes }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun currentMediaOutputApi33(audioManager: AudioManager): AudioDeviceInfo? {
    val mediaAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()
    return audioManager.getAudioDevicesForAttributes(mediaAttributes).firstOrNull()
}

internal fun audioOutputState(
    routeName: CharSequence?,
    deviceType: Int,
): AudioOutputState {
    val kind = when (deviceType) {
        in bluetoothDeviceTypes -> AudioOutputKind.Bluetooth
        in wiredDeviceTypes -> AudioOutputKind.Wired
        in remoteDeviceTypes -> AudioOutputKind.Remote
        else -> AudioOutputKind.Device
    }
    return AudioOutputState(
        displayName = routeName?.toString()?.trim().orEmpty().ifEmpty { "This device" },
        kind = kind,
    )
}

private val bluetoothDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
    AudioDeviceInfo.TYPE_BLE_HEADSET,
    AudioDeviceInfo.TYPE_BLE_SPEAKER,
    AudioDeviceInfo.TYPE_HEARING_AID,
)

private val wiredDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_WIRED_HEADSET,
    AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
    AudioDeviceInfo.TYPE_USB_DEVICE,
    AudioDeviceInfo.TYPE_USB_ACCESSORY,
    AudioDeviceInfo.TYPE_USB_HEADSET,
    AudioDeviceInfo.TYPE_HDMI,
    AudioDeviceInfo.TYPE_HDMI_ARC,
    AudioDeviceInfo.TYPE_HDMI_EARC,
    AudioDeviceInfo.TYPE_DOCK,
    AudioDeviceInfo.TYPE_AUX_LINE,
    AudioDeviceInfo.TYPE_LINE_ANALOG,
    AudioDeviceInfo.TYPE_LINE_DIGITAL,
)

private val remoteDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_REMOTE_SUBMIX,
    AudioDeviceInfo.TYPE_IP,
    AudioDeviceInfo.TYPE_BUS,
)

private val localDeviceTypes = setOf(
    AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
    AudioDeviceInfo.TYPE_BUILTIN_EARPIECE,
)
