package com.codeframe78.twentyfourseven.player.domain

import java.text.Normalizer
import java.util.Locale

enum class TrackRequestStatus {
    Available,
    InCurrentQueue,
    RecentlyPlayed,
    AuthenticationRequired,
    UserCooldown,
    MembershipRequired,
    RequestLimitReached,
    StationUnavailable,
    RequestsUnavailable,
    Unknown,
}

data class TrackRequestAvailability(
    val status: TrackRequestStatus,
    val detail: String? = null,
) {
    val canRequest: Boolean get() = status == TrackRequestStatus.Available

    companion object {
        fun available() = TrackRequestAvailability(TrackRequestStatus.Available)
        fun unknown(detail: String? = null) = TrackRequestAvailability(TrackRequestStatus.Unknown, detail)
    }
}

data class RequestTrackIdentity(
    val songId: String? = null,
    val albumId: String? = null,
    val title: String,
    val artist: String? = null,
    val albumTitle: String? = null,
)

object TrackRequestAvailabilityResolver {
    fun resolve(
        stationId: StationId,
        identity: RequestTrackIdentity,
        stationAvailability: TrackRequestAvailability,
        queue: QueueState,
    ): TrackRequestAvailability {
        if (queue.stationId != stationId) {
            return TrackRequestAvailability(
                TrackRequestStatus.StationUnavailable,
                "Request status was not available for the selected station.",
            )
        }
        if (queue.status != QueueLoadStatus.Ready || queue.isStale) {
            return if (stationAvailability.status == TrackRequestStatus.Available) {
                TrackRequestAvailability(
                    TrackRequestStatus.RequestsUnavailable,
                    if (queue.isStale) {
                        "Cached Queue data is stale and must be refreshed before this track can be requested."
                    } else {
                        "Queue status must be refreshed before this track can be requested."
                    },
                )
            } else {
                stationAvailability
            }
        }
        queue.upcoming.firstOrNull { matches(identity, it.identity()) }?.let {
            return TrackRequestAvailability(
                TrackRequestStatus.InCurrentQueue,
                "This track is currently in the station queue.",
            )
        }
        if (queue.upcoming.any { sameText(identity.title, it.displayTitle) }) {
            return TrackRequestAvailability(
                TrackRequestStatus.RequestsUnavailable,
                "A queued track shares this title, but its metadata could not be matched safely.",
            )
        }
        queue.recentlyPlayed.firstOrNull { matches(identity, it.identity()) }?.let {
            return TrackRequestAvailability(
                TrackRequestStatus.RecentlyPlayed,
                stationAvailability.detail ?: "This track appears in the station's recently played list.",
            )
        }
        if (queue.recentlyPlayed.any { sameText(identity.title, it.displayTitle) }) {
            return TrackRequestAvailability(
                TrackRequestStatus.RequestsUnavailable,
                "A recently played track shares this title, but its metadata could not be matched safely.",
            )
        }
        return stationAvailability
    }

    fun matches(candidate: RequestTrackIdentity, observed: RequestTrackIdentity): Boolean {
        val candidateSongId = candidate.songId?.takeIf(String::isNotBlank)
        val observedSongId = observed.songId?.takeIf(String::isNotBlank)
        if (candidateSongId != null && observedSongId != null) return candidateSongId == observedSongId

        val candidateAlbumId = candidate.albumId?.normalizeId()
        val observedAlbumId = observed.albumId?.normalizeId()
        if (candidateAlbumId != null && observedAlbumId != null) {
            return candidateAlbumId == observedAlbumId && sameText(candidate.title, observed.title)
        }

        if (!sameText(candidate.title, observed.title)) return false
        val comparableArtist = comparable(candidate.artist, observed.artist)
        val comparableAlbum = comparable(candidate.albumTitle, observed.albumTitle)
        if (!comparableArtist && !comparableAlbum) return false
        if (bothPresent(candidate.artist, observed.artist) && !sameText(candidate.artist, observed.artist)) return false
        if (bothPresent(candidate.albumTitle, observed.albumTitle) && !sameText(candidate.albumTitle, observed.albumTitle)) return false
        return true
    }

    private fun QueueTrack.identity() = RequestTrackIdentity(
        songId = songId,
        albumId = albumId,
        title = displayTitle,
        artist = artistName,
        albumTitle = albumTitle,
    )

    private fun HistoryTrack.identity() = RequestTrackIdentity(
        songId = songId,
        albumId = albumId,
        title = displayTitle,
        artist = artistName,
        albumTitle = albumTitle,
    )

    private fun comparable(first: String?, second: String?): Boolean =
        !first.isNullOrBlank() && !second.isNullOrBlank()

    private fun bothPresent(first: String?, second: String?): Boolean = comparable(first, second)

    private fun sameText(first: String?, second: String?): Boolean =
        !first.isNullOrBlank() && !second.isNullOrBlank() && normalizeText(first) == normalizeText(second)

    private fun normalizeText(value: String): String = Normalizer.normalize(value, Normalizer.Form.NFKD)
        .lowercase(Locale.ROOT)
        .replace(COMBINING_MARKS, "")
        .replace(PUNCTUATION, " ")
        .replace(WHITESPACE, " ")
        .trim()

    private fun String.normalizeId(): String? = trim().lowercase(Locale.ROOT).takeIf(String::isNotEmpty)

    private val COMBINING_MARKS = Regex("\\p{M}+")
    private val PUNCTUATION = Regex("[^\\p{L}\\p{N}]+")
    private val WHITESPACE = Regex("\\s+")
}

fun classifyStationRequestAvailability(message: String?): TrackRequestAvailability {
    val detail = message?.trim()?.takeIf(String::isNotBlank)
        ?: return TrackRequestAvailability.unknown()
    val normalized = detail.lowercase(Locale.ROOT)
    return when {
        listOf("last played", "played recently", "recently played", "requestable again").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.RecentlyPlayed, detail)
        listOf("track is already in queue", "track is already in the queue", "track is currently in queue").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.InCurrentQueue, detail)
        listOf("sign in", "log in", "login").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.AuthenticationRequired, detail)
        listOf("vip", "membership", "member only").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.MembershipRequired, detail)
        listOf("request limit", "maximum requests").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.RequestLimitReached, detail)
        listOf("cooldown", "wait before", "too soon").any(normalized::contains) ->
            TrackRequestAvailability(TrackRequestStatus.UserCooldown, detail)
        else -> TrackRequestAvailability(TrackRequestStatus.RequestsUnavailable, detail)
    }
}
