package com.dezdeqness.auth.data.datastore

import com.dezdeqness.network.auth.TokenProvider

interface SessionTokenStore : TokenProvider {
    suspend fun setToken(token: String)
    override suspend fun getToken(): String
    suspend fun clear()
}
