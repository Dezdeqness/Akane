package com.dezdeqness.network.auth

interface TokenProvider {
    suspend fun getToken(): String
}
