package com.codeframe78.twentyfourseven.player.ui

import com.codeframe78.twentyfourseven.player.domain.TrackRequestAvailability
import com.codeframe78.twentyfourseven.player.domain.TrackRequestStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TrackSortOrderTest {
    @Test
    fun `library order preserves the station response`() {
        val tracks = tracks()

        assertEquals(
            tracks.map(Track::title),
            tracks.sortedForDisplay(TrackSortOrder.LibraryOrder, Track::availability).map(Track::title),
        )
    }

    @Test
    fun `play state puts actionable tracks first and remains stable within groups`() {
        assertEquals(
            listOf("Available first", "Available second", "Queued", "Recently played", "Unavailable"),
            tracks().sortedForDisplay(TrackSortOrder.PlayState, Track::availability).map(Track::title),
        )
    }

    private fun tracks() = listOf(
        Track("Recently played", TrackRequestStatus.RecentlyPlayed),
        Track("Available first", TrackRequestStatus.Available),
        Track("Unavailable", TrackRequestStatus.RequestsUnavailable),
        Track("Available second", TrackRequestStatus.Available),
        Track("Queued", TrackRequestStatus.InCurrentQueue),
    )

    private data class Track(
        val title: String,
        val status: TrackRequestStatus,
    ) {
        val availability = TrackRequestAvailability(status)
    }
}
