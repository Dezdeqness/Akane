package com.dezdeqness.auth.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

fun getDataStore(): DataStore<Preferences> {
    val userHome = System.getProperty("user.home")
    val storageDirectory = File(userHome, ".akane/storage").apply { mkdirs() }
    val storageFile = File(storageDirectory, SESSION_PREFERENCES_FILE)

    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { storageFile.absolutePath.toPath() }
    )
}

private const val SESSION_PREFERENCES_FILE = "session.preferences_pb"
