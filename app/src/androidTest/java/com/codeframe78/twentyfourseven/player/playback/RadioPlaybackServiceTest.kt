package com.codeframe78.twentyfourseven.player.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RadioPlaybackServiceTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun sessionHidesFallbackNavigationAndReconnectsAfterServiceStop() {
        val serviceIntent = Intent(context, RadioPlaybackService::class.java)
        assertNotNull(context.startService(serviceIntent))

        connectController().useOnMainThread { controller ->
            val commands = controller.availableCommands
            assertTrue(commands.contains(Player.COMMAND_PLAY_PAUSE))
            assertFalse(commands.contains(Player.COMMAND_SEEK_TO_NEXT))
            assertFalse(commands.contains(Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM))
            assertFalse(commands.contains(Player.COMMAND_SEEK_TO_PREVIOUS))
            assertFalse(commands.contains(Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM))
        }

        assertTrue(context.stopService(serviceIntent))
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        connectController().useOnMainThread { controller ->
            assertTrue(controller.availableCommands.contains(Player.COMMAND_PLAY_PAUSE))
        }
    }

    private fun connectController(): MediaController {
        val token = SessionToken(context, ComponentName(context, RadioPlaybackService::class.java))
        return MediaController.Builder(context, token)
            .buildAsync()
            .get(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
    }

    private fun MediaController.useOnMainThread(block: (MediaController) -> Unit) {
        try {
            InstrumentationRegistry.getInstrumentation().runOnMainSync { block(this) }
        } finally {
            InstrumentationRegistry.getInstrumentation().runOnMainSync { release() }
        }
    }

    private companion object {
        const val CONNECTION_TIMEOUT_SECONDS = 10L
    }
}
