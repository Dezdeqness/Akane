package com.dezdeqness.shared.session

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.auth.contract.session.SessionManager
import com.dezdeqness.auth.contract.session.SessionState
import com.dezdeqness.auth.data.datastore.SessionTokenStore
import com.dezdeqness.core.dispatcher.CoroutineDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class SessionManagerImpl(
    private val sessionTokenStore: SessionTokenStore,
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : SessionManager {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    override val isAuthorized: Boolean
        get() = _sessionState.value is SessionState.Authenticated

    override suspend fun restoreSession() {
        withContext(dispatcherProvider.io()) {
            val token = sessionTokenStore.getToken()
            _sessionState.value = if (token.isEmpty()) {
                SessionState.Unauthenticated
            } else {
                SessionState.Authenticated
            }
        }
    }

    override suspend fun login(credentials: AuthCredentialsEntity): Result<Unit> =
        withContext(dispatcherProvider.io()) {
            authRepository.login(credentials).mapCatching { token ->
                sessionTokenStore.setToken(token.token)
                _sessionState.value = SessionState.Authenticated
            }
        }

    override suspend fun register(data: RegistrationDataEntity): Result<Unit> =
        withContext(dispatcherProvider.io()) {
            authRepository.register(data).mapCatching { token ->
                sessionTokenStore.setToken(token.token)
                _sessionState.value = SessionState.Authenticated
            }
        }

    override suspend fun logout(): Result<Unit> = withContext(dispatcherProvider.io()) {
        authRepository.logout().also {
            sessionTokenStore.clear()
            _sessionState.value = SessionState.Unauthenticated
        }
    }
}
