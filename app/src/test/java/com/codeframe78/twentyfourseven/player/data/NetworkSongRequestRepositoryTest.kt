package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.RequestSearchField
import com.codeframe78.twentyfourseven.player.domain.RequestSearchResult
import com.codeframe78.twentyfourseven.player.domain.RequestSuggestionMode
import com.codeframe78.twentyfourseven.player.domain.RequestableTrack
import com.codeframe78.twentyfourseven.player.domain.QueueLoadStatus
import com.codeframe78.twentyfourseven.player.domain.QueueState
import com.codeframe78.twentyfourseven.player.domain.QueueTrack
import com.codeframe78.twentyfourseven.player.domain.TrackRequestStatus
import com.codeframe78.twentyfourseven.player.domain.TrackRequestAvailability
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class NetworkSongRequestRepositoryTest {
    @Test
    fun `search and album browsing are user initiated`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)

        assertEquals(0, remote.searchCalls)
        repository.search(stationId, "Example", RequestSearchField.Title)
        assertEquals(1, remote.searchCalls)
        assertEquals("Example track", repository.observeRequests(stationId).first().searchResults.single().trackTitle)

        repository.openAlbum(stationId, "ALBUM_1")
        assertEquals(1, remote.albumCalls)
        val albumState = repository.observeRequests(stationId).first()
        assertEquals("Requestable track", albumState.tracks.single().title)
        assertEquals(emptyList<RequestSearchResult>(), albumState.searchResults)
    }

    @Test
    fun `submission requires preparation and is never retried`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)
        repository.openAlbum(stationId, "ALBUM_1")

        repository.confirmRequest(stationId, readyQueue)
        assertEquals(0, remote.submitCalls)

        repository.prepareRequest(stationId, "12345")
        assertEquals("12345", repository.observeRequests(stationId).first().pendingRequest?.songId)
        repository.confirmRequest(stationId, readyQueue, "For the evening listeners")
        repository.confirmRequest(stationId, readyQueue)

        val state = repository.observeRequests(stationId).first()
        assertEquals(1, remote.submitCalls)
        assertEquals(2, remote.albumCalls)
        assertEquals("For the evening listeners", remote.lastMessage)
        assertNull(state.pendingRequest)
        assertFalse(state.tracks.single().eligible)
    }

    @Test
    fun `least played suggestion replaces prior browsing results`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)
        repository.search(stationId, "Example", RequestSearchField.Title)

        repository.suggest(stationId, RequestSuggestionMode.LeastPlayed)

        val state = repository.observeRequests(stationId).first()
        assertEquals(1, remote.suggestCalls)
        assertEquals(RequestSuggestionMode.LeastPlayed, remote.lastSuggestionMode)
        assertEquals(emptyList<RequestSearchResult>(), state.searchResults)
        assertEquals("Suggested track", state.tracks.single().title)
    }

    @Test
    fun `indeterminate confirmation suppresses retry and directs user to queue`() = runTest {
        val remote = FakeRemote().apply { submitFailure = IOException("Station response was too large") }
        val repository = NetworkSongRequestRepository(remote)
        repository.openAlbum(stationId, "ALBUM_1")
        repository.prepareRequest(stationId, "12345")

        repository.confirmRequest(stationId, readyQueue)
        repository.confirmRequest(stationId, readyQueue)

        val state = repository.observeRequests(stationId).first()
        assertEquals(1, remote.submitCalls)
        assertNull(state.pendingRequest)
        assertFalse(state.tracks.single().eligible)
        assertEquals(
            "The station may have received this request, but confirmation could not be read. " +
                "The confirmation page exceeded the safe response limit. " +
                "Check Queue before trying again. Nothing was retried.",
            state.errorMessage,
        )
    }

    @Test
    fun `fresh queue validation blocks a queued track without submitting`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)
        repository.openAlbum(stationId, "ALBUM_1")
        repository.prepareRequest(stationId, "12345")
        val queue = readyQueue.copy(
            upcoming = listOf(
                QueueTrack(
                    position = 1,
                    displayTitle = "Requestable track",
                    albumId = "ALBUM_1",
                    artistName = "Composer",
                    albumTitle = "Example album",
                ),
            ),
        )

        repository.confirmRequest(stationId, queue)

        val state = repository.observeRequests(stationId).first()
        assertEquals(0, remote.submitCalls)
        assertEquals(TrackRequestStatus.InCurrentQueue, state.tracks.single().availability.status)
        assertTrue(state.errorMessage.orEmpty().startsWith("Track Recently Played"))
    }

    @Test
    fun `missing fresh queue fails closed without submitting`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)
        repository.openAlbum(stationId, "ALBUM_1")
        repository.prepareRequest(stationId, "12345")

        repository.confirmRequest(stationId, QueueState(stationId, QueueLoadStatus.Error))

        assertEquals(0, remote.submitCalls)
        assertTrue(repository.observeRequests(stationId).first().errorMessage.orEmpty().startsWith("Requests Temporarily Unavailable"))
    }

    @Test
    fun `track leaving queue remains blocked when fresh station data says recently played`() = runTest {
        val remote = FakeRemote()
        val repository = NetworkSongRequestRepository(remote)
        repository.openAlbum(stationId, "ALBUM_1")
        repository.prepareRequest(stationId, "12345")
        remote.trackAvailability = TrackRequestAvailability(
            TrackRequestStatus.RecentlyPlayed,
            "Requestable again tomorrow.",
        )

        repository.confirmRequest(stationId, readyQueue)

        assertEquals(0, remote.submitCalls)
        assertEquals(
            TrackRequestStatus.RecentlyPlayed,
            repository.observeRequests(stationId).first().tracks.single().availability.status,
        )
    }

    private class FakeRemote : SongRequestRemoteDataSource {
        var searchCalls = 0
        var albumCalls = 0
        var submitCalls = 0
        var suggestCalls = 0
        var submitFailure: Throwable? = null
        var lastMessage: String? = null
        var lastSuggestionMode: RequestSuggestionMode? = null
        var trackAvailability = TrackRequestAvailability.available()

        override suspend fun search(stationId: StationId, query: String, field: RequestSearchField): List<RequestSearchResult> {
            searchCalls++
            return listOf(RequestSearchResult("ALBUM_1", "Example track", "Example album", "2004"))
        }

        override suspend fun loadAlbum(stationId: StationId, albumId: String): RequestAlbum {
            albumCalls++
            return RequestAlbum(
                "Example album",
                listOf(
                    RequestableTrack(
                        albumId,
                        "12345",
                        "Requestable track",
                        "Composer",
                        "3:21",
                        trackAvailability.canRequest,
                        albumTitle = "Example album",
                        availability = trackAvailability,
                    ),
                ),
            )
        }

        override suspend fun suggest(stationId: StationId, mode: RequestSuggestionMode): RequestAlbum {
            suggestCalls++
            lastSuggestionMode = mode
            return RequestAlbum(
                "Suggested album",
                listOf(RequestableTrack("ALBUM_2", "67890", "Suggested track", "Composer", "1:24", true)),
            )
        }

        override suspend fun submit(
            stationId: StationId,
            track: RequestableTrack,
            message: String,
        ): RequestSubmissionResult {
            submitCalls++
            lastMessage = message
            submitFailure?.let { throw it }
            return RequestSubmissionResult.Submitted("Request accepted")
        }
    }

    private companion object {
        val stationId = StationId("sst")
        val readyQueue = QueueState(stationId, status = QueueLoadStatus.Ready)
    }
}
