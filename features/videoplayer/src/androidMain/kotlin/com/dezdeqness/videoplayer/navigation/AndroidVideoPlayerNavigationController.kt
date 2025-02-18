package com.dezdeqness.videoplayer.navigation

import android.content.Context
import androidx.navigation.NavHostController
import com.dezdeqness.videoplayer.VideoPlayerActivity
import org.koin.java.KoinJavaComponent.inject

class AndroidVideoPlayerNavigationController : VideoPlayerNavigationController {
    override fun navigateToPlayer(controller: NavHostController, url: String) {
        val context = inject<Context>(Context::class.java)
        VideoPlayerActivity.startActivity(context.value, url)
    }
}
