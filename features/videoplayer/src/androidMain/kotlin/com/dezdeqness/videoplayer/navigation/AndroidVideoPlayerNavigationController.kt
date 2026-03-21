package com.dezdeqness.videoplayer.navigation

import android.content.Context
import androidx.navigation.NavHostController
import com.dezdeqness.videoplayer.VideoPlayerActivity
import org.koin.java.KoinJavaComponent.inject

class AndroidVideoPlayerNavigationController : VideoPlayerNavigationController {
    override fun navigateToPlayer(controller: NavHostController, id: Long, episodeId: String) {
        val context = inject<Context>(Context::class.java)
        VideoPlayerActivity.startActivity(context.value, id, episodeId)
    }

    override fun navigateToDownloadedPlaylist(controller: NavHostController, releaseId: Long, startEpisodeId: String) {
        val context = inject<Context>(Context::class.java)
        VideoPlayerActivity.startActivityForDownload(context.value, releaseId, startEpisodeId)
    }
}
