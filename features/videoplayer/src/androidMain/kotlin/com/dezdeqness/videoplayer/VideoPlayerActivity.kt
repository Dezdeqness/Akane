package com.dezdeqness.videoplayer

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dezdeqness.designsystem.utils.noRippleClickable
import com.dezdeqness.videoplayer.core.InstallFullScreenState
import com.dezdeqness.videoplayer.core.SystemBarsVisibility
import com.dezdeqness.videoplayer.core.rememberFullScreenState
import com.dezdeqness.videoplayer.navigation.EPISODE_URL

class VideoPlayerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(EPISODE_URL) ?: return

        setContent {
            val systemBarsControllerState = rememberFullScreenState(
                initialNavigationBarVisibility = SystemBarsVisibility.Visible,
                initialStatusBarVisibility = SystemBarsVisibility.Visible
            )

            InstallFullScreenState(systemBarsControllerState)

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
                    .noRippleClickable {
                        if (systemBarsControllerState.isSystemBarVisible) {
                            systemBarsControllerState.hideSystemBar()
                        } else {
                            systemBarsControllerState.showSystemBar()
                        }
                    }
            ) {
                VideoPlayerScreen(videoUrl = url)
            }
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
