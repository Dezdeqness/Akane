package com.dezdeqness.network.auth

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

class AuthInterceptorConfig {
    var tokenProvider: TokenProvider? = null
}

val AuthInterceptor = createClientPlugin("AuthInterceptor", ::AuthInterceptorConfig) {
    val tokenProvider = pluginConfig.tokenProvider

    onRequest { request, _ ->
        val requiresAuth = request.headers[AuthHeader.NAME] == AuthHeader.VALUE
        if (!requiresAuth) return@onRequest

        val token = tokenProvider?.getToken().orEmpty()
        if (token.isNotEmpty()) {
            request.header(HttpHeaders.Authorization, "Bearer $token")
        }
        request.headers.remove(AuthHeader.NAME)
    }
}
