package com.dezdeqness.home.domain

import com.dezdeqness.cache.CachedResult
import com.dezdeqness.calendar.contract.model.CalendarScheduleEntity
import com.dezdeqness.catalog.contract.model.ReleaseEntity
import com.dezdeqness.franchise.contract.model.FranchiseEntity
import com.dezdeqness.genre.contract.model.GenreEntity
import com.dezdeqness.promo.contract.model.PromoEntity

sealed interface HomeFeedStage {
    data class Promos(val result: Result<CachedResult<List<PromoEntity>>>) : HomeFeedStage
    data class Schedule(val result: Result<CachedResult<CalendarScheduleEntity>>) : HomeFeedStage
    data class OnGoing(val result: Result<CachedResult<List<ReleaseEntity>>>) : HomeFeedStage
    data class Franchises(val result: Result<CachedResult<List<FranchiseEntity>>>) : HomeFeedStage
    data class Released(val result: Result<CachedResult<List<ReleaseEntity>>>) : HomeFeedStage
    data class BestRated(val result: Result<CachedResult<List<ReleaseEntity>>>) : HomeFeedStage
    data class Genres(val result: Result<CachedResult<List<GenreEntity>>>) : HomeFeedStage
    data class ContinueWatching(val item: ContinueWatchingEntity?) : HomeFeedStage
}
