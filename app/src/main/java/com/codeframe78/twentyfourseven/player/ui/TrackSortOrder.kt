package com.codeframe78.twentyfourseven.player.ui

import com.codeframe78.twentyfourseven.player.domain.TrackRequestAvailability
import com.codeframe78.twentyfourseven.player.domain.TrackRequestStatus

internal enum class TrackSortOrder(val label: String) {
    LibraryOrder("Library order"),
    PlayState("Play state"),
}

internal fun <T> List<T>.sortedForDisplay(
    order: TrackSortOrder,
    availability: (T) -> TrackRequestAvailability,
): List<T> = when (order) {
    TrackSortOrder.LibraryOrder -> this
    TrackSortOrder.PlayState -> {
        val groups = List(TrackRequestStatus.entries.size) { mutableListOf<T>() }
        forEach { track -> groups[availability(track).status.playStateSortRank()] += track }
        buildList(size) { groups.forEach(::addAll) }
    }
}

private fun TrackRequestStatus.playStateSortRank(): Int = when (this) {
    TrackRequestStatus.Available -> 0
    TrackRequestStatus.InCurrentQueue -> 1
    TrackRequestStatus.RecentlyPlayed -> 2
    TrackRequestStatus.UserCooldown -> 3
    TrackRequestStatus.RequestLimitReached -> 4
    TrackRequestStatus.MembershipRequired -> 5
    TrackRequestStatus.AuthenticationRequired -> 6
    TrackRequestStatus.RequestsUnavailable -> 7
    TrackRequestStatus.StationUnavailable -> 8
    TrackRequestStatus.Unknown -> 9
}
