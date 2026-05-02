package com.dezdeqness.videoplayer.navigation

import android.content.Context
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.videoplayer.VideoPlayerActivity
import org.koin.java.KoinJavaComponent.inject

class AndroidVideoPlayerNavigationController : VideoPlayerNavigationController {
    override fun navigateToPlayer(backStack: NavBackStack<NavKey>, id: Long, episodeId: String) {
        val context = inject<Context>(Context::class.java)
        VideoPlayerActivity.startActivity(context.value, id, episodeId)
    }

    override fun navigateToDownloadedPlaylist(backStack: NavBackStack<NavKey>, releaseId: Long, startEpisodeId: String) {
        val context = inject<Context>(Context::class.java)
        VideoPlayerActivity.startActivityForDownload(context.value, releaseId, startEpisodeId)
    }
}
