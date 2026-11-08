package com.dezdeqness.stories.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.analytics.core.AkaneErrorReporter
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.personal.contract.repository.PersonalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryFavouriteViewModel(
    private val personalRepository: PersonalRepository,
    sessionManager: SessionManager,
    private val dispatchers: CoroutineDispatcherProvider,
    private val errorReporter: AkaneErrorReporter,
) : ViewModel() {

    private val inProgress = MutableStateFlow<Set<Long>>(emptySet())

    val state: StateFlow<FavouriteState> = combine(
        sessionManager.sessionState,
        personalRepository.getFavoriteIdsAsFlow(),
        inProgress,
    ) { session, favouriteIds, inProgressIds ->
        if (session == SessionState.Authenticated) {
            FavouriteState.Visible(favouriteIds = favouriteIds.toSet(), inProgressIds = inProgressIds)
        } else {
            FavouriteState.Hidden
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, FavouriteState.Hidden)

    fun onFavouriteClicked(releaseId: Long) {
        if (releaseId in inProgress.value) return
        inProgress.update { it + releaseId }

        viewModelScope.launch {
            withContext(dispatchers.io()) {
                val isFavourite = personalRepository.containsById(id = releaseId)
                if (isFavourite) {
                    personalRepository.removeFromFavorites(id = releaseId)
                } else {
                    personalRepository.addToFavorites(id = releaseId)
                }
            }.onFailure { throwable ->
                errorReporter.captureException(
                    throwable = throwable,
                    message = "Toggle favourite failed",
                    tags = mapOf("feature" to "stories"),
                    extras = mapOf("release_id" to releaseId.toString()),
                )
            }
            inProgress.update { it - releaseId }
        }
    }
}

sealed interface FavouriteState {
    data object Hidden : FavouriteState
    data class Visible(val favouriteIds: Set<Long>, val inProgressIds: Set<Long>) : FavouriteState
}
