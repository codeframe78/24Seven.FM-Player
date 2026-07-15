package com.codeframe78.twentyfourseven.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codeframe78.twentyfourseven.player.domain.Station
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.StationRepository
import com.codeframe78.twentyfourseven.player.domain.PlaybackController
import com.codeframe78.twentyfourseven.player.domain.PlaybackState
import com.codeframe78.twentyfourseven.player.domain.NowPlayingRepository
import com.codeframe78.twentyfourseven.player.domain.NowPlayingState
import com.codeframe78.twentyfourseven.player.domain.QueueRepository
import com.codeframe78.twentyfourseven.player.domain.QueueState
import com.codeframe78.twentyfourseven.player.domain.AuthRepository
import com.codeframe78.twentyfourseven.player.domain.AuthState
import com.codeframe78.twentyfourseven.player.domain.AuthStatus
import com.codeframe78.twentyfourseven.player.domain.ChatRepository
import com.codeframe78.twentyfourseven.player.domain.ChatState
import com.codeframe78.twentyfourseven.player.domain.RequestSearchField
import com.codeframe78.twentyfourseven.player.domain.RequestSuggestionMode
import com.codeframe78.twentyfourseven.player.domain.SongRequestRepository
import com.codeframe78.twentyfourseven.player.domain.SongRequestState
import com.codeframe78.twentyfourseven.player.domain.FavoriteTrack
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksLoadStatus
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksRepository
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksState
import com.codeframe78.twentyfourseven.player.domain.TrackRequestAvailability
import com.codeframe78.twentyfourseven.player.domain.TrackRequestAvailabilityResolver
import com.codeframe78.twentyfourseven.player.domain.TrackRequestStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainDestination { Player, Favorites, Chat, Queue, More }

data class MainUiState(
    val stations: List<Station> = emptyList(),
    val selectedStation: Station? = null,
    val playback: PlaybackState = PlaybackState(),
    val nowPlaying: NowPlayingState = NowPlayingState(),
    val queue: QueueState? = null,
    val auth: AuthState? = null,
    val chat: ChatState? = null,
    val requests: SongRequestState? = null,
    val favorites: FavoriteTracksState? = null,
    val destination: MainDestination = MainDestination.Player,
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val stations: StationRepository,
    private val playback: PlaybackController,
    private val nowPlaying: NowPlayingRepository,
    private val queue: QueueRepository,
    private val auth: AuthRepository,
    private val chat: ChatRepository,
    private val requests: SongRequestRepository,
    private val favorites: FavoriteTracksRepository,
) : ViewModel() {
    private val destination = MutableStateFlow(MainDestination.Player)

    private val selectedQueue = combine(
        stations.observeSelectedStation(),
        destination,
    ) { station, selectedDestination -> station to selectedDestination }
        .flatMapLatest { (station, selectedDestination) ->
            if (selectedDestination in setOf(MainDestination.Queue, MainDestination.Favorites, MainDestination.More)) {
                queue.observeQueue(station.id)
            } else {
                flowOf(QueueState(station.id))
            }
        }

    private val selectedAuth = stations.observeSelectedStation()
        .flatMapLatest { station -> auth.observeAuth(station.id) }

    private val selectedChat = combine(
        stations.observeSelectedStation(),
        destination,
    ) { station, selectedDestination -> station to selectedDestination }
        .flatMapLatest { (station, selectedDestination) ->
            if (selectedDestination == MainDestination.Chat) {
                chat.observeChat(station.id)
            } else {
                flowOf(ChatState(station.id))
            }
        }

    private val selectedRequests = stations.observeSelectedStation()
        .flatMapLatest { station -> requests.observeRequests(station.id) }

    private val selectedFavorites = combine(
        stations.observeSelectedStation(),
        destination,
    ) { station, selectedDestination -> station to selectedDestination }
        .flatMapLatest { (station, selectedDestination) ->
            if (selectedDestination == MainDestination.Favorites) {
                favorites.observeFavorites(station.id)
            } else {
                flowOf(FavoriteTracksState(station.id))
            }
        }

    private val requestContent = combine(selectedRequests, selectedFavorites, ::RequestContent)

    private val stationContent = combine(
        nowPlaying.observeNowPlaying(),
        selectedQueue,
        selectedAuth,
        selectedChat,
        requestContent,
        ::StationContent,
    )

    val uiState: StateFlow<MainUiState> = combine(
        stations.observeStations(),
        stations.observeSelectedStation(),
        playback.state,
        stationContent,
        destination,
    ) { all, selected, playbackState, content, selectedDestination ->
        val selectedQueueState = content.queue.takeIf { it.stationId == selected.id }
            ?: QueueState(selected.id)
        val selectedAuthState = content.auth.takeIf { it.stationId == selected.id }
        val resolvedRequests = content.requestContent.requests
            .takeIf { it.stationId == selected.id }
            ?.resolveAvailability(selected.id, selectedQueueState, selectedAuthState?.status == AuthStatus.SignedIn)
        val resolvedFavorites = content.requestContent.favorites
            .takeIf { it.stationId == selected.id }
            ?.resolveAvailability(selected.id, selectedQueueState)
        MainUiState(
            stations = all,
            selectedStation = selected,
            playback = playbackState,
            nowPlaying = content.nowPlaying.takeIf { it.stationId == selected.id }
                ?: NowPlayingState(stationId = selected.id),
            queue = selectedQueueState,
            auth = selectedAuthState,
            chat = content.chat.takeIf { it.stationId == selected.id },
            requests = resolvedRequests,
            favorites = resolvedFavorites,
            destination = selectedDestination,
        )
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    init {
        viewModelScope.launch {
            stations.observeSelectedStation().collect(playback::selectStation)
        }
        viewModelScope.launch {
            stations.observeSelectedStation().collect { station -> auth.restoreSession(station.id) }
        }
    }

    fun selectStation(id: StationId) = viewModelScope.launch { stations.selectStation(id) }
    fun play() = playback.play()
    fun pause() = playback.pause()
    fun stop() = playback.stop()
    fun selectDestination(destination: MainDestination) {
        this.destination.value = destination
        if (destination == MainDestination.Favorites) {
            viewModelScope.launch {
                val stationId = stations.observeSelectedStation().first().id
                if (favorites.observeFavorites(stationId).first().status == FavoriteTracksLoadStatus.Idle) {
                    favorites.refresh(stationId)
                }
            }
        }
    }

    fun refreshQueue() = viewModelScope.launch {
        queue.refresh(stations.observeSelectedStation().first().id)
    }

    fun refreshAuth() = viewModelScope.launch {
        auth.refreshChallenge(stations.observeSelectedStation().first().id)
    }

    fun refreshChat() = viewModelScope.launch {
        chat.refresh(stations.observeSelectedStation().first().id)
    }

    fun refreshFavorites() = viewModelScope.launch {
        favorites.refresh(stations.observeSelectedStation().first().id)
    }

    fun sendChatMessage(message: String) = viewModelScope.launch {
        chat.sendMessage(stations.observeSelectedStation().first().id, message)
    }

    fun signIn(username: String, password: String, securityCode: String) = viewModelScope.launch {
        auth.signIn(stations.observeSelectedStation().first().id, username, password, securityCode)
    }

    fun signOut() = viewModelScope.launch {
        val stationId = stations.observeSelectedStation().first().id
        auth.signOut(stationId)
        favorites.clear(stationId)
    }

    fun searchRequests(query: String, field: RequestSearchField) = viewModelScope.launch {
        requests.search(stations.observeSelectedStation().first().id, query, field)
    }

    fun suggestRequest(mode: RequestSuggestionMode) = viewModelScope.launch {
        requests.suggest(stations.observeSelectedStation().first().id, mode)
    }

    fun openRequestAlbum(albumId: String) = viewModelScope.launch {
        requests.openAlbum(stations.observeSelectedStation().first().id, albumId)
    }

    fun prepareSongRequest(songId: String) = viewModelScope.launch {
        requests.prepareRequest(stations.observeSelectedStation().first().id, songId)
    }

    fun prepareFavoriteRequest(track: FavoriteTrack) = viewModelScope.launch {
        track.requestTrack?.let { requests.prepareRequest(stations.observeSelectedStation().first().id, it) }
    }

    fun cancelSongRequest() = viewModelScope.launch {
        requests.cancelRequest(stations.observeSelectedStation().first().id)
    }

    fun confirmSongRequest(message: String) = viewModelScope.launch {
        val stationId = stations.observeSelectedStation().first().id
        queue.refresh(stationId)
        requests.confirmRequest(stationId, queue.currentQueue(stationId), message)
    }

    class Factory(
        private val stations: StationRepository,
        private val playback: PlaybackController,
        private val nowPlaying: NowPlayingRepository,
        private val queue: QueueRepository,
        private val auth: AuthRepository,
        private val chat: ChatRepository,
        private val requests: SongRequestRepository,
        private val favorites: FavoriteTracksRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(stations, playback, nowPlaying, queue, auth, chat, requests, favorites) as T
    }
}

private data class StationContent(
    val nowPlaying: NowPlayingState,
    val queue: QueueState,
    val auth: AuthState,
    val chat: ChatState,
    val requestContent: RequestContent,
)

private data class RequestContent(
    val requests: SongRequestState,
    val favorites: FavoriteTracksState,
)

private fun SongRequestState.resolveAvailability(
    stationId: StationId,
    queue: QueueState,
    signedIn: Boolean,
): SongRequestState = copy(
    tracks = tracks.map { track -> track.resolveAvailability(stationId, queue, signedIn) },
    pendingRequest = pendingRequest?.resolveAvailability(stationId, queue, signedIn),
)

private fun com.codeframe78.twentyfourseven.player.domain.RequestableTrack.resolveAvailability(
    stationId: StationId,
    queue: QueueState,
    signedIn: Boolean,
): com.codeframe78.twentyfourseven.player.domain.RequestableTrack {
        val queueAvailability = TrackRequestAvailabilityResolver.resolve(
            stationId,
            identity,
            availability,
            queue,
        )
        val resolved = if (
            !signedIn && queueAvailability.status !in setOf(
                TrackRequestStatus.StationUnavailable,
                TrackRequestStatus.RequestsUnavailable,
                TrackRequestStatus.Unknown,
            )
        ) {
            TrackRequestAvailability(TrackRequestStatus.AuthenticationRequired)
        } else {
            queueAvailability
        }
        return copy(eligible = resolved.canRequest, availability = resolved)
}

private fun FavoriteTracksState.resolveAvailability(stationId: StationId, queue: QueueState): FavoriteTracksState = copy(
    tracks = tracks.map { track ->
        val resolved = TrackRequestAvailabilityResolver.resolve(stationId, track.identity, track.availability, queue)
        track.copy(
            availability = resolved,
            requestTrack = track.requestTrack?.copy(
                eligible = resolved.canRequest,
                albumTitle = track.album,
                availability = resolved,
            ),
        )
    },
)

