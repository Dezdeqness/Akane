package com.dezdeqness.auth.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDataStore(): DataStore<Preferences> {
    val storagePath = storageDirectory() + "/" + SESSION_PREFERENCES_FILE
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { storagePath.toPath() }
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun storageDirectory(): String {
    val fileManager = NSFileManager.defaultManager
    val documentDirectory = fileManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val storageUrl = documentDirectory?.URLByAppendingPathComponent("storage", isDirectory = true)
        ?: error("Unable to resolve storage directory")

    if (fileManager.fileExistsAtPath(storageUrl.path ?: "").not()) {
        fileManager.createDirectoryAtURL(
            url = storageUrl,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
    }

    return requireNotNull(storageUrl.path)
}

private const val SESSION_PREFERENCES_FILE = "session.preferences_pb"
