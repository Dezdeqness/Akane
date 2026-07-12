package com.dezdeqness.promo.data.repository

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.cache.staleWhileRevalidate
import com.dezdeqness.foundation.cache.JsonCacheStore
import com.dezdeqness.promo.contract.model.PromoEntity
import com.dezdeqness.promo.contract.repository.PromoRepository
import com.dezdeqness.promo.data.cache.PromoCacheMapper
import com.dezdeqness.promo.data.cache.PromoSnapshot
import com.dezdeqness.promo.data.datasource.PromoApiDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlin.time.Duration.Companion.hours

class PromoRepositoryImpl(
    private val promoApiDatasource: PromoApiDatasource,
    private val jsonCacheStore: JsonCacheStore,
    private val promoCacheMapper: PromoCacheMapper,
) : PromoRepository {

    override fun getPromotions(): Flow<Result<CachedResult<List<PromoEntity>>>> =
        staleWhileRevalidate(
            read = {
                jsonCacheStore
                    .read(CACHE_KEY, SNAPSHOT_SERIALIZER, TTL_MILLIS)
                    ?.map(promoCacheMapper::toEntity)
            },
            fetch = { promoApiDatasource.getPromotions() },
            write = { entities ->
                jsonCacheStore.write(CACHE_KEY, entities.map(promoCacheMapper::toSnapshot), SNAPSHOT_SERIALIZER)
            },
        )

    private companion object {
        const val CACHE_KEY = "promo_promotions"
        val TTL_MILLIS = 24.hours.inWholeMilliseconds
        val SNAPSHOT_SERIALIZER = ListSerializer(PromoSnapshot.serializer())
    }
}
