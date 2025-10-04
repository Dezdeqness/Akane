package com.dezdeqness.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.feed.domain.repository.FeedRepository
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

class HomeViewModel(
    private val feedRepository: FeedRepository,
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
                    val (onGoing, released, best) = coroutineScope {
                        awaitAll(
                            async { feedRepository.getFeedOngoing() },
                            async { feedRepository.getFeedReleased() },
                            async { feedRepository.getFeedBestRating() },
                        )
                    }.also {
                        it.forEach {
                            if (it.isFailure) {
                                emit(HomeState(status = StateStatus.Error))
                                return@flow
                            }
                        }
                    }
                    emit(
                        HomeState(
                            onGoing = onGoing.getOrThrow().map(homeUiMapper::toUiModel),
                            released = released.getOrThrow().map(homeUiMapper::toUiModel),
                            bestRated = best.getOrThrow().map(homeUiMapper::toUiModel),
                            status = StateStatus.Loaded,
                        )
                    )
                }.flowOn(coroutineDispatcherProvider.io())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = HomeState(status = StateStatus.Loading)
            )

    fun onRetryClicked() {
        reloadTrigger.tryEmit(Unit)
    }
}
