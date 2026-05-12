package com.dezdeqness.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionManager: SessionManager,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onLoginChanged(value: String) = _state.update {
        it.copy(login = value)
    }

    fun onPasswordChanged(value: String) = _state.update {
        it.copy(password = value)
    }

    fun onPasswordVisibilityToggled() = _state.update {
        it.copy(isPasswordVisible = !it.isPasswordVisible)
    }

    fun onSignInClicked() {
        val current = _state.value
        if (current.login.isBlank() || current.password.isBlank()) return

        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = sessionManager.login(
                AuthCredentialsEntity(login = current.login, password = current.password)
            )
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
