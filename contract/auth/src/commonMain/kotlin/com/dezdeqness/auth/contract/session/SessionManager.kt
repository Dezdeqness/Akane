package com.dezdeqness.auth.contract.session

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val sessionState: StateFlow<SessionState>
    val isAuthorized: Boolean

    suspend fun restoreSession()
    suspend fun login(credentials: AuthCredentialsEntity): Result<Unit>
    suspend fun register(data: RegistrationDataEntity): Result<Unit>
    suspend fun logout(): Result<Unit>
}

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data object Authenticated : SessionState
}
