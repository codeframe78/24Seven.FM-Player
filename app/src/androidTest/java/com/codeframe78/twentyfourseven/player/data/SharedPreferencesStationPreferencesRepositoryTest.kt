package com.codeframe78.twentyfourseven.player.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.StartupStationMode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesStationPreferencesRepositoryTest {
    @Test
    fun preferencesSurviveRepositoryRecreationAndRemainDeviceLocal() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "m14-station-preferences-test"
        context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        try {
            SharedPreferencesStationPreferencesRepository(context, name).apply {
                recordLastStation(StationId("adagio"))
                setStartupPreference(StartupStationMode.Fixed, StationId("death"))
            }

            val restored = SharedPreferencesStationPreferencesRepository(context, name).current

            assertEquals(StationId("adagio"), restored.lastStationId)
            assertEquals(StartupStationMode.Fixed, restored.startupMode)
            assertEquals(StationId("death"), restored.defaultStationId)
        } finally {
            context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear().commit()
        }
    }

    @Test
    fun unknownPersistedModeFallsBackToLastSelectedBehavior() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "m14-station-preferences-corrupt-test"
        val stored = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        stored.edit().clear().putString("startup_mode", "REMOVED_MODE").putString("last_station_id", "entranced").commit()
        try {
            val restored = SharedPreferencesStationPreferencesRepository(context, name).current

            assertEquals(StartupStationMode.LastSelected, restored.startupMode)
            assertEquals(StationId("entranced"), restored.lastStationId)
        } finally {
            stored.edit().clear().commit()
        }
    }
}
