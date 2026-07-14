package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.AuthRepository
import com.codeframe78.twentyfourseven.player.domain.AuthState
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UnavailableAuthRepository : AuthRepository {
    override fun observeAuth(stationId: StationId): Flow<AuthState> = flowOf(AuthState(stationId))

    override suspend fun restoreSession(stationId: StationId) = Unit

    override suspend fun refreshChallenge(stationId: StationId) = Unit

    override suspend fun signIn(
        stationId: StationId,
        username: String,
        password: String,
        securityCode: String,
    ) = Unit

    override suspend fun signOut(stationId: StationId) = Unit
}
