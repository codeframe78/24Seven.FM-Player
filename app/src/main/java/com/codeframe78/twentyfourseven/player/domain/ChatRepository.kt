package com.codeframe78.twentyfourseven.player.domain

import kotlinx.coroutines.flow.Flow

enum class ChatLoadStatus { Unavailable, Loading, Ready, Error }

data class ChatMessage(
    val authorDisplayName: String,
    val messageText: String,
)

data class ChatState(
    val stationId: StationId,
    val status: ChatLoadStatus = ChatLoadStatus.Unavailable,
    val messages: List<ChatMessage> = emptyList(),
    val errorMessage: String? = null,
)

interface ChatRepository {
    fun observeChat(stationId: StationId): Flow<ChatState>

    suspend fun refresh(stationId: StationId)
}
