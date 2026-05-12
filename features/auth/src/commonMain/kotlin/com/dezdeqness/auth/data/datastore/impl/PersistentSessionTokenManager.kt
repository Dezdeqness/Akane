package com.dezdeqness.auth.data.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dezdeqness.auth.data.datastore.SessionTokenStore
import kotlinx.coroutines.flow.first

class PersistentSessionTokenManager(
    private val dataStore: DataStore<Preferences>
) : SessionTokenStore {

    override suspend fun setToken(token: String) {
        dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    override suspend fun getToken() = dataStore.data.first()[TOKEN_KEY].orEmpty()

    override suspend fun clear() {
        dataStore.edit {
            it.remove(TOKEN_KEY)
        }
    }

    private companion object {
        val TOKEN_KEY = stringPreferencesKey("session_token")
    }
}
