package com.dezdeqness.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.details.domain.model.ReleaseDetailsEntity
import com.dezdeqness.details.domain.repository.FranchiseRepository
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.details.navigation.RELEASE_ID
import com.dezdeqness.personal.domain.models.PersonalEntity
import com.dezdeqness.personal.domain.repository.PersonalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private val franchiseRepository: FranchiseRepository,
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
                val releaseResult = releaseRepository.getReleaseById(releaseId)
                val franchiseResult = franchiseRepository.getReleaseFranchiseById(releaseId)
                emit(LoadResult(event, releaseResult, franchiseResult))
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .scan(ReleaseDetailsState()) { previous, result ->
            result
                .releaseResult
                .onSuccess { details ->
                    val franchise = result.franchiseResult.getOrNull()
                    val details = releaseDetailsUiMapper.map(details, franchise)
                    return@scan previous.copy(
                        status = Status.Loaded,
                        details = details
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
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            if (personalRepository.containsById(id = id)) {
                personalRepository.deleteById(id = id)
            } else {
                val item = releaseDetailsStateFlow.value.details ?: return@launch
                val personalEntity = PersonalEntity(
                    id = item.id,
                    name = item.header.title,
                    poster = item.header.imageUrl,
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
        val releaseResult: Result<ReleaseDetailsEntity>,
        val franchiseResult: Result<com.dezdeqness.details.domain.model.FranchiseEntity>,
    )

    companion object {
        private const val TAG = "ReleaseDetailsViewModel"
    }

}
