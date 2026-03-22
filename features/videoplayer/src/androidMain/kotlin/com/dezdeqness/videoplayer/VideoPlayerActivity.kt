package com.dezdeqness.videoplayer

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.videoplayer.core.SystemBarsVisibility
import com.dezdeqness.videoplayer.core.rememberFullScreenState
import com.dezdeqness.videoplayer.navigation.DOWNLOAD_RELEASE_ID
import com.dezdeqness.videoplayer.navigation.DOWNLOAD_START_EPISODE_ID
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import kotlinx.coroutines.delay

class VideoPlayerActivity : ComponentActivity() {
    private var lastTouchEvent by mutableIntStateOf(MotionEvent.ACTION_UP)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.navigationBars())
            hide(WindowInsetsCompat.Type.statusBars())
        }

        setContent {
            val systemBarsControllerState = rememberFullScreenState(
                initialNavigationBarVisibility = SystemBarsVisibility.Visible,
                initialStatusBarVisibility = SystemBarsVisibility.Visible
            )

            val shouldTriggerEffect by remember { derivedStateOf { lastTouchEvent == MotionEvent.ACTION_UP } }

            LaunchedEffect(systemBarsControllerState.isSystemBarVisible, shouldTriggerEffect) {
                if (systemBarsControllerState.isSystemBarVisible && shouldTriggerEffect) {
                    delay(5000)
                    systemBarsControllerState.hideSystemBar()
                }
            }

            CompositionLocalProvider(
                LocalAstImageLoader provides getImageLoader()
            ) {
                AkaneTheme {
                    VideoPlayerScreen(
                        onBackButtonClicked = {
                            finish()
                        }
                    )
                }

            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        lastTouchEvent = ev?.action ?: MotionEvent.ACTION_UP
        return super.dispatchTouchEvent(ev)
    }

    companion object {
        fun startActivity(context: Context, id: Long, episodeId: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(ID, id)
                putExtra(EPISODE_ID, episodeId)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

        fun startActivityForDownload(context: Context, releaseId: Long, startEpisodeId: String) {
            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                putExtra(DOWNLOAD_RELEASE_ID, releaseId)
                putExtra(DOWNLOAD_START_EPISODE_ID, startEpisodeId)
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

}
