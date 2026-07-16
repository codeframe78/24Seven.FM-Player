package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.Flow

enum class RequestSearchField(val wireValue: String) {
    Title("title"),
    Album("album"),
    Artist("artist"),
    Genre("genre"),
}

enum class RequestSuggestionMode(val wireValue: String) {
    Random("random"),
    LeastPlayed("randomleast"),
}

enum class SongRequestLoadStatus { Idle, Loading, Ready, Submitting, Error }

const val MAX_REQUEST_MESSAGE_CHARACTERS = 80

sealed interface RequestSearchTarget {
    data class Album(val albumId: String) : RequestSearchTarget
    data class Artist(val artistName: String) : RequestSearchTarget
}

data class RequestSearchResult(
    val target: RequestSearchTarget,
    val title: String,
    val subtitle: String? = null,
    val year: String? = null,
)

data class RequestableTrack(
    val albumId: String,
    val songId: String,
    val title: String,
    val artist: String? = null,
    val duration: String? = null,
    val eligible: Boolean,
    val albumTitle: String? = null,
    val availability: TrackRequestAvailability = if (eligible) {
        TrackRequestAvailability.available()
    } else {
        TrackRequestAvailability.unknown()
    },
) {
    val identity: RequestTrackIdentity get() = RequestTrackIdentity(
        songId = songId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumTitle = albumTitle,
    )
}

data class SongRequestState(
    val stationId: StationId,
    val status: SongRequestLoadStatus = SongRequestLoadStatus.Idle,
    val query: String = "",
    val searchField: RequestSearchField = RequestSearchField.Title,
    val searchResults: List<RequestSearchResult> = emptyList(),
    val albumTitle: String? = null,
    val tracks: List<RequestableTrack> = emptyList(),
    val pendingRequest: RequestableTrack? = null,
    val notice: String? = null,
    val errorMessage: String? = null,
)

interface SongRequestRepository {
    fun observeRequests(stationId: StationId): Flow<SongRequestState>
    suspend fun search(stationId: StationId, query: String, field: RequestSearchField)
    suspend fun suggest(stationId: StationId, mode: RequestSuggestionMode)
    suspend fun openSearchResult(stationId: StationId, target: RequestSearchTarget)
    suspend fun prepareRequest(stationId: StationId, songId: String)
    suspend fun prepareRequest(stationId: StationId, track: RequestableTrack)
    suspend fun cancelRequest(stationId: StationId)
    suspend fun confirmRequest(stationId: StationId, queue: QueueState, message: String = "")
}
