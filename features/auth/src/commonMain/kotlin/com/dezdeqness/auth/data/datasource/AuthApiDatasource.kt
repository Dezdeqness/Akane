package com.dezdeqness.auth.data.datasource

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity

interface AuthApiDatasource {
    suspend fun login(credentials: AuthCredentialsEntity): Result<AuthTokenEntity>
    suspend fun logout(): Result<Unit>
    suspend fun register(data: RegistrationDataEntity): Result<AuthTokenEntity>
    suspend fun forgetPassword(email: String): Result<Unit>
    suspend fun resetPassword(data: PasswordResetDataEntity): Result<Unit>
}
