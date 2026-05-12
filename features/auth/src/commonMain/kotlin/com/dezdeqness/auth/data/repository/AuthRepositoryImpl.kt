package com.dezdeqness.auth.data.repository

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.auth.contract.repository.AuthRepository
import com.dezdeqness.auth.data.datasource.AuthApiDatasource

class AuthRepositoryImpl(
    private val authApiDatasource: AuthApiDatasource,
) : AuthRepository {

    override suspend fun login(credentials: AuthCredentialsEntity): Result<AuthTokenEntity> =
        authApiDatasource.login(credentials)

    override suspend fun logout(): Result<Unit> = authApiDatasource.logout()

    override suspend fun register(data: RegistrationDataEntity): Result<AuthTokenEntity> =
        authApiDatasource.register(data)

    override suspend fun forgetPassword(email: String) =
        authApiDatasource.forgetPassword(email)

    override suspend fun resetPassword(data: PasswordResetDataEntity) =
        authApiDatasource.resetPassword(data)

}
