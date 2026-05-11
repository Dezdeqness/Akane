package com.dezdeqness.auth.data.datasource.impl

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.auth.data.datasource.AuthApiDatasource
import com.dezdeqness.auth.data.mapper.AuthMapper
import com.dezdeqness.network.models.response.AuthTokenResponse
import com.dezdeqness.network.services.AuthService
import de.jensklingenberg.ktorfit.Response

class AuthApiDatasourceImpl(
    private val authService: AuthService,
    private val authMapper: AuthMapper,
) : AuthApiDatasource {

    override suspend fun login(credentials: AuthCredentialsEntity) = tryWithCatch {
        authService
            .login(authMapper.mapLogin(credentials))
            .toAuthTokenResult()
    }

    override suspend fun logout() = tryWithCatch {
        val response = authService.logout()
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(httpError(response))
        }
    }

    override suspend fun register(data: RegistrationDataEntity) = tryWithCatch {
        authService
            .register(authMapper.mapRegister(data))
            .toAuthTokenResult()
    }

    override suspend fun forgetPassword(email: String) = tryWithCatch {
        val response = authService.forgetPassword(authMapper.mapForget(email))
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(httpError(response))
        }
    }

    override suspend fun resetPassword(data: PasswordResetDataEntity) = tryWithCatch {
        val response = authService.resetPassword(authMapper.mapReset(data))
        if (response.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(httpError(response))
        }
    }

    private fun Response<AuthTokenResponse>.toAuthTokenResult(): Result<AuthTokenEntity> {
        if (!isSuccessful) {
            return Result.failure(httpError(this))
        }
        val token = body()?.let(authMapper::mapToken)
            ?: return Result.failure(Throwable("Auth token is missing in response"))
        return Result.success(token)
    }

    private fun httpError(response: Response<*>): Throwable =
        Throwable("Code: ${response.code}\nError: ${response.errorBody()}")

    private suspend fun <T> tryWithCatch(block: suspend () -> Result<T>) = try {
        block()
    } catch (throwable: Throwable) {
        Result.failure(throwable)
    }
}
