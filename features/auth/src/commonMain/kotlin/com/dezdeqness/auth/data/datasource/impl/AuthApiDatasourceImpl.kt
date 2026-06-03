package com.dezdeqness.auth.data.datasource.impl

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.auth.data.datasource.AuthApiDatasource
import com.dezdeqness.auth.data.mapper.AuthMapper
import com.dezdeqness.network.datasource.BaseDataSource
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.exception.createApiException
import com.dezdeqness.network.models.response.AuthTokenResponse
import com.dezdeqness.network.services.AuthService
import de.jensklingenberg.ktorfit.Response

class AuthApiDatasourceImpl(
    private val authService: AuthService,
    private val authMapper: AuthMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), AuthApiDatasource {

    override suspend fun login(credentials: AuthCredentialsEntity) = tryWithCatchSuspend {
        authService
            .login(authMapper.mapLogin(credentials))
            .toAuthTokenResult()
    }

    override suspend fun logout() = tryWithCatchSuspend {
        val response = authService.logout()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun register(data: RegistrationDataEntity) = tryWithCatchSuspend {
        authService
            .register(authMapper.mapRegister(data))
            .toAuthTokenResult()
    }

    override suspend fun forgetPassword(email: String) = tryWithCatchSuspend {
        val response = authService.forgetPassword(authMapper.mapForget(email))
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            throw response.createApiException()
        }
    }

    override suspend fun resetPassword(data: PasswordResetDataEntity) = tryWithCatchSuspend {
        val response = authService.resetPassword(authMapper.mapReset(data))
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            throw response.createApiException()
        }
    }

    private fun Response<AuthTokenResponse>.toAuthTokenResult(): Result<AuthTokenEntity> {
        if (!isSuccessful) {
            throw createApiException()
        }
        val token = body()?.let(authMapper::mapToken)
            ?: return Result.failure(Throwable("Auth token is missing in response"))
        return Result.success(token)
    }
}
