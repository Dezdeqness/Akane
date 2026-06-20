package com.dezdeqness.franchise.ui.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.franchise.ui.mapper.FranchiseUiMapper
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

class AllFranchisesViewModel(
    private val franchiseRepository: FranchiseRepository,
    private val franchiseUiMapper: FranchiseUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<AllFranchisesState> = reloadTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(AllFranchisesState(status = AllFranchisesStatus.Loading))

                franchiseRepository.getFranchises()
                    .onSuccess { franchises ->
                        emit(
                            AllFranchisesState(
                                franchises = franchises.map(franchiseUiMapper::map),
                                status = AllFranchisesStatus.Loaded,
                            )
                        )
                    }
                    .onFailure { throwable ->
                        captureError(throwable)
                        emit(AllFranchisesState(status = AllFranchisesStatus.Error))
                    }
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .catch { throwable ->
            captureError(throwable)
            emit(AllFranchisesState(status = AllFranchisesStatus.Error))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = AllFranchisesState(status = AllFranchisesStatus.Loading),
        )

    fun onRetryClicked() {
        reloadTrigger.tryEmit(Unit)
    }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "All franchises load failed",
            tags = mapOf("feature" to "franchise"),
        )
    }
}
