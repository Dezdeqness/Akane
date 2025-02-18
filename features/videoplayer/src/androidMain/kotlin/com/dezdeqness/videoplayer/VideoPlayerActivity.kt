package com.dezdeqness.videoplayer

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.dezdeqness.videoplayer.navigation.EPISODE_URL

class VideoPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        WindowCompat
            .getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = true

        val url = intent.getStringExtra(EPISODE_URL) ?: return

        setContent {
            VideoPlayerScreen(videoUrl = url)
        }
    }

    companion object {
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(EPISODE_URL, url)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

}
