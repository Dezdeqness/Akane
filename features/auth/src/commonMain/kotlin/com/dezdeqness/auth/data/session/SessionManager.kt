package com.dezdeqness.auth.data.session

import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val sessionState: StateFlow<SessionState>
    val currentSession: SessionState.Authenticated? get() = sessionState.value as? SessionState.Authenticated
    val isAuthorized: Boolean

    suspend fun login(code: String): Result<Unit>
    suspend fun logout(): Result<Unit>
    suspend fun restoreSession()

    suspend fun getValidToken(): Result<String>
}

sealed interface SessionState {
    data object Loading : SessionState
    data object Unauthenticated : SessionState
    data object Authenticated : SessionState
}
