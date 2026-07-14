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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeframe78.twentyfourseven.player.domain.PlaybackStatus
import com.codeframe78.twentyfourseven.player.domain.StationCapabilities
import com.codeframe78.twentyfourseven.player.domain.StationId
import com.codeframe78.twentyfourseven.player.domain.StreamFormat

private val navigationItems = listOf(
    NavigationItem(MainDestination.Player, "Player", Icons.Default.Radio),
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
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 600.dp) {
            TabletShell(state, onSelectStation, onSelectDestination, onPlay, onPause, onStop)
        } else {
            PhoneShell(state, onSelectStation, onSelectDestination, onPlay, onPause, onStop)
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
) {
    Scaffold(
        topBar = { StationTopBar(state, onSelectStation) },
        bottomBar = {
            Column {
                if (state.destination != MainDestination.Player) {
                    MiniPlayer(state, onSelectDestination, onPlay, onPause)
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
        DestinationContent(state, padding, onPlay, onPause, onStop)
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
            topBar = { StationTopBar(state, onSelectStation) },
            bottomBar = {
                if (state.destination != MainDestination.Player) {
                    MiniPlayer(state, onSelectDestination, onPlay, onPause)
                }
            },
        ) { padding ->
            DestinationContent(state, padding, onPlay, onPause, onStop)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationTopBar(state: MainUiState, onSelectStation: (StationId) -> Unit) {
    var menuOpen by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = {
            Box {
                TextButton(onClick = { menuOpen = true }) {
                    Text(state.selectedStation?.name ?: "Choose station")
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    state.stations.forEach { station ->
                        DropdownMenuItem(
                            text = { Text(station.name) },
                            onClick = {
                                onSelectStation(station.id)
                                menuOpen = false
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun DestinationContent(
    state: MainUiState,
    padding: PaddingValues,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
) {
    when (state.destination) {
        MainDestination.Player -> PlayerScreen(state, padding, onPlay, onPause, onStop)
        MainDestination.Chat -> FeatureScreen(
            title = "Chat",
            description = if (state.selectedStation?.capabilities?.supportsChat == true) {
                "Chat support is available for this station but its verified native transport is not implemented yet."
            } else {
                "No supported chat connection has been verified for this station yet."
            },
            icon = Icons.AutoMirrored.Filled.Chat,
            padding = padding,
        )
        MainDestination.Queue -> FeatureScreen(
            title = "Queue",
            description = if (state.selectedStation?.capabilities?.supportsQueue == true) {
                "Queue support is available for this station but its verified data source is not implemented yet."
            } else {
                "No supported queue or history source has been verified for this station yet."
            },
            icon = Icons.AutoMirrored.Filled.QueueMusic,
            padding = padding,
        )
        MainDestination.More -> MoreScreen(state, padding)
    }
}

@Composable
private fun PlayerScreen(
    state: MainUiState,
    padding: PaddingValues,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(state.selectedStation?.shortName ?: "24seven.FM", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(12.dp))
        Text(
            state.nowPlaying.displayTitle ?: "Live radio",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            state.playback.errorMessage ?: state.selectedStation?.description.orEmpty(),
            color = if (state.playback.status == PlaybackStatus.Error) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        val active = state.playback.status.isActive
        FilledIconButton(
            onClick = if (active) onPause else onPlay,
            enabled = state.selectedStation?.streams?.isNotEmpty() == true,
            shape = CircleShape,
        ) {
            Icon(
                if (active) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (active) "Pause" else "Play",
            )
        }
        TextButton(onClick = onStop, enabled = state.playback.status != PlaybackStatus.Idle) {
            Icon(Icons.Default.Stop, contentDescription = null)
            Text("Stop")
        }
        Text("LIVE • ${state.playback.status.displayName}", style = MaterialTheme.typography.labelLarge)
        state.selectedStation?.streams?.minByOrNull { it.priority }?.qualityLabel?.let { quality ->
            Text(quality, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
private fun MoreScreen(state: MainUiState, padding: PaddingValues) {
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
        Text(
            "Features remain unavailable until their station-specific sources and behavior are verified.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CapabilityCard(capabilities: StationCapabilities) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CapabilityRow("Chat", capabilities.supportsChat)
            CapabilityRow("Queue", capabilities.supportsQueue)
            CapabilityRow("History", capabilities.supportsHistory)
            CapabilityRow("Requests", capabilities.supportsRequests)
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

@Composable
private fun MiniPlayer(
    state: MainUiState,
    onSelectDestination: (MainDestination) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) {
    Surface(
        onClick = { onSelectDestination(MainDestination.Player) },
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Radio, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    state.nowPlaying.displayTitle ?: "Live radio",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    state.selectedStation?.shortName.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(
                onClick = if (state.playback.status.isActive) onPause else onPlay,
                enabled = state.selectedStation?.streams?.isNotEmpty() == true,
            ) {
                Icon(
                    if (state.playback.status.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.playback.status.isActive) "Pause" else "Play",
                )
            }
        }
    }
}

private val com.codeframe78.twentyfourseven.player.domain.StreamVariant.qualityLabel: String?
    get() {
        val formatLabel = when (format) {
            StreamFormat.Aac -> "AAC"
            StreamFormat.Mp3 -> "MP3"
            StreamFormat.Hls -> "HLS"
            StreamFormat.Unknown -> null
        }
        return listOfNotNull(formatLabel, bitrateKbps?.let { "$it kbps" })
            .takeIf(List<String>::isNotEmpty)
            ?.joinToString(" • ")
    }

private val PlaybackStatus.isActive: Boolean
    get() = this in setOf(
        PlaybackStatus.Connecting,
        PlaybackStatus.Buffering,
        PlaybackStatus.Playing,
        PlaybackStatus.Retrying,
    )

private val PlaybackStatus.displayName: String
    get() = when (this) {
        PlaybackStatus.Idle -> "Not connected"
        PlaybackStatus.Connecting -> "Connecting"
        PlaybackStatus.Buffering -> "Buffering"
        PlaybackStatus.Playing -> "Playing"
        PlaybackStatus.Paused -> "Paused"
        PlaybackStatus.Retrying -> "Trying fallback"
        PlaybackStatus.Error -> "Playback error"
    }
