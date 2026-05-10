package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.AUTH_LOGIN
import com.dezdeqness.network.constants.ApiEndPoints.AUTH_LOGOUT
import com.dezdeqness.network.constants.ApiEndPoints.AUTH_PASSWORD_FORGET
import com.dezdeqness.network.constants.ApiEndPoints.AUTH_PASSWORD_RESET
import com.dezdeqness.network.constants.ApiEndPoints.AUTH_REGISTRATION
import com.dezdeqness.network.models.request.ForgetPasswordRequest
import com.dezdeqness.network.models.request.LoginRequest
import com.dezdeqness.network.models.request.RegistrationRequest
import com.dezdeqness.network.models.request.ResetPasswordRequest
import com.dezdeqness.network.models.response.AuthTokenResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface AuthService {

    @POST(AUTH_LOGIN)
    suspend fun login(@Body body: LoginRequest): Response<AuthTokenResponse>

    @POST(AUTH_LOGOUT)
    suspend fun logout(): Response<AuthTokenResponse>

    @POST(AUTH_REGISTRATION)
    suspend fun register(@Body body: RegistrationRequest): Response<AuthTokenResponse>

    @POST(AUTH_PASSWORD_FORGET)
    suspend fun forgetPassword(@Body body: ForgetPasswordRequest): Response<Unit>

    @POST(AUTH_PASSWORD_RESET)
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<Unit>
}
