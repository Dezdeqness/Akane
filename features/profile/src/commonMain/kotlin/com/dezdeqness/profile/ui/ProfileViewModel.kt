package com.dezdeqness.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import com.dezdeqness.profile.contract.repository.ProfileRepository
import com.dezdeqness.profile.ui.mapper.ProfileUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileUiMapper: ProfileUiMapper,
    private val sessionManager: SessionManager,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        sessionManager.sessionState
            .onEach { handleSession(it) }
            .launchIn(viewModelScope)
    }
    fun onRetryClicked() {
        loadProfile()
    }

    fun onLogoutClicked() {
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            sessionManager.logout()
        }
    }

    private fun handleSession(state: SessionState) {
        when (state) {
            SessionState.Authenticated -> loadProfile()
            SessionState.Unauthenticated -> _state.update {
                ProfileState(profile = null, isLoading = false, errorMessage = null)
            }
            SessionState.Loading -> Unit
        }
    }

    private fun loadProfile() {
        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            profileRepository.getProfile()
                .onSuccess { profile ->
                    _state.update {
                        it.copy(
                            profile = profileUiMapper.map(profile),
                            isLoading = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { throwable ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = throwable.message)
                    }
                }
        }
    }
}
