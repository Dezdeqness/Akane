package com.dezdeqness.analytics

import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.Analytics
import com.dezdeqness.analytics.core.AnalyticsProperties
import com.dezdeqness.analytics.core.AptabaseConfig
import com.dezdeqness.analytics.core.AptabaseEventPayload
import com.dezdeqness.analytics.core.AptabaseSystemProps
import com.dezdeqness.analytics.core.readPlatformSystemInfo
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random

class AptabaseAnalytics(
    private val config: AptabaseConfig,
    private val logger: Logger = Logger.withTag("AptabaseAnalytics"),
) : Analytics {

    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
    }

    private val platformSystemInfo = readPlatformSystemInfo()
    private val sessionId = newSessionId()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val queueMutex = Mutex()
    private val flushMutex = Mutex()
    private val queue = mutableListOf<AptabaseEventPayload>()

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        expectSuccess = false
    }

    init {
        scope.launch {
            while (true) {
                delay(config.flushIntervalMillis)
                flush()
            }
        }
    }

    override fun track(eventName: String, properties: AnalyticsProperties) {
        if (eventName.isBlank()) {
            logger.w { "Skipped Aptabase event with blank name." }
            return
        }

        val event = AptabaseEventPayload(
            timestamp = Clock.System.now().toString(),
            sessionId = sessionId,
            eventName = eventName,
            systemProps = systemProperties(),
            props = properties.mapValues { (_, value) -> value.toJsonElement() },
        )

        scope.launch {
            val shouldFlush = queueMutex.withLock {
                queue += event
                queue.size >= MAX_BATCH_SIZE
            }

            if (shouldFlush) {
                flush()
            }
        }
    }

    override suspend fun flush() {
        flushMutex.withLock {
            while (true) {
                val batch = queueMutex.withLock {
                    if (queue.isEmpty()) {
                        emptyList()
                    } else {
                        val count = minOf(queue.size, MAX_BATCH_SIZE)
                        val snapshot = queue.take(count)
                        repeat(count) {
                            queue.removeAt(0)
                        }
                        snapshot
                    }
                }

                if (batch.isEmpty()) {
                    return
                }

                val delivered = sendBatch(batch)
                if (!delivered) {
                    queueMutex.withLock {
                        queue.addAll(0, batch)
                    }
                    return
                }
            }
        }
    }

    suspend fun close() {
        flush()
        scope.cancel()
        httpClient.close()
    }

    private fun systemProperties() = AptabaseSystemProps(
        locale = platformSystemInfo.locale,
        osName = platformSystemInfo.osName,
        osVersion = platformSystemInfo.osVersion,
        deviceModel = platformSystemInfo.deviceModel,
        isDebug = config.isDebug,
        appVersion = config.appVersion,
        sdkVersion = config.sdkVersion,
    )

    private suspend fun sendBatch(batch: List<AptabaseEventPayload>): Boolean {
        return runCatching {
            val response = httpClient.post("${config.resolvedHost()}$EVENTS_PATH") {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                header(APP_KEY_HEADER, config.appKey)
                setBody(batch)
            }

            response.status.value in 200..299
        }.onFailure { throwable ->
            logger.w(throwable) { "Failed to send Aptabase events batch." }
        }.getOrDefault(false)
    }

    private fun newSessionId(): String {
        val epochSeconds = Clock.System.now().toEpochMilliseconds() / 1000
        val randomPart = Random.nextInt(0, 100_000_000).toString().padStart(8, '0')
        return "$epochSeconds$randomPart"
    }

    private companion object {
        const val APP_KEY_HEADER = "App-Key"
        const val EVENTS_PATH = "/api/v0/events"
        const val MAX_BATCH_SIZE = 25
    }
}
