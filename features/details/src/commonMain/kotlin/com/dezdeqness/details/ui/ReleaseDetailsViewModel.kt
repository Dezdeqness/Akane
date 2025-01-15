package com.dezdeqness.details.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.details.domain.repository.ReleaseRepository
import com.dezdeqness.details.navigation.RELEASE_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReleaseDetailsViewModel(
    private val releaseRepository: ReleaseRepository,
    private val releaseDetailsUiMapper: ReleaseDetailsUiMapper,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private var releaseId = savedStateHandle.get<Long>(RELEASE_ID) ?: -1

    private val _releaseDetailsStateFlow: MutableStateFlow<ReleaseDetailsState> =
        MutableStateFlow(ReleaseDetailsState())
    val releaseDetailsStateFlow: StateFlow<ReleaseDetailsState> = _releaseDetailsStateFlow

    fun onInitialLoad() {
        viewModelScope.launch(Dispatchers.IO) {
            _releaseDetailsStateFlow.update {
                it.copy(status = Status.Loading)
            }
            releaseRepository
                .getReleaseById(id = releaseId)
                .onSuccess { details ->
                    _releaseDetailsStateFlow.update {
                        it.copy(
                            status = Status.Loaded,
                            details = releaseDetailsUiMapper.map(details)
                        )
                    }
                    Logger.i(
                        tag = TAG,
                        messageString = "Loaded $details",
                    )
                }
                .onFailure { throwable ->
                    _releaseDetailsStateFlow.update {
                        it.copy(status = Status.Error)
                    }
                    Logger.e(
                        tag = TAG,
                        messageString = throwable.message.orEmpty(),
                    )
                }
        }
    }

    companion object {
        private const val TAG = "ReleaseDetailsViewModel"
    }

}
