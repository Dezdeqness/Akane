package com.dezdeqness.auth.data.mapper

import com.dezdeqness.auth.contract.model.AuthCredentialsEntity
import com.dezdeqness.auth.contract.model.AuthTokenEntity
import com.dezdeqness.auth.contract.model.PasswordResetDataEntity
import com.dezdeqness.auth.contract.model.RegistrationDataEntity
import com.dezdeqness.network.models.request.ForgetPasswordRequest
import com.dezdeqness.network.models.request.LoginRequest
import com.dezdeqness.network.models.request.RegistrationRequest
import com.dezdeqness.network.models.request.ResetPasswordRequest
import com.dezdeqness.network.models.response.AuthTokenResponse

class AuthMapper {

    fun mapLogin(credentials: AuthCredentialsEntity) = LoginRequest(
        login = credentials.login,
        password = credentials.password,
    )

    fun mapRegister(data: RegistrationDataEntity) = RegistrationRequest(
        login = data.login,
        email = data.email,
        nickname = data.nickname,
        password = data.password,
        passwordConfirmation = data.passwordConfirmation,
        recaptchaToken = data.recaptchaToken,
    )

    fun mapForget(email: String) = ForgetPasswordRequest(email = email)

    fun mapReset(data: PasswordResetDataEntity) = ResetPasswordRequest(
        token = data.token,
        password = data.password,
        passwordConfirmation = data.passwordConfirmation,
    )

    fun mapToken(response: AuthTokenResponse): AuthTokenEntity? =
        response.token?.let(::AuthTokenEntity)
}
