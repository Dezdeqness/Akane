package com.dezdeqness.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.details.navigation.RELEASE_ID
import com.dezdeqness.personal.domain.models.PersonalEntity
import com.dezdeqness.personal.domain.repository.PersonalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReleaseDetailsViewModel(
    private val releaseRepository: ReleaseRepository,
    private val personalRepository: PersonalRepository,
    private val releaseDetailsUiMapper: ReleaseDetailsUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(RELEASE_ID) ?: -1

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val releaseDetailsStateFlow: StateFlow<ReleaseDetailsState> = combine(
        loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val result = releaseRepository.getReleaseById(releaseId)
                emit(LoadResult(event, result))
            }.flowOn(coroutineDispatcherProvider.io())
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
        },
        personalRepository.getPersonalAsFlow()
    ) { detailsState, personalList ->
        val isFavourite = personalList.any { it.id == releaseId }
        detailsState.copy(isFavourite = isFavourite)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ReleaseDetailsState(status = Status.Loading)
        )

    fun onFavouriteClicked(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if (personalRepository.containsById(id = id)) {
                personalRepository.deleteById(id = id)
            } else {
                val item = releaseDetailsStateFlow.value.details ?: return@launch
                val personalEntity = PersonalEntity(
                    id = item.id,
                    name = item.title,
                    poster = item.imageUrl,
                )
                personalRepository.add(personalEntity)
            }
        }
    }

    private sealed class LoadEvent {
        data object Initial : LoadEvent()
    }

    private data class LoadResult(
        val event: LoadEvent,
        val result: Result<ReleaseDetailsEntity>,
    )

    companion object {
        private const val TAG = "ReleaseDetailsViewModel"
    }

}
