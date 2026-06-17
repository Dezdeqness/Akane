package com.dezdeqness.genre.ui.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.catalog.contract.model.ReleasesPageEntity
import com.dezdeqness.catalog.ui.mapper.ReleaseUiMapper
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.genre.contract.repository.GenreRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class GenreReleasesViewModel(
    private val genreId: Int,
    private val genreRepository: GenreRepository,
    private val releaseUiMapper: ReleaseUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<GenreReleasesState> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = genreRepository.getGenreReleases(
                    genreId = genreId,
                    page = event.page,
                    limit = LIMIT,
                )
                emit(LoadResult(event = event, result = result))
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .scan(GenreReleasesState()) { previous, loadResult ->
            val event = loadResult.event

            loadResult.result.onSuccess { response ->
                val mapped = response.items.map(releaseUiMapper::map)
                val updated = when (event) {
                    is LoadEvent.LoadMore -> previous.items + mapped
                    else -> mapped
                }
                val status = if (updated.isEmpty()) {
                    GenreReleasesStatus.Empty
                } else {
                    GenreReleasesStatus.Loaded
                }
                return@scan previous.copy(
                    items = updated,
                    status = status,
                    currentPage = response.currentPage,
                    hasNextPage = response.hasNextPage,
                )
            }

            loadResult.result.onFailure { throwable ->
                errorReporter.captureException(
                    throwable = throwable,
                    message = "Genre releases load failed",
                    tags = mapOf("feature" to "genre"),
                    extras = mapOf(
                        "genreId" to genreId.toString(),
                        "page" to event.page.toString(),
                    ),
                )
                val status = when (event) {
                    is LoadEvent.Initial -> GenreReleasesStatus.Error
                    else -> previous.status
                }
                return@scan previous.copy(status = status)
            }

            previous
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = GenreReleasesState(),
        )

    fun onLoadMore() {
        val current = state.value
        if (current.hasNextPage) {
            loadEvents.tryEmit(LoadEvent.LoadMore(page = current.currentPage + 1))
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
        val result: Result<ReleasesPageEntity>,
    )

    companion object {
        private const val LIMIT = 10
        private const val INITIAL_PAGE = 1
    }
}
