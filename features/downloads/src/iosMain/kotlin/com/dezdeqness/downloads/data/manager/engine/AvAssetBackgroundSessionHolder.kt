package com.dezdeqness.downloads.data.manager.engine

object AvAssetBackgroundSessionHolder {

    const val SESSION_IDENTIFIER = "com.dezdeqness.akane.downloads"

    private var completionHandler: (() -> Unit)? = null

    fun storeCompletionHandler(identifier: String, handler: () -> Unit): Boolean {
        if (identifier != SESSION_IDENTIFIER) return false
        completionHandler = handler
        return true
    }

    fun consumeCompletionHandler(): (() -> Unit)? {
        val handler = completionHandler
        completionHandler = null
        return handler
    }
}
