package com.dezdeqness.auth.data.datastore

interface SessionTokenStore {
    suspend fun setToken(token: String)
    suspend fun getToken(): String
    suspend fun clear()
}