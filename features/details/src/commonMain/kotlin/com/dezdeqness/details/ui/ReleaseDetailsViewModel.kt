package com.dezdeqness.details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.dezdeqness.analytics.core.AkaneAnalytics
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.release.contract.model.FranchiseEntity
import com.dezdeqness.release.contract.model.ReleaseDetailsEntity
import com.dezdeqness.release.contract.repository.FranchiseRepository
import com.dezdeqness.release.contract.repository.ReleaseRepository
import com.dezdeqness.details.ui.model.DetailsTab
import com.dezdeqness.details.ui.model.EpisodesUiModel
import com.dezdeqness.details.ui.model.FavouriteButtonState
import com.dezdeqness.downloads.contract.repository.DownloadEpisodeRepository
import com.dezdeqness.downloads.domain.usecase.CancelAllDownloadsUseCase
import com.dezdeqness.downloads.domain.usecase.CancelDownloadUseCase
import com.dezdeqness.downloads.domain.usecase.EnqueueDownloadUseCase
import com.dezdeqness.downloads.contract.model.DownloadTiming
import com.dezdeqness.personal.contract.repository.PersonalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReleaseDetailsViewModel(
    private val releaseId: Long,
    private val releaseRepository: ReleaseRepository,
    private val franchiseRepository: FranchiseRepository,
    private val personalRepository: PersonalRepository,
    private val sessionManager: SessionManager,
    downloadEpisodeRepository: DownloadEpisodeRepository,
    private val enqueueDownloadUseCase: EnqueueDownloadUseCase,
    private val cancelDownloadUseCase: CancelDownloadUseCase,
    private val cancelAllDownloadsUseCase: CancelAllDownloadsUseCase,
    private val releaseDetailsUiMapper: ReleaseDetailsUiMapper,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val analytics: AkaneAnalytics,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val loadEvents = MutableSharedFlow<LoadEvent>(extraBufferCapacity = 1)

    private val _dialogState = MutableStateFlow<DownloadDialogState>(DownloadDialogState.None)

    private val _favouriteState = MutableStateFlow(FavouriteUiState())

    private val favouriteStateFlow = combine(
        _favouriteState,
        sessionManager.sessionState,
    ) { uiState, sessionState ->
        FavouriteState(uiState = uiState, sessionState = sessionState)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val detailsFlow = loadEvents
        .onStart { emit(LoadEvent.Initial) }
        .flatMapLatest { event ->
            flow {
                val releaseResult = releaseRepository.getReleaseById(releaseId)
                val franchiseResult = franchiseRepository.getReleaseFranchiseById(releaseId)
                emit(LoadResult(event, releaseResult, franchiseResult))
            }.flowOn(coroutineDispatcherProvider.io())
        }
        .scan(LoadResultCache()) { previous, result ->
            result
                .franchiseResult
                .onFailure { throwable ->
                    errorReporter.captureException(
                        throwable = throwable,
                        message = "Release details load failed",
                        tags = mapOf("feature" to "details"),
                        extras = mapOf("release_id" to releaseId.toString()),
                    )
                }

            result
                .releaseResult
                .onSuccess { details ->
                    val franchise = result.franchiseResult.getOrNull()
                    return@scan LoadResultCache(
                        status = Status.Loaded,
                        release = details,
                        franchise = franchise,
                    )
                }
                .onFailure { throwable ->
                    errorReporter.captureException(
                        throwable = throwable,
                        message = "Release details load failed",
                        tags = mapOf("feature" to "details"),
                        extras = mapOf("release_id" to releaseId.toString()),
                    )
                    Logger.e(
                        tag = TAG,
                        messageString = throwable.message.orEmpty(),
                    )
                    return@scan previous.copy(status = Status.Error)
                }
            previous
        }

    val releaseDetailsStateFlow: StateFlow<ReleaseDetailsState> = combine(
        detailsFlow,
        personalRepository.getFavoriteIdsAsFlow(),
        downloadEpisodeRepository.getAllDownloadsAsFlow(),
        _dialogState,
        favouriteStateFlow,
    ) { cache, favoriteIds, downloads, dialogState, favouriteState ->
        val favouriteButtonState = when {
            favouriteState.sessionState != SessionState.Authenticated -> FavouriteButtonState.Hidden
            favouriteState.uiState.isLoading -> FavouriteButtonState.Loading
            else -> FavouriteButtonState.Loaded(isFavourite = favoriteIds.contains(releaseId))
        }
        if (cache.status != Status.Loaded || cache.release == null) {
            ReleaseDetailsState(
                status = cache.status,
                favouriteButtonState = favouriteButtonState,
                dialogState = dialogState,
            )
        } else {
            val mapped = releaseDetailsUiMapper.map(cache.release, cache.franchise, downloads)
            ReleaseDetailsState(
                status = Status.Loaded,
                details = mapped.details,
                favouriteButtonState = favouriteButtonState,
                dialogState = dialogState,
                commonQualities = mapped.commonQualities,
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ReleaseDetailsState(status = Status.Loading)
        )

    fun onFavouriteClicked(id: Long) {
        if (_favouriteState.value.isLoading) return
        val item = releaseDetailsStateFlow.value.details ?: return
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            _favouriteState.update { it.copy(isLoading = true) }

            val isFavourite = personalRepository.containsById(id = id)
            val result = if (isFavourite) {
                personalRepository.removeFromFavorites(id = id)
            } else {
                personalRepository.addToFavorites(id = id)
            }

            result
                .onSuccess {
                    if (isFavourite) {
                        analytics.trackUnfavouriteAnime(animeId = id, title = item.header.title)
                    } else {
                        analytics.trackFavouriteAnime(animeId = id, title = item.header.title)
                    }
                }
                .onFailure { throwable ->
                    errorReporter.captureException(
                        throwable = throwable,
                        message = "Toggle favourite failed",
                        tags = mapOf("feature" to "details"),
                        extras = mapOf("release_id" to id.toString()),
                    )
                }

            _favouriteState.update { it.copy(isLoading = false) }
        }
    }

    fun onRetryClicked() {
        loadEvents.tryEmit(LoadEvent.Initial)
    }

    fun onShowDownloadDialog(episode: EpisodesUiModel) {
        _dialogState.value = DownloadDialogState.SingleEpisode(episode)
    }

    fun onShowBatchDownloadDialog() {
        if (releaseDetailsStateFlow.value.commonQualities.isEmpty()) return
        _dialogState.value = DownloadDialogState.BatchQuality
    }

    fun onDismissDialog() {
        _dialogState.value = DownloadDialogState.None
    }

    fun onDownloadEpisode(episodeId: String, quality: String, hlsUrl: String) {
        _dialogState.value = DownloadDialogState.None
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            val details = releaseDetailsStateFlow.value.details ?: return@launch
            val episode = findEpisode(episodeId) ?: return@launch

            enqueueDownloadUseCase(
                EnqueueDownloadUseCase.Params(
                    releaseId = details.id,
                    releaseTitle = details.header.title,
                    episodeId = episode.id,
                    episodeName = episode.name,
                    episodeOrdinal = episode.ordinal,
                    quality = quality,
                    hlsUrl = hlsUrl,
                    previewUrl = episode.previewUrl,
                    opening = episode.opening?.let { DownloadTiming(it.start, it.end) },
                    ending = episode.ending?.let { DownloadTiming(it.start, it.end) },
                )
            )
        }
    }

    fun onDownloadAllEpisodes(quality: String) {
        _dialogState.value = DownloadDialogState.None
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            val details = releaseDetailsStateFlow.value.details ?: return@launch
            val episodes = getEpisodes().sortedBy { it.ordinal }

            episodes.forEach { episode ->
                val url = episode.episodeUrls[quality] ?: return@forEach
                enqueueDownloadUseCase(
                    EnqueueDownloadUseCase.Params(
                        releaseId = details.id,
                        releaseTitle = details.header.title,
                        episodeId = episode.id,
                        episodeName = episode.name,
                        episodeOrdinal = episode.ordinal,
                        quality = quality,
                        hlsUrl = url,
                        previewUrl = episode.previewUrl,
                        opening = episode.opening?.let { DownloadTiming(it.start, it.end) },
                        ending = episode.ending?.let { DownloadTiming(it.start, it.end) },
                    )
                )
            }
        }
    }

    fun onCancelDownload(episodeId: String) {
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            cancelDownloadUseCase(episodeId)
        }
    }

    fun onCancelAllDownloads() {
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            cancelAllDownloadsUseCase(releaseId)
        }
    }

    private fun getEpisodes(): List<EpisodesUiModel> {
        val details = releaseDetailsStateFlow.value.details ?: return emptyList()
        return details.tabs.filterIsInstance<DetailsTab.EpisodesTab>()
            .firstOrNull()?.episodes.orEmpty()
    }

    private fun findEpisode(episodeId: String): EpisodesUiModel? {
        return getEpisodes().find { it.id == episodeId }
    }

    private sealed class LoadEvent {
        data object Initial : LoadEvent()
    }

    private data class LoadResult(
        val event: LoadEvent,
        val releaseResult: Result<ReleaseDetailsEntity>,
        val franchiseResult: Result<FranchiseEntity>,
    )

    private data class LoadResultCache(
        val status: Status = Status.Loading,
        val release: ReleaseDetailsEntity? = null,
        val franchise: FranchiseEntity? = null,
    )

    private data class FavouriteUiState(
        val isLoading: Boolean = false,
    )

    private data class FavouriteState(
        val uiState: FavouriteUiState,
        val sessionState: SessionState,
    )

    companion object {
        private const val TAG = "ReleaseDetailsViewModel"
    }

}
