package com.codeframe78.twentyfourseven.player.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeframe78.twentyfourseven.player.domain.AuthStatus
import com.codeframe78.twentyfourseven.player.domain.ChatLoadStatus
import com.codeframe78.twentyfourseven.player.domain.HistoryTrack
import com.codeframe78.twentyfourseven.player.domain.FavoriteTrack
import com.codeframe78.twentyfourseven.player.domain.QueueLoadStatus
import com.codeframe78.twentyfourseven.player.domain.QueueTrack
import com.codeframe78.twentyfourseven.player.domain.StationCapabilities
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.RequestSearchField
import com.codeframe78.twentyfourseven.player.domain.RequestSuggestionMode
import com.codeframe78.twentyfourseven.player.domain.SongRequestLoadStatus
import coil3.compose.AsyncImage
import com.codeframe78.twentyfourseven.player.R

private val navigationItems = listOf(
    NavigationItem(MainDestination.Player, "Player", Icons.Default.Radio),
    NavigationItem(MainDestination.Favorites, "Favorites", Icons.Default.Favorite),
    NavigationItem(MainDestination.Chat, "Chat", Icons.AutoMirrored.Filled.Chat),
    NavigationItem(MainDestination.Queue, "Queue", Icons.AutoMirrored.Filled.QueueMusic),
    NavigationItem(MainDestination.More, "More", Icons.Default.MoreHoriz),
)

private data class NavigationItem(
    val destination: MainDestination,
    val label: String,
    val icon: ImageVector,
)

@Composable
internal fun RadioApp(
    state: MainUiState,
    onSelectStation: (StationId) -> Unit,
    onSelectDestination: (MainDestination) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onRefreshQueue: () -> Unit,
    onRefreshFavorites: () -> Unit = {},
    onRefreshChat: () -> Unit = {},
    onSendChatMessage: (String) -> Unit = {},
    onRefreshAuth: () -> Unit = {},
    onSignIn: (String, String, String) -> Unit = { _, _, _ -> },
    onSignOut: () -> Unit = {},
    onSearchRequests: (String, RequestSearchField) -> Unit = { _, _ -> },
    onSuggestRequest: (RequestSuggestionMode) -> Unit = {},
    onOpenRequestAlbum: (String) -> Unit = {},
    onPrepareRequest: (String) -> Unit = {},
    onPrepareFavoriteRequest: (FavoriteTrack) -> Unit = {},
    onCancelRequest: () -> Unit = {},
    onConfirmRequest: (String) -> Unit = {},
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 600.dp) {
            TabletShell(state, onSelectStation, onSelectDestination, onPlay, onPause, onStop, onRefreshQueue, onRefreshFavorites, onRefreshChat, onSendChatMessage, onRefreshAuth, onSignIn, onSignOut, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onPrepareFavoriteRequest, onCancelRequest, onConfirmRequest)
        } else {
            PhoneShell(state, onSelectStation, onSelectDestination, onPlay, onPause, onStop, onRefreshQueue, onRefreshFavorites, onRefreshChat, onSendChatMessage, onRefreshAuth, onSignIn, onSignOut, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onPrepareFavoriteRequest, onCancelRequest, onConfirmRequest)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhoneShell(
    state: MainUiState,
    onSelectStation: (StationId) -> Unit,
    onSelectDestination: (MainDestination) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onRefreshQueue: () -> Unit,
    onRefreshFavorites: () -> Unit,
    onRefreshChat: () -> Unit,
    onSendChatMessage: (String) -> Unit,
    onRefreshAuth: () -> Unit,
    onSignIn: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    onSearchRequests: (String, RequestSearchField) -> Unit,
    onSuggestRequest: (RequestSuggestionMode) -> Unit,
    onOpenRequestAlbum: (String) -> Unit,
    onPrepareRequest: (String) -> Unit,
    onPrepareFavoriteRequest: (FavoriteTrack) -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    Scaffold(
        topBar = { StationTopBar(state, onSelectDestination) },
        bottomBar = {
            Column {
                if (state.destination != MainDestination.Player) {
                    PersistentMiniPlayer(state, onSelectDestination, onPlay, onPause)
                }
                NavigationBar(Modifier.testTag("phone_navigation_bar")) {
                    navigationItems.forEach { item ->
                        NavigationBarItem(
                            selected = state.destination == item.destination,
                            onClick = { onSelectDestination(item.destination) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        DestinationContent(state, padding, onSelectStation, onSelectDestination, onPlay, onPause, onStop, onRefreshQueue, onRefreshFavorites, onRefreshChat, onSendChatMessage, onRefreshAuth, onSignIn, onSignOut, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onPrepareFavoriteRequest, onCancelRequest, onConfirmRequest)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabletShell(
    state: MainUiState,
    onSelectStation: (StationId) -> Unit,
    onSelectDestination: (MainDestination) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onRefreshQueue: () -> Unit,
    onRefreshFavorites: () -> Unit,
    onRefreshChat: () -> Unit,
    onSendChatMessage: (String) -> Unit,
    onRefreshAuth: () -> Unit,
    onSignIn: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    onSearchRequests: (String, RequestSearchField) -> Unit,
    onSuggestRequest: (RequestSuggestionMode) -> Unit,
    onOpenRequestAlbum: (String) -> Unit,
    onPrepareRequest: (String) -> Unit,
    onPrepareFavoriteRequest: (FavoriteTrack) -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    Row(Modifier.fillMaxSize()) {
        NavigationRail(Modifier.fillMaxHeight().testTag("tablet_navigation_rail")) {
            Spacer(Modifier.height(12.dp))
            navigationItems.forEach { item ->
                NavigationRailItem(
                    selected = state.destination == item.destination,
                    onClick = { onSelectDestination(item.destination) },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                )
            }
        }
        VerticalDivider(Modifier.fillMaxHeight())
        Scaffold(
            modifier = Modifier.weight(1f),
            topBar = { StationTopBar(state, onSelectDestination) },
            bottomBar = {
                if (state.destination != MainDestination.Player) {
                    PersistentMiniPlayer(state, onSelectDestination, onPlay, onPause)
                }
            },
        ) { padding ->
            DestinationContent(state, padding, onSelectStation, onSelectDestination, onPlay, onPause, onStop, onRefreshQueue, onRefreshFavorites, onRefreshChat, onSendChatMessage, onRefreshAuth, onSignIn, onSignOut, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onPrepareFavoriteRequest, onCancelRequest, onConfirmRequest)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationTopBar(
    state: MainUiState,
    onSelectDestination: (MainDestination) -> Unit,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.app_logo),
                contentDescription = "24Seven.FM logo",
                modifier = Modifier.padding(start = 12.dp).size(40.dp).clip(RoundedCornerShape(10.dp)),
            )
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("24Seven.FM", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = { onSelectDestination(MainDestination.Player) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                ) {
                    Text(
                        state.selectedStation?.name ?: "Choose station",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { onSelectDestination(MainDestination.More) }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Account and station options")
            }
        },
    )
}

@Composable
private fun DestinationContent(
    state: MainUiState,
    padding: PaddingValues,
    onSelectStation: (StationId) -> Unit,
    onSelectDestination: (MainDestination) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onRefreshQueue: () -> Unit,
    onRefreshFavorites: () -> Unit,
    onRefreshChat: () -> Unit,
    onSendChatMessage: (String) -> Unit,
    onRefreshAuth: () -> Unit,
    onSignIn: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    onSearchRequests: (String, RequestSearchField) -> Unit,
    onSuggestRequest: (RequestSuggestionMode) -> Unit,
    onOpenRequestAlbum: (String) -> Unit,
    onPrepareRequest: (String) -> Unit,
    onPrepareFavoriteRequest: (FavoriteTrack) -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    when (state.destination) {
        MainDestination.Player -> AdaptivePlayerScreen(state, padding, onSelectStation, onPlay, onPause, onStop)
        MainDestination.Favorites -> FavoriteTracksScreen(
            state = state,
            padding = padding,
            onRefresh = onRefreshFavorites,
            onPrepareRequest = onPrepareFavoriteRequest,
            onCancelRequest = onCancelRequest,
            onConfirmRequest = onConfirmRequest,
            onOpenAccount = { onSelectDestination(MainDestination.More) },
        )
        MainDestination.Chat -> ChatScreen(state, padding, onRefreshChat, onSendChatMessage)
        MainDestination.Queue -> QueueScreen(state, padding, onRefreshQueue)
        MainDestination.More -> MoreScreen(state, padding, onRefreshAuth, onSignIn, onSignOut, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onCancelRequest, onConfirmRequest)
    }
}

@Composable
private fun ChatScreen(
    state: MainUiState,
    padding: PaddingValues,
    onRefresh: () -> Unit,
    onSendMessage: (String) -> Unit,
) {
    val chat = state.chat
    when {
        state.selectedStation?.capabilities?.supportsChat != true ||
            chat == null || chat.status == ChatLoadStatus.Unavailable -> FeatureScreen(
                title = "Chat",
                description = "No supported chat connection has been verified for this station yet.",
                icon = Icons.AutoMirrored.Filled.Chat,
                padding = padding,
            )
        chat.status == ChatLoadStatus.Loading -> Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        chat.status == ChatLoadStatus.Error -> Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Chat unavailable", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                chat.errorMessage ?: "The station chat could not be refreshed.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text("Try again")
            }
        }
        else -> ChatMessages(state, padding, onRefresh, onSendMessage)
    }
}

@Composable
private fun ChatMessages(
    state: MainUiState,
    padding: PaddingValues,
    onRefresh: () -> Unit,
    onSendMessage: (String) -> Unit,
) {
    val chat = checkNotNull(state.chat)
    var draft by remember(state.selectedStation?.id) { mutableStateOf("") }
    var awaitingSend by remember(state.selectedStation?.id) { mutableStateOf(false) }
    LaunchedEffect(chat.isSending, chat.sendErrorMessage, chat.messages) {
        if (awaitingSend && !chat.isSending) {
            if (chat.sendErrorMessage != null) {
                awaitingSend = false
            } else if (chat.messages.any { it.messageText == draft }) {
                draft = ""
                awaitingSend = false
            }
        }
    }
    Column(Modifier.fillMaxSize().padding(padding)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Station chat", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh chat")
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (chat.messages.isEmpty()) {
                item { EmptyTrackList("No recent chat messages are available.") }
            } else {
                itemsIndexed(
                    chat.messages,
                    key = { index, message ->
                        "$index-${message.postedAtLabel}-${message.authorDisplayName}-${message.messageText}"
                    },
                ) { _, message ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth()) {
                                Text(
                                    message.authorDisplayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.weight(1f),
                                )
                                message.postedAtLabel?.let { timestamp ->
                                    Text(
                                        timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(message.messageText)
                        }
                    }
                }
            }
        }
        if (state.auth?.status == AuthStatus.SignedIn) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                chat.sendErrorMessage?.let { error ->
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = draft,
                        onValueChange = { if (it.length <= 255) draft = it },
                        label = { Text("Message") },
                        supportingText = { Text("${draft.length}/255") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = !chat.isSending,
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            awaitingSend = true
                            onSendMessage(draft)
                        },
                        enabled = draft.isNotBlank() && !chat.isSending,
                    ) {
                        Text(if (chat.isSending) "Sending" else "Send")
                    }
                }
            }
        } else {
            Text(
                "Sign in from More to send messages.",
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun QueueScreen(state: MainUiState, padding: PaddingValues, onRefresh: () -> Unit) {
    val queue = state.queue
    val supportsQueueOrHistory = state.selectedStation?.capabilities?.run {
        supportsQueue || supportsHistory
    } == true
    when {
        !supportsQueueOrHistory ||
            queue == null || queue.status == QueueLoadStatus.Unavailable -> FeatureScreen(
                title = "Queue",
                description = "No supported queue or history source has been verified for this station yet.",
                icon = Icons.AutoMirrored.Filled.QueueMusic,
                padding = padding,
            )
        queue.status == QueueLoadStatus.Loading -> Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        queue.status == QueueLoadStatus.Error -> Column(
            Modifier.fillMaxSize().padding(padding).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Queue unavailable", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                queue.errorMessage ?: "The station data could not be refreshed.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text("Try again")
            }
        }
        else -> QueueLists(
            queue.upcoming,
            queue.recentlyPlayed,
            queue.isStale,
            queue.errorMessage,
            padding,
            onRefresh,
        )
    }
}

@Composable
private fun QueueLists(
    upcoming: List<QueueTrack>,
    history: List<HistoryTrack>,
    isStale: Boolean,
    refreshMessage: String?,
    padding: PaddingValues,
    onRefresh: () -> Unit,
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Up next", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh queue")
                }
            }
        }
        if (isStale) {
            item {
                Text(
                    refreshMessage ?: "Showing cached Queue data while a fresh copy is unavailable.",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        if (upcoming.isEmpty()) {
            item { EmptyTrackList("The station queue is currently empty.") }
        } else {
            items(upcoming, key = { "queue-${it.position}-${it.displayTitle}" }) { track ->
                TrackCard(
                    track.position.toString(),
                    track.displayTitle,
                    track.artistName,
                    track.albumTitle,
                    track.durationLabel,
                    track.artworkUrl,
                    track.requesterName,
                    track.requestMessage,
                )
            }
        }
        item {
            Spacer(Modifier.height(8.dp))
            Text("Recently played", style = MaterialTheme.typography.headlineSmall)
        }
        if (history.isEmpty()) {
            item { EmptyTrackList("No recent history is available.") }
        } else {
            items(history) { track ->
                TrackCard(
                    null,
                    track.displayTitle,
                    track.artistName,
                    track.albumTitle,
                    track.durationLabel,
                    track.artworkUrl,
                    track.requesterName,
                    track.requestMessage,
                )
            }
        }
    }
}

@Composable
private fun EmptyTrackList(message: String) {
    Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun TrackCard(
    position: String?,
    title: String,
    artist: String?,
    album: String?,
    duration: String?,
    artworkUrl: String? = null,
    requesterName: String? = null,
    requestMessage: String? = null,
) {
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            position?.let {
                Text(it, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(14.dp))
            }
            artworkUrl?.let {
                AsyncImage(
                    model = it,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)),
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                listOfNotNull(artist, album).takeIf(List<String>::isNotEmpty)?.let { details ->
                    Text(
                        details.joinToString(" • "),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                requesterName?.let {
                    Text(
                        "Requested by $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                requestMessage?.let {
                    Text(
                        "“$it”",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            duration?.let {
                Spacer(Modifier.width(12.dp))
                Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun FeatureScreen(title: String, description: String, icon: ImageVector, padding: PaddingValues) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun MoreScreen(
    state: MainUiState,
    padding: PaddingValues,
    onRefreshAuth: () -> Unit,
    onSignIn: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
    onSearchRequests: (String, RequestSearchField) -> Unit,
    onSuggestRequest: (RequestSuggestionMode) -> Unit,
    onOpenRequestAlbum: (String) -> Unit,
    onPrepareRequest: (String) -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("About this station", style = MaterialTheme.typography.titleLarge)
                Text(state.selectedStation?.name.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(state.selectedStation?.description.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        Text("Feature availability", style = MaterialTheme.typography.titleMedium)
        CapabilityCard(state.selectedStation?.capabilities ?: StationCapabilities())
        AccountSection(state, onRefreshAuth, onSignIn, onSignOut)
        SongRequestSection(state, onSearchRequests, onSuggestRequest, onOpenRequestAlbum, onPrepareRequest, onCancelRequest, onConfirmRequest)
        PrivacySection()
        Text(
            "Features remain unavailable until their station-specific sources and behavior are verified.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AccountSection(
    state: MainUiState,
    onRefresh: () -> Unit,
    onSignIn: (String, String, String) -> Unit,
    onSignOut: () -> Unit,
) {
    val auth = state.auth
    var username by remember(state.selectedStation?.id) { mutableStateOf("") }
    var password by remember(state.selectedStation?.id) { mutableStateOf("") }
    var securityCode by remember(state.selectedStation?.id) { mutableStateOf("") }
    Text("Account", style = MaterialTheme.typography.titleMedium)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when (auth?.status ?: AuthStatus.Unavailable) {
                AuthStatus.SignedIn -> {
                    Text("Signed in as ${auth?.displayName.orEmpty()}", fontWeight = FontWeight.Medium)
                    Button(onClick = onSignOut) { Text("Sign out") }
                }
                AuthStatus.LoadingChallenge, AuthStatus.SigningIn -> {
                    CircularProgressIndicator()
                    Text(if (auth?.status == AuthStatus.SigningIn) "Signing in…" else "Loading secure sign in…")
                }
                AuthStatus.Unavailable -> Button(onClick = onRefresh) { Text("Load sign in") }
                AuthStatus.SignedOut, AuthStatus.Error -> {
                    auth?.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    OutlinedTextField(username, { username = it }, label = { Text("Username") }, singleLine = true)
                    OutlinedTextField(
                        password,
                        { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    auth?.challengeImageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Security code image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(112.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(12.dp),
                        )
                    }
                    OutlinedTextField(
                        securityCode,
                        { securityCode = it.filter(Char::isLetterOrDigit) },
                        label = { Text("Security code") },
                        singleLine = true,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            onSignIn(username, password, securityCode)
                            password = ""
                            securityCode = ""
                        }) { Text("Sign in") }
                        TextButton(onClick = onRefresh) { Text("New code") }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongRequestSection(
    state: MainUiState,
    onSearch: (String, RequestSearchField) -> Unit,
    onSuggest: (RequestSuggestionMode) -> Unit,
    onOpenAlbum: (String) -> Unit,
    onPrepareRequest: (String) -> Unit,
    onCancelRequest: () -> Unit,
    onConfirmRequest: (String) -> Unit,
) {
    val requests = state.requests
    var query by remember(state.selectedStation?.id) { mutableStateOf("") }
    var field by remember(state.selectedStation?.id) { mutableStateOf(RequestSearchField.Title) }
    var fieldMenuOpen by remember { mutableStateOf(false) }
    val signedIn = state.auth?.status == AuthStatus.SignedIn

    RequestConfirmationDialog(state, onCancelRequest, onConfirmRequest)

    Text("Song requests", style = MaterialTheme.typography.titleMedium)
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (state.selectedStation?.capabilities?.supportsRequests != true) {
                Text("Song requests have not been verified for this station.")
                return@Column
            }
            Text(
                "Search the station library. A request is only sent after you review and confirm one available track.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    TextButton(onClick = { fieldMenuOpen = true }) {
                        Text("Search by ${field.name.lowercase()}")
                    }
                    DropdownMenu(expanded = fieldMenuOpen, onDismissRequest = { fieldMenuOpen = false }) {
                        RequestSearchField.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    field = option
                                    fieldMenuOpen = false
                                },
                            )
                        }
                    }
                }
            }
            OutlinedTextField(
                value = query,
                onValueChange = { query = it.take(100) },
                label = { Text("Library search") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onSearch(query, field) },
                enabled = requests?.status != SongRequestLoadStatus.Loading &&
                    requests?.status != SongRequestLoadStatus.Submitting,
            ) { Text("Search") }

            Text(
                "Or let the station choose one available track.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { onSuggest(RequestSuggestionMode.Random) },
                enabled = requests?.status != SongRequestLoadStatus.Loading &&
                    requests?.status != SongRequestLoadStatus.Submitting,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Suggest a random track") }
            Button(
                onClick = { onSuggest(RequestSuggestionMode.LeastPlayed) },
                enabled = requests?.status != SongRequestLoadStatus.Loading &&
                    requests?.status != SongRequestLoadStatus.Submitting,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Suggest a least-played random track") }

            if (requests?.status == SongRequestLoadStatus.Loading || requests?.status == SongRequestLoadStatus.Submitting) {
                CircularProgressIndicator()
                Text(if (requests.status == SongRequestLoadStatus.Submitting) "Sending one request…" else "Loading station library…")
            }
            requests?.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            requests?.notice?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

            requests?.searchResults?.takeIf { it.isNotEmpty() }?.let { results ->
                Text("Search results", style = MaterialTheme.typography.titleSmall)
                results.forEach { result ->
                    Surface(
                        onClick = { onOpenAlbum(result.albumId) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 2.dp,
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(result.trackTitle, fontWeight = FontWeight.Medium)
                            Text(
                                listOfNotNull(result.albumTitle, result.year).joinToString(" • "),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text("View album", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            requests?.tracks?.takeIf { it.isNotEmpty() }?.let { tracks ->
                Text(requests.albumTitle ?: "Album tracks", style = MaterialTheme.typography.titleSmall)
                if (!signedIn) {
                    Text("Sign in to request a track. Library browsing remains available without an account.")
                }
                tracks.forEach { track ->
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(track.title, fontWeight = FontWeight.Medium)
                            Text(
                                listOfNotNull(track.artist, track.duration).joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            RequestStatusIndicator(
                                availability = track.availability,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                            track.availability.detail?.let { detail ->
                                Text(
                                    detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        if (track.availability.canRequest) {
                            TextButton(
                                onClick = { onPrepareRequest(track.songId) },
                                enabled = requests.status == SongRequestLoadStatus.Ready,
                            ) { Text("Request Now") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityCard(capabilities: StationCapabilities) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CapabilityRow("Authentication", capabilities.supportsAuthentication)
            CapabilityRow("Chat", capabilities.supportsChat)
            CapabilityRow("Favorites", capabilities.supportsFavorites)
            CapabilityRow("Queue", capabilities.supportsQueue)
            CapabilityRow("History", capabilities.supportsHistory)
            CapabilityRow("Requests", capabilities.supportsRequests)
            CapabilityRow("Request messages", capabilities.supportsRequestMessages)
        }
    }
}

@Composable
private fun CapabilityRow(label: String, supported: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(
            if (supported) "Available" else "Not verified",
            color = if (supported) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
    }
}
