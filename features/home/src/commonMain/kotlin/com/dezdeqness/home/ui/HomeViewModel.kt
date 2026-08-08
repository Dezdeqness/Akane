package com.dezdeqness.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.home.domain.HomeFeedStage
import com.dezdeqness.home.domain.LoadHomeFeedUseCase
import com.dezdeqness.home.ui.mapper.HomeUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val loadHomeFeedUseCase: LoadHomeFeedUseCase,
    private val homeUiMapper: HomeUiMapper,
    private val errorReporter: AkaneErrorReporter,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
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
        // Entity -> UI mapping in reduce() must not run on the main thread.
        .flowOn(coroutineDispatcherProvider.io())
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
            is HomeFeedStage.Promos -> stage.result.fold(
                onSuccess = { loaded(state.copy(promos = it.value.map(homeUiMapper::toPromoPanel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.Schedule -> stage.result.fold(
                onSuccess = { loaded(state.copy(freshUpdates = it.value.today.map(homeUiMapper::toUiModelSchedule))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.OnGoing -> stage.result.fold(
                onSuccess = { loaded(state.copy(onGoing = it.value.map(homeUiMapper::toUiModel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.Franchises -> stage.result.fold(
                onSuccess = { loaded(state.copy(franchises = it.value.map(homeUiMapper::toFranchisePanel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.Released -> stage.result.fold(
                onSuccess = { loaded(state.copy(released = it.value.map(homeUiMapper::toUiModel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.BestRated -> stage.result.fold(
                onSuccess = { loaded(state.copy(bestRated = it.value.map(homeUiMapper::toUiModel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.Genres -> stage.result.fold(
                onSuccess = { loaded(state.copy(genres = it.value.map(homeUiMapper::toGenrePanel))) },
                onFailure = { onSectionError(state, it) },
            )

            is HomeFeedStage.ContinueWatching ->
                loaded(state.copy(continueWatching = stage.item?.let(homeUiMapper::toContinueWatching)))
        }

    private fun loaded(state: HomeState) = state.copy(status = StateStatus.Loaded)

    private fun onSectionError(state: HomeState, throwable: Throwable): HomeState {
        captureError(throwable)
        return if (state.status == StateStatus.Loading || state.status == StateStatus.Initial) {
            state.copy(status = StateStatus.Error)
        } else {
            state
        }
    }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "Home load failed",
            tags = mapOf("feature" to "home"),
        )
    }
}
