package com.dezdeqness.analytics.data

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AptabaseEventPayload
import com.dezdeqness.analytics.data.db.AptabaseEventDao
import com.dezdeqness.analytics.data.db.AptabaseEventLocal
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class PendingAptabaseEvent(
    val id: Long,
    val payload: AptabaseEventPayload,
)

@OptIn(ExperimentalTime::class)
class AptabaseEventStore(
    private val aptabaseEventDao: AptabaseEventDao,
    private val json: Json,
    private val logger: Logger = Logger.withTag("AptabaseEventStore"),
) {
    suspend fun append(event: AptabaseEventPayload) {
        aptabaseEventDao.insert(
            AptabaseEventLocal(
                payload = json.encodeToString(event),
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
            )
        )
    }

    suspend fun getBatch(limit: Int): List<PendingAptabaseEvent> {
        val invalidIds = mutableListOf<Long>()
        val events = mutableListOf<PendingAptabaseEvent>()

        aptabaseEventDao.getBatch(limit).forEach { event ->
            runCatching {
                PendingAptabaseEvent(
                    id = event.id,
                    payload = json.decodeFromString<AptabaseEventPayload>(event.payload),
                )
            }
                .onSuccess(events::add)
                .onFailure { throwable ->
                    invalidIds += event.id
                    logger.w(throwable) { "Dropping invalid persisted Aptabase event." }
                }
        }

        if (invalidIds.isNotEmpty()) {
            aptabaseEventDao.deleteByIds(invalidIds)
        }

        return events
    }

    suspend fun delete(ids: List<Long>) {
        if (ids.isEmpty()) return
        aptabaseEventDao.deleteByIds(ids)
    }

    suspend fun count(): Int = aptabaseEventDao.count()
}
