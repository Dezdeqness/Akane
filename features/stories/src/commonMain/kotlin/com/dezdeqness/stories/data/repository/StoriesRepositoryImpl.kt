package com.dezdeqness.stories.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.foundation.cache.JsonCacheStore
import com.dezdeqness.stories.contract.model.StoryCollectionEntity
import com.dezdeqness.stories.contract.repository.StoriesRepository
import com.dezdeqness.stories.data.cache.StoryCacheMapper
import com.dezdeqness.stories.data.cache.StoryCollectionSnapshot
import com.dezdeqness.stories.data.datasource.StoriesApiDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.todayIn
import kotlinx.serialization.builtins.ListSerializer
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class StoriesRepositoryImpl(
    private val storiesApiDatasource: StoriesApiDatasource,
    private val jsonCacheStore: JsonCacheStore,
    private val storyCacheMapper: StoryCacheMapper,
) : StoriesRepository {

    private val clock: Clock = Clock.System

    override fun getStories(): Flow<Result<CachedResult<List<StoryCollectionEntity>>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(
                        CACHE_KEY,
                        SNAPSHOT_SERIALIZER,
                        TTL_MILLIS
                    )
                    ?.map(storyCacheMapper::toEntity)
            },
            fetch = { storiesApiDatasource.getWeekStories(today().isoDayNumber) },
            write = { entities ->
                jsonCacheStore.write(
                    CACHE_KEY, entities.map(storyCacheMapper::toSnapshot),
                    SNAPSHOT_SERIALIZER
                )
            },
        )

    private fun today(): DayOfWeek = clock.todayIn(TimeZone.currentSystemDefault()).dayOfWeek

    private companion object {
        const val CACHE_KEY = "stories_snapshot"
        val TTL_MILLIS = 15.minutes.inWholeMilliseconds
        val SNAPSHOT_SERIALIZER = ListSerializer(StoryCollectionSnapshot.serializer())
    }
}
