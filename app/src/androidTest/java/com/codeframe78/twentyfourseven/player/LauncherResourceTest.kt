package com.codeframe78.twentyfourseven.player

import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherResourceTest {
    @Test
    fun launcherUsesAdaptiveIconAndAndroid13MonochromeLayer() {
        val context = ApplicationProvider.getApplicationContext<RadioApplication>()
        assertEquals(R.mipmap.ic_launcher, context.applicationInfo.icon)

        val icon = context.getDrawable(context.applicationInfo.icon)
        assertTrue(icon is AdaptiveIconDrawable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            assertNotNull((icon as AdaptiveIconDrawable).monochrome)
        }
    }
}
