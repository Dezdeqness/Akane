package com.dezdeqness.home.domain

import com.dezdeqness.calendar.contract.repository.CalendarRepository
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.contract.repository.FeedRepository
import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.promo.contract.repository.PromoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class LoadHomeFeedUseCase(
    private val promoRepository: PromoRepository,
    private val calendarRepository: CalendarRepository,
    private val feedRepository: FeedRepository,
    private val franchiseRepository: FranchiseRepository,
    private val genreRepository: GenreRepository,
    private val loadContinueWatchingUseCase: LoadContinueWatchingUseCase,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    operator fun invoke(): Flow<HomeFeedStage> = merge(
        promoRepository.getPromotions().map(HomeFeedStage::Promos),
        calendarRepository.getScheduleNow().map(HomeFeedStage::Schedule),
        feedRepository.getFeedOngoing().map(HomeFeedStage::OnGoing),
        franchiseRepository.getRandomFranchises().map(HomeFeedStage::Franchises),
        feedRepository.getFeedReleased().map(HomeFeedStage::Released),
        feedRepository.getFeedBestRating().map(HomeFeedStage::BestRated),
        genreRepository.getRandomGenres().map(HomeFeedStage::Genres),
        loadContinueWatchingUseCase().map(HomeFeedStage::ContinueWatching),
    ).flowOn(coroutineDispatcherProvider.io())
}
