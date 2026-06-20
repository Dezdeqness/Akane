package com.dezdeqness.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.home.domain.HomeFeedStage
import com.dezdeqness.home.domain.LoadHomeFeedUseCase
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val loadHomeFeedUseCase: LoadHomeFeedUseCase,
    private val homeUiMapper: HomeUiMapper,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeState: StateFlow<HomeState> = reloadTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            loadHomeFeedUseCase()
                .scan(HomeState(status = StateStatus.Loading)) { state, stage ->
                    reduce(state, stage)
                }
        }
        .catch { throwable ->
            captureError(throwable)
            emit(HomeState(status = StateStatus.Error))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = HomeState(status = StateStatus.Loading),
        )

    fun retry() {
        reloadTrigger.tryEmit(Unit)
    }

    private fun reduce(state: HomeState, stage: HomeFeedStage): HomeState =
        when (stage) {
            is HomeFeedStage.FirstPart -> stage.result.fold(
                onSuccess = { data ->
                    state.copy(
                        promos = data.promos.map(homeUiMapper::toPromoPanel),
                        freshUpdates = data.freshUpdates.map(homeUiMapper::toUiModelSchedule),
                        onGoing = data.onGoing.map(homeUiMapper::toUiModel),
                        status = StateStatus.LoadingMore,
                    )
                },
                onFailure = { throwable ->
                    captureError(throwable)
                    state.copy(status = StateStatus.Error)
                },
            )

            is HomeFeedStage.SecondPart -> stage.result.fold(
                onSuccess = { data ->
                    state.copy(
                        franchises = data.franchises.map(homeUiMapper::toFranchisePanel),
                        released = data.released.map(homeUiMapper::toUiModel),
                        bestRated = data.bestRated.map(homeUiMapper::toUiModel),
                        genres = data.genres.map(homeUiMapper::toGenrePanel),
                        status = StateStatus.Loaded,
                    )
                },
                onFailure = { throwable ->
                    captureError(throwable)
                    state.copy(status = StateStatus.SecondPartError)
                },
            )
        }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "Home load failed",
            tags = mapOf("feature" to "home"),
        )
    }
}
