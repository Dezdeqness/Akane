package com.dezdeqness.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.calendar.domain.model.CalendarScheduleEntity
import com.dezdeqness.calendar.domain.repository.CalendarRepository
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.domain.model.ReleaseEntity
import com.dezdeqness.feed.domain.repository.FeedRepository
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val feedRepository: FeedRepository,
    private val calendarRepository: CalendarRepository,
    private val homeUiMapper: HomeUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeState : StateFlow<HomeState> =
        reloadTrigger
            .onStart { emit(Unit) }
            .flatMapLatest {
                flow {
                    val (ongoing, released, best, calendar) = coroutineScope {
                        val ongoingD = async { feedRepository.getFeedOngoing() }
                        val releasedD = async { feedRepository.getFeedReleased() }
                        val bestD = async { feedRepository.getFeedBestRating() }
                        val calendarD = async { calendarRepository.getScheduleNow() }

                        HomeParallelResult(
                            ongoing = ongoingD.await(),
                            released = releasedD.await(),
                            best = bestD.await(),
                            calendar = calendarD.await(),
                        )
                    }

                    if (listOf(ongoing, released, best, calendar).any { it.isFailure }) {
                        emit(HomeState(status = StateStatus.Error))
                        return@flow
                    }

                    emit(
                        HomeState(
                            onGoing = ongoing.getOrThrow().map(homeUiMapper::toUiModel),
                            released = released.getOrThrow().map(homeUiMapper::toUiModel),
                            bestRated = best.getOrThrow().map(homeUiMapper::toUiModel),
                            freshUpdates = calendar.getOrThrow().today.map(homeUiMapper::toUiModelSchedule),
                            status = StateStatus.Loaded
                        )
                    )
                }.flowOn(coroutineDispatcherProvider.io())
            }
            .catch { emit(HomeState(status = StateStatus.Error)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = HomeState(status = StateStatus.Loading)
            )

    fun onRetryClicked() {
        reloadTrigger.tryEmit(Unit)
    }
}

private data class HomeParallelResult(
    val ongoing: Result<List<ReleaseEntity>>,
    val released: Result<List<ReleaseEntity>>,
    val best: Result<List<ReleaseEntity>>,
    val calendar: Result<CalendarScheduleEntity>,
)