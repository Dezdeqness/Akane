package com.dezdeqness.akane

import com.dezdeqness.downloads.data.manager.engine.AvAssetBackgroundSessionHolder
import com.dezdeqness.downloads.data.manager.engine.AvAssetDownloadEngine
import com.dezdeqness.downloads.data.manager.engine.DownloadEngine
import com.dezdeqness.shared.di.KoinModules
import org.koin.mp.KoinPlatform

object BackgroundDownloadsBridge {

    fun handleBackgroundSessionEvents(identifier: String, completionHandler: () -> Unit) {
        val stored = AvAssetBackgroundSessionHolder.storeCompletionHandler(identifier, completionHandler)
        if (!stored) {
            completionHandler()
            return
        }

        KoinModules.initKoinModules()
        (KoinPlatform.getKoin().get<DownloadEngine>() as? AvAssetDownloadEngine)?.warmUp()
    }
}
