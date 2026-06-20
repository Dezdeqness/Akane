package com.dezdeqness.franchise.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.catalog.ui.mapper.ReleaseUiMapper
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.franchise.contract.repository.FranchiseRepository
import com.dezdeqness.franchise.ui.mapper.FranchiseHeaderUiMapper
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

class FranchiseDetailViewModel(
    private val franchiseId: String,
    private val franchiseRepository: FranchiseRepository,
    private val releaseUiMapper: ReleaseUiMapper,
    private val franchiseHeaderUiMapper: FranchiseHeaderUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val reloadTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<FranchiseDetailState> = reloadTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                val cachedHeader = franchiseRepository.getCachedFranchise(franchiseId)
                    .getOrNull()
                    ?.let(franchiseHeaderUiMapper::map)

                emit(
                    FranchiseDetailState(
                        header = cachedHeader,
                        status = FranchiseDetailStatus.Loading,
                    )
                )

                franchiseRepository.getFranchiseById(franchiseId)
                    .onSuccess { details ->
                        val items = details.releases.map { releaseUiMapper.map(it.release) }
                        emit(
                            FranchiseDetailState(
                                header = cachedHeader ?: franchiseHeaderUiMapper.map(details.franchise),
                                items = items,
                                status = if (items.isEmpty()) {
                                    FranchiseDetailStatus.Empty
                                } else {
                                    FranchiseDetailStatus.Loaded
                                },
                            )
                        )
                    }
                    .onFailure { throwable ->
                        captureError(throwable)
                        emit(
                            FranchiseDetailState(
                                header = cachedHeader,
                                status = FranchiseDetailStatus.Error,
                            )
                        )
                    }
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .catch { throwable ->
            captureError(throwable)
            emit(FranchiseDetailState(status = FranchiseDetailStatus.Error))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = FranchiseDetailState(status = FranchiseDetailStatus.Loading),
        )

    fun onRetryClicked() {
        reloadTrigger.tryEmit(Unit)
    }

    private fun captureError(throwable: Throwable) {
        errorReporter.captureException(
            throwable = throwable,
            message = "Franchise detail load failed",
            tags = mapOf("feature" to "franchise"),
            extras = mapOf("franchiseId" to franchiseId),
        )
    }
}
