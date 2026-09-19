package com.dezdeqness.auth.ui.reset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val authRepository: AuthRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ResetPasswordState())
    val state: StateFlow<ResetPasswordState> = _state.asStateFlow()

    fun onTokenChanged(value: String) = _state.update {
        it.copy(token = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) = _state.update {
        it.copy(password = value, errorMessage = null)
    }

    fun onPasswordConfirmationChanged(value: String) = _state.update {
        it.copy(passwordConfirmation = value, errorMessage = null)
    }

    fun onPasswordVisibilityToggled() = _state.update {
        it.copy(isPasswordVisible = !it.isPasswordVisible)
    }

    fun onConfirmationVisibilityToggled() = _state.update {
        it.copy(isConfirmationVisible = !it.isConfirmationVisible)
    }

    fun onSubmitClicked() {
        val current = _state.value
        if (current.isLoading) return
        if (current.token.isBlank() || current.password.isBlank() ||
            current.passwordConfirmation.isBlank()
        ) {
            _state.update { it.copy(errorMessage = "Заполните все поля") }
            return
        }
        if (current.password != current.passwordConfirmation) {
            _state.update { it.copy(errorMessage = "Пароли не совпадают") }
            return
        }

        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.resetPassword(
                PasswordResetDataEntity(
                    token = current.token.trim(),
                    password = current.password,
                    passwordConfirmation = current.passwordConfirmation,
                )
            )
            _state.update {
                it.copy(
                    isLoading = false,
                    isSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message,
                )
            }
        }
    }
}
