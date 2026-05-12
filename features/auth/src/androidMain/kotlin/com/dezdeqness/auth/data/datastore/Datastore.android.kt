package com.dezdeqness.auth.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

fun getDataStore(context: Context): DataStore<Preferences> {
    val appContext = context.applicationContext
    val storageDirectory = File(appContext.filesDir, "storage").apply { mkdirs() }
    val storageFile = File(storageDirectory, SESSION_PREFERENCES_FILE)

    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { storageFile.absolutePath.toPath() }
    )
}

private const val SESSION_PREFERENCES_FILE = "session.preferences_pb"
