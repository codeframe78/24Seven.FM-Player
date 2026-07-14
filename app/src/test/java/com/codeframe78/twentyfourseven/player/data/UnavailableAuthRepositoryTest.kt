package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.AuthStatus
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UnavailableAuthRepositoryTest {
    @Test
    fun `state remains unavailable without a verified authentication source`() = runTest {
        val stationId = StationId("sst")
        val repository = UnavailableAuthRepository()

        repository.signIn(stationId, "not-stored", "not-stored", "not-stored")
        val state = repository.observeAuth(stationId).first()

        assertEquals(stationId, state.stationId)
        assertEquals(AuthStatus.Unavailable, state.status)
        assertNull(state.displayName)
        assertNull(state.errorMessage)
    }
}
