package com.codeframe78.twentyfourseven.player.data

import com.codeframe78.twentyfourseven.player.domain.QueueRepository
import com.codeframe78.twentyfourseven.player.domain.QueueState
import com.codeframe78.twentyfourseven.player.domain.StationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UnavailableQueueRepository : QueueRepository {
    override fun observeQueue(stationId: StationId): Flow<QueueState> = flowOf(QueueState(stationId))

    override suspend fun refresh(stationId: StationId) = Unit
}
