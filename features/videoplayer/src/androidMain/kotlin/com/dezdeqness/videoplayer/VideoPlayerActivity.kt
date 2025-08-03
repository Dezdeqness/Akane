package com.dezdeqness.videoplayer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dezdeqness.designsystem.utils.noRippleClickable
import com.dezdeqness.videoplayer.core.FullScreenState
import com.dezdeqness.videoplayer.core.SystemBarsVisibility
import com.dezdeqness.videoplayer.core.rememberFullScreenState
import com.dezdeqness.videoplayer.navigation.EPISODE_ID
import com.dezdeqness.videoplayer.navigation.ID
import kotlinx.coroutines.delay

class VideoPlayerActivity : ComponentActivity() {
    private var lastTouchEvent by mutableIntStateOf(MotionEvent.ACTION_UP)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val id = intent.getLongExtra(ID, 0)
        val episodeId = intent.getStringExtra(EPISODE_ID) ?: return

        setContent {
            val systemBarsControllerState = rememberFullScreenState(
                initialNavigationBarVisibility = SystemBarsVisibility.Visible,
                initialStatusBarVisibility = SystemBarsVisibility.Visible
            )

            InstallFullScreenState(systemBarsControllerState)

            val shouldTriggerEffect by remember { derivedStateOf { lastTouchEvent == MotionEvent.ACTION_UP } }

            LaunchedEffect(systemBarsControllerState.isSystemBarVisible, shouldTriggerEffect) {
                if (systemBarsControllerState.isSystemBarVisible && shouldTriggerEffect) {
                    delay(5000)
                    systemBarsControllerState.hideSystemBar()
                }
            }

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
                VideoPlayerScreen(
                    id = id,
                    episodeId = episodeId,
                    systemBarsControllerState = systemBarsControllerState,
                    onBackButtonClicked = {
                        finish()
                    }
                )
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
    }

}

@Composable
fun InstallFullScreenState(state: FullScreenState = rememberFullScreenState()) {

    val mState by rememberUpdatedState(newValue = state)

    val context = LocalContext.current

    val insetsController = remember(context) {
        val window = run {
            while (context is ContextWrapper) {
                if (context is Activity) return@run context
                return@run context.baseContext as Activity
            }
            null
        }?.window ?: return@remember null

        WindowCompat.getInsetsController(window, window.decorView)
    }

    DisposableEffect(
        state.isNavigationBarVisible,
        state.isStatusBarVisible,
        state.isSystemBarVisible
    ) {
        insetsController?.apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (mState.isNavigationBarVisible) show(WindowInsetsCompat.Type.navigationBars())
            else hide(WindowInsetsCompat.Type.navigationBars())

            if (mState.isStatusBarVisible) show(WindowInsetsCompat.Type.statusBars())
            else hide(WindowInsetsCompat.Type.statusBars())
        }

        onDispose {}
    }
}
