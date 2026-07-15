package com.codeframe78.twentyfourseven.player.ui

import com.codeframe78.twentyfourseven.player.data.BootstrapStationRepository
import com.codeframe78.twentyfourseven.player.data.UnavailableAuthRepository
import com.codeframe78.twentyfourseven.player.data.UnavailableChatRepository
import com.codeframe78.twentyfourseven.player.data.UnavailableSongRequestRepository
import com.codeframe78.twentyfourseven.player.data.UnavailableFavoriteTracksRepository
import com.codeframe78.twentyfourseven.player.domain.ChatRepository
import com.codeframe78.twentyfourseven.player.domain.ChatState
import com.codeframe78.twentyfourseven.player.domain.PlaybackController
import com.codeframe78.twentyfourseven.player.domain.PlaybackState
import com.codeframe78.twentyfourseven.player.domain.NowPlayingRepository
import com.codeframe78.twentyfourseven.player.domain.NowPlayingState
import com.codeframe78.twentyfourseven.player.domain.QueueLoadStatus
import com.codeframe78.twentyfourseven.player.domain.QueueRepository
import com.codeframe78.twentyfourseven.player.domain.QueueState
import com.codeframe78.twentyfourseven.player.domain.QueueTrack
import com.codeframe78.twentyfourseven.player.domain.FavoriteTrack
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksLoadStatus
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksRepository
import com.codeframe78.twentyfourseven.player.domain.FavoriteTracksState
import com.codeframe78.twentyfourseven.player.domain.RequestableTrack
import com.codeframe78.twentyfourseven.player.domain.TrackRequestStatus
import com.codeframe78.twentyfourseven.player.domain.Station
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selection and playback actions are delegated through domain contracts`() = runTest(dispatcher) {
        val stations = BootstrapStationRepository()
        val playback = FakePlaybackController()
        val queue = FakeQueueRepository()
        val viewModel = MainViewModel(
            stations,
            playback,
            FakeNowPlayingRepository(),
            queue,
            UnavailableAuthRepository(),
            UnavailableChatRepository(),
            UnavailableSongRequestRepository(),
            UnavailableFavoriteTracksRepository(),
        )
        advanceUntilIdle()

        assertEquals("sst", playback.selectedStation?.id?.value)

        viewModel.selectStation(StationId("adagio"))
        viewModel.play()
        viewModel.pause()
        viewModel.stop()
        viewModel.refreshQueue()
        advanceUntilIdle()

        assertEquals("adagio", playback.selectedStation?.id?.value)
        assertEquals(1, playback.playCalls)
        assertEquals(1, playback.pauseCalls)
        assertEquals(1, playback.stopCalls)
        assertEquals(StationId("adagio"), queue.refreshedStation)
    }

    @Test
    fun `now playing title updates remain scoped to the selected station`() = runTest(dispatcher) {
        val nowPlaying = FakeNowPlayingRepository()
        val viewModel = MainViewModel(
            BootstrapStationRepository(),
            FakePlaybackController(),
            nowPlaying,
            FakeQueueRepository(),
            UnavailableAuthRepository(),
            UnavailableChatRepository(),
            UnavailableSongRequestRepository(),
            UnavailableFavoriteTracksRepository(),
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        nowPlaying.state.value = NowPlayingState(StationId("sst"), "First raw title")
        advanceUntilIdle()
        assertEquals("First raw title", viewModel.uiState.value.nowPlaying.displayTitle)

        nowPlaying.state.value = NowPlayingState(StationId("sst"), "Updated raw title")
        advanceUntilIdle()
        assertEquals("Updated raw title", viewModel.uiState.value.nowPlaying.displayTitle)

        viewModel.selectStation(StationId("adagio"))
        advanceUntilIdle()
        assertNull(viewModel.uiState.value.nowPlaying.displayTitle)
    }

    @Test
    fun `destination selection is exposed as immutable ui state`() = runTest(dispatcher) {
        val viewModel = MainViewModel(
            BootstrapStationRepository(),
            FakePlaybackController(),
            FakeNowPlayingRepository(),
            FakeQueueRepository(),
            UnavailableAuthRepository(),
            UnavailableChatRepository(),
            UnavailableSongRequestRepository(),
            UnavailableFavoriteTracksRepository(),
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        assertEquals(MainDestination.Player, viewModel.uiState.value.destination)

        viewModel.selectDestination(MainDestination.Chat)
        advanceUntilIdle()

        assertEquals(MainDestination.Chat, viewModel.uiState.value.destination)
    }

    @Test
    fun `queue state follows selected station and stays immutable`() = runTest(dispatcher) {
        val queue = FakeQueueRepository()
        val viewModel = MainViewModel(
            BootstrapStationRepository(),
            FakePlaybackController(),
            FakeNowPlayingRepository(),
            queue,
            UnavailableAuthRepository(),
            UnavailableChatRepository(),
            UnavailableSongRequestRepository(),
            UnavailableFavoriteTracksRepository(),
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        assertEquals(StationId("sst"), viewModel.uiState.value.queue?.stationId)
        assertEquals(StationId("sst"), viewModel.uiState.value.auth?.stationId)

        viewModel.selectDestination(MainDestination.Queue)
        advanceUntilIdle()
        assertEquals(StationId("sst"), queue.observedStation)
        assertEquals(1, queue.activeObservations)

        viewModel.selectStation(StationId("death"))
        advanceUntilIdle()

        assertEquals(StationId("death"), viewModel.uiState.value.queue?.stationId)
        assertEquals(StationId("death"), viewModel.uiState.value.auth?.stationId)
        assertEquals(QueueLoadStatus.Unavailable, viewModel.uiState.value.queue?.status)
        assertEquals(StationId("death"), queue.observedStation)
        assertEquals(1, queue.activeObservations)

        viewModel.selectDestination(MainDestination.Player)
        advanceUntilIdle()
        assertEquals(0, queue.activeObservations)
    }

    @Test
    fun `chat state is station scoped and observed only on chat destination`() = runTest(dispatcher) {
        val chat = FakeChatRepository()
        val viewModel = MainViewModel(
            BootstrapStationRepository(),
            FakePlaybackController(),
            FakeNowPlayingRepository(),
            FakeQueueRepository(),
            UnavailableAuthRepository(),
            chat,
            UnavailableSongRequestRepository(),
            UnavailableFavoriteTracksRepository(),
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        advanceUntilIdle()

        assertEquals(StationId("sst"), viewModel.uiState.value.chat?.stationId)
        assertEquals(0, chat.activeObservations)

        viewModel.selectDestination(MainDestination.Chat)
        advanceUntilIdle()
        assertEquals(StationId("sst"), chat.observedStation)
        assertEquals(1, chat.activeObservations)

        viewModel.refreshChat()
        viewModel.sendChatMessage("Hello")
        advanceUntilIdle()
        assertEquals(StationId("sst"), chat.refreshedStation)
        assertEquals(StationId("sst"), chat.sentStation)
        assertEquals("Hello", chat.sentMessage)

        viewModel.selectStation(StationId("adagio"))
        advanceUntilIdle()
        assertEquals(StationId("adagio"), viewModel.uiState.value.chat?.stationId)
        assertEquals(StationId("adagio"), chat.observedStation)
        assertEquals(1, chat.activeObservations)

        viewModel.selectDestination(MainDestination.Player)
        advanceUntilIdle()
        assertEquals(0, chat.activeObservations)
    }

    @Test
    fun `favorites recalculate when selected station queue changes`() = runTest(dispatcher) {
        val stationId = StationId("sst")
        val queue = FakeQueueRepository()
        val favoriteRequest = RequestableTrack(
            albumId = "ALBUM_1",
            songId = "12345",
            title = "Example Track",
            artist = "Example Artist",
            eligible = true,
            albumTitle = "Example Album",
        )
        val favorites = FakeFavoriteTracksRepository(
            FavoriteTracksState(
                stationId,
                FavoriteTracksLoadStatus.Ready,
                tracks = listOf(
                    FavoriteTrack(
                        position = 1,
                        title = "Example Track",
                        album = "Example Album",
                        artist = "Example Artist",
                        requestTrack = favoriteRequest,
                    ),
                ),
            ),
        )
        val viewModel = MainViewModel(
            BootstrapStationRepository(),
            FakePlaybackController(),
            FakeNowPlayingRepository(),
            queue,
            UnavailableAuthRepository(),
            UnavailableChatRepository(),
            UnavailableSongRequestRepository(),
            favorites,
        )
        backgroundScope.launch { viewModel.uiState.collect() }
        viewModel.selectDestination(MainDestination.Favorites)
        queue.emit(QueueState(stationId, QueueLoadStatus.Ready))
        advanceUntilIdle()
        assertEquals(TrackRequestStatus.Available, viewModel.uiState.value.favorites?.tracks?.single()?.availability?.status)

        queue.emit(
            QueueState(
                stationId,
                QueueLoadStatus.Ready,
                upcoming = listOf(
                    QueueTrack(
                        1,
                        "Example Track",
                        albumId = "ALBUM_1",
                        artistName = "Example Artist",
                        albumTitle = "Example Album",
                    ),
                ),
            ),
        )
        advanceUntilIdle()

        assertEquals(TrackRequestStatus.InCurrentQueue, viewModel.uiState.value.favorites?.tracks?.single()?.availability?.status)
        assertEquals(1, queue.activeObservations)
    }

    private class FakeNowPlayingRepository : NowPlayingRepository {
        val state = MutableStateFlow(NowPlayingState())

        override fun observeNowPlaying() = state
    }

    private class FakePlaybackController : PlaybackController {
        override val state: StateFlow<PlaybackState> = MutableStateFlow(PlaybackState())
        var selectedStation: Station? = null
        var playCalls = 0
        var pauseCalls = 0
        var stopCalls = 0

        override fun selectStation(station: Station) {
            selectedStation = station
        }

        override fun play() {
            playCalls++
        }

        override fun pause() {
            pauseCalls++
        }

        override fun stop() {
            stopCalls++
        }
    }

    private class FakeQueueRepository : QueueRepository {
        var refreshedStation: StationId? = null
        var observedStation: StationId? = null
        var activeObservations = 0
        private val states = mutableMapOf<StationId, MutableStateFlow<QueueState>>()

        override fun observeQueue(stationId: StationId): Flow<QueueState> {
            observedStation = stationId
            return flow {
                activeObservations++
                try {
                    state(stationId).collect { emit(it) }
                } finally {
                    activeObservations--
                }
            }
        }

        override suspend fun refresh(stationId: StationId) {
            refreshedStation = stationId
        }

        override suspend fun currentQueue(stationId: StationId): QueueState = state(stationId).value

        fun emit(queue: QueueState) {
            state(queue.stationId).value = queue
        }

        private fun state(stationId: StationId) = states.getOrPut(stationId) {
            MutableStateFlow(QueueState(stationId))
        }
    }

    private class FakeFavoriteTracksRepository(initial: FavoriteTracksState) : FavoriteTracksRepository {
        private val state = MutableStateFlow(initial)
        override fun observeFavorites(stationId: StationId): Flow<FavoriteTracksState> = state
        override suspend fun refresh(stationId: StationId) = Unit
        override suspend fun clear(stationId: StationId) {
            state.value = FavoriteTracksState(stationId)
        }
    }

    private class FakeChatRepository : ChatRepository {
        var observedStation: StationId? = null
        var activeObservations = 0
        var refreshedStation: StationId? = null
        var sentStation: StationId? = null
        var sentMessage: String? = null

        override fun observeChat(stationId: StationId): Flow<ChatState> {
            observedStation = stationId
            return flow {
                activeObservations++
                emit(ChatState(stationId))
                try {
                    awaitCancellation()
                } finally {
                    activeObservations--
                }
            }
        }

        override suspend fun refresh(stationId: StationId) {
            refreshedStation = stationId
        }

        override suspend fun sendMessage(stationId: StationId, message: String) {
            sentStation = stationId
            sentMessage = message
        }
    }
}
