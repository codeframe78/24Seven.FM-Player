package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.FavoriteTrack
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksLoadStatus
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkFavoriteTracksRepositoryTest {
    private val stationId = StationId("sst")

    @Test
    fun `refresh publishes immutable favorites and clear removes prior account data`() = runTest {
        val expected = listOf(FavoriteTrack(1, "Title", "Album", "Artist"))
        val repository = NetworkFavoriteTracksRepository(FakeFavoriteTracksRemote(expected))

        repository.refresh(stationId)
        val ready = repository.observeFavorites(stationId).first()
        assertEquals(FavoriteTracksLoadStatus.Ready, ready.status)
        assertEquals(expected, ready.tracks)

        repository.clear(stationId)
        val cleared = repository.observeFavorites(stationId).first()
        assertEquals(FavoriteTracksLoadStatus.Idle, cleared.status)
        assertTrue(cleared.tracks.isEmpty())
    }

    @Test
    fun `missing session exposes sign in guidance`() = runTest {
        val repository = NetworkFavoriteTracksRepository(
            FakeFavoriteTracksRemote(failure = FavoritesAuthenticationRequiredException()),
        )

        repository.refresh(stationId)
        val state = repository.observeFavorites(stationId).first()

        assertEquals(FavoriteTracksLoadStatus.Error, state.status)
        assertEquals("Sign in to this station to load your favorite tracks.", state.errorMessage)
    }

    private class FakeFavoriteTracksRemote(
        private val tracks: List<FavoriteTrack> = emptyList(),
        private val failure: Throwable? = null,
    ) : FavoriteTracksRemoteDataSource {
        override suspend fun load(stationId: StationId): List<FavoriteTrack> {
            failure?.let { throw it }
            return tracks
        }
    }
}
