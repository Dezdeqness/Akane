package com.dezdeqness.personal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.network.error.ErrorEntity
import com.dezdeqness.personal.contract.model.PersonalPageEntity
import com.dezdeqness.personal.contract.repository.PersonalRepository
import com.dezdeqness.personal.ui.mapper.PersonalUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class PersonalViewModel(
    private val personalRepository: PersonalRepository,
    private val personalUiMapper: PersonalUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    init {
        personalRepository.getFavoriteIdsAsFlow()
            .drop(1)
            .onEach { loadEvents.tryEmit(LoadEvent.Refresh) }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val personalStateFlow: StateFlow<PersonalState> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = personalRepository.getFavoriteReleases(page = event.page)
                emit(LoadResult(event, result))
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .scan(PersonalState()) { previous, loadResult ->
            val event = loadResult.event

            loadResult.result.onSuccess { page ->
                val mapped = page.items.map(personalUiMapper::toUiModel)
                val updatedList = when (event) {
                    is LoadEvent.LoadMore -> previous.list + mapped
                    else -> mapped
                }
                val status = if (updatedList.isEmpty()) Status.Empty else Status.Loaded
                return@scan previous.copy(
                    list = updatedList,
                    status = status,
                    currentPage = page.currentPage,
                    hasNextPage = page.hasNextPage,
                )
            }

            loadResult.result.onFailure { error ->
                errorReporter.captureException(
                    throwable = error,
                    message = "Favorites load failed",
                    tags = mapOf("feature" to "personal"),
                    extras = mapOf("page" to event.page.toString()),
                )
                Logger.e(tag = TAG, messageString = error.message.orEmpty())

                val status = when {
                    error is ErrorEntity.UnauthorizedErrorEntity -> Status.Unauthorized
                    event is LoadEvent.LoadMore -> previous.status
                    else -> Status.Error
                }
                return@scan previous.copy(status = status)
            }

            previous
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = PersonalState(),
        )

    fun onLoadMore() {
        val state = personalStateFlow.value
        if (state.hasNextPage) {
            loadEvents.tryEmit(LoadEvent.LoadMore(page = state.currentPage + 1))
        }
    }

    fun onRetryClicked() {
        loadEvents.tryEmit(LoadEvent.Refresh)
    }

    private sealed class LoadEvent(open val page: Int) {
        data object Initial : LoadEvent(INITIAL_PAGE)
        data object Refresh : LoadEvent(INITIAL_PAGE)
        data class LoadMore(override val page: Int) : LoadEvent(page)
    }

    private data class LoadResult(
        val event: LoadEvent,
        val result: Result<PersonalPageEntity>,
    )

    private companion object {
        private const val TAG = "PersonalViewModel"
        private const val INITIAL_PAGE = 1
    }
}
