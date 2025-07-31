package com.dezdeqness.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.details.navigation.RELEASE_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

class ReleaseDetailsViewModel(
    private val releaseRepository: ReleaseRepository,
    private val releaseDetailsUiMapper: ReleaseDetailsUiMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(RELEASE_ID) ?: -1

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val releaseDetailsStateFlow: StateFlow<ReleaseDetailsState> = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = releaseRepository.getReleaseById(releaseId)
                emit(LoadResult(event, result))
            }.flowOn(Dispatchers.IO)
        }
        .scan(ReleaseDetailsState()) { previous, result ->
            result
                .result
                .onSuccess { details ->
                    return@scan previous.copy(
                        status = Status.Loaded,
                        details = releaseDetailsUiMapper.map(details)
                    )
                }
                .onFailure { throwable ->
                    Logger.e(
                        tag = TAG,
                        messageString = throwable.message.orEmpty(),
                    )
                    return@scan previous.copy(status = Status.Error)
                }

            previous
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ReleaseDetailsState(status = Status.Loading)
        )

    private sealed class LoadEvent {
        object Initial : LoadEvent()
    }

    private data class LoadResult(
        val event: LoadEvent,
        val result: Result<ReleaseDetailsEntity>,
    )

    companion object {
        private const val TAG = "ReleaseDetailsViewModel"
    }

}
