package com.dezdeqness.genre.ui.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.genre.contract.repository.GenreRepository
import com.dezdeqness.genre.ui.mapper.GenreUiMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AllGenresViewModel(
    private val genreRepository: GenreRepository,
    private val genreUiMapper: GenreUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<AllGenresState> = reloadTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(AllGenresState(status = AllGenresStatus.Loading))

                genreRepository.getGenres()
                    .onSuccess { genres ->
                        emit(
                            AllGenresState(
                                genres = genres.map(genreUiMapper::map),
                                status = AllGenresStatus.Loaded,
                            )
                        )
                    }
                    .onFailure { throwable ->
                        captureError(throwable)
                        emit(AllGenresState(status = AllGenresStatus.Error))
                    }
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .catch { throwable ->
            captureError(throwable)
            emit(AllGenresState(status = AllGenresStatus.Error))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AllGenresState(status = AllGenresStatus.Loading),
        )

    fun onRetryClicked() {
        reloadTrigger.tryEmit(Unit)
    }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "All genres load failed",
            tags = mapOf("feature" to "genre"),
        )
    }
}
