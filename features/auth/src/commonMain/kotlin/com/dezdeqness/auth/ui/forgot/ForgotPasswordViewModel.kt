package com.dezdeqness.auth.ui.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    fun onEmailChanged(value: String) = _state.update {
        it.copy(email = value, errorMessage = null)
    }

    fun onSubmitClicked() {
        val current = _state.value
        if (current.isLoading || current.email.isBlank()) return

        viewModelScope.launch(coroutineDispatcherProvider.io()) {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.forgetPassword(current.email.trim())
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
