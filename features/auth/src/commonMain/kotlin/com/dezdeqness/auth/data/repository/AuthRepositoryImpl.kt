package com.dezdeqness.auth.data.repository

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.auth.data.datasource.AuthApiDatasource
import com.dezdeqness.auth.data.datastore.SessionTokenStore

class AuthRepositoryImpl(
    private val authApiDatasource: AuthApiDatasource,
    private val sessionTokenStore: SessionTokenStore,
) : AuthRepository {

    override suspend fun login(credentials: AuthCredentialsEntity): Result<AuthTokenEntity> {
        val result = authApiDatasource.login(credentials)

        result.onSuccess { sessionTokenStore.setToken(it.token) }

        return result
    }

    override suspend fun logout() = authApiDatasource.logout().also {
        sessionTokenStore.clear()
    }

    override suspend fun register(data: RegistrationDataEntity): Result<AuthTokenEntity> {
        val result = authApiDatasource.register(data)

        result.onSuccess { sessionTokenStore.setToken(it.token) }

        return result
    }

    override suspend fun forgetPassword(email: String) =
        authApiDatasource.forgetPassword(email)

    override suspend fun resetPassword(data: PasswordResetDataEntity) =
        authApiDatasource.resetPassword(data)

}
