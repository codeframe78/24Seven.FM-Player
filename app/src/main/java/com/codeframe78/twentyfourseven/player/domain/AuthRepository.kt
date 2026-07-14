package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.Flow

enum class AuthStatus { Unavailable, SignedOut, SigningIn, SignedIn, Error }

data class AuthState(
    val stationId: StationId,
    val status: AuthStatus = AuthStatus.Unavailable,
    val displayName: String? = null,
    val errorMessage: String? = null,
)

interface AuthRepository {
    fun observeAuth(stationId: StationId): Flow<AuthState>

    /** Credentials are transient inputs and must never be persisted or logged. */
    suspend fun signIn(stationId: StationId, username: String, password: String)

    suspend fun signOut(stationId: StationId)
}
