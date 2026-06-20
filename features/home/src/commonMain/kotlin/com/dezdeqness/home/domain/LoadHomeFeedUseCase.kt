package com.dezdeqness.home.domain

import com.dezdeqness.calendar.contract.repository.CalendarRepository
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.contract.repository.FeedRepository
import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.promo.contract.repository.PromoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoadHomeFeedUseCase(
    private val promoRepository: PromoRepository,
    private val calendarRepository: CalendarRepository,
    private val feedRepository: FeedRepository,
    private val franchiseRepository: FranchiseRepository,
    private val genreRepository: GenreRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) {

    operator fun invoke(): Flow<HomeFeedStage> = flow {
        val firstPart = loadFirstPart()
        emit(HomeFeedStage.FirstPart(firstPart))

        if (firstPart.isFailure) return@flow

        emit(HomeFeedStage.SecondPart(loadSecondPart()))
    }.flowOn(coroutineDispatcherProvider.io())

    private suspend fun loadFirstPart(): Result<HomeFirstPartData> = coroutineScope {
        val promos = async { promoRepository.getPromotions() }
        val freshUpdates = async { calendarRepository.getScheduleNow() }
        val onGoing = async { feedRepository.getFeedOngoing() }

        val promosResult = promos.await()
        val freshUpdatesResult = freshUpdates.await()
        val onGoingResult = onGoing.await()

        runCatching {
            HomeFirstPartData(
                promos = promosResult.getOrThrow(),
                freshUpdates = freshUpdatesResult.getOrThrow().today,
                onGoing = onGoingResult.getOrThrow(),
            )
        }
    }

    private suspend fun loadSecondPart(): Result<HomeSecondPartData> = coroutineScope {
        val franchises = async { franchiseRepository.getRandomFranchises() }
        val released = async { feedRepository.getFeedReleased() }
        val bestRated = async { feedRepository.getFeedBestRating() }
        val genres = async { genreRepository.getRandomGenres() }

        val franchisesResult = franchises.await()
        val releasedResult = released.await()
        val bestRatedResult = bestRated.await()
        val genresResult = genres.await()

        runCatching {
            HomeSecondPartData(
                franchises = franchisesResult.getOrThrow(),
                released = releasedResult.getOrThrow(),
                bestRated = bestRatedResult.getOrThrow(),
                genres = genresResult.getOrThrow(),
            )
        }
    }
}
