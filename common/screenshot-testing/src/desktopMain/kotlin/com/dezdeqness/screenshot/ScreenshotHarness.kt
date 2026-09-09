package com.dezdeqness.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

const val SCREENSHOT_DIR: String = "src/desktopTest/resources/screenshots"

private val ScreenshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01F),
)

@OptIn(ExperimentalTestApi::class)
fun screenshotViewports(
    name: String,
    viewports: List<Viewport> = Viewport.entries,
    content: @Composable (Viewport) -> Unit,
) {
    viewports.forEach { viewport ->
        runDesktopComposeUiTest(width = viewport.widthPx, height = viewport.heightPx) {
            mainClock.autoAdvance = false

            setContent {
                CompositionLocalProvider(LocalAstImageLoader provides getImageLoader()) {
                    AkaneTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            AdaptiveLayout(modifier = Modifier.fillMaxSize()) {
                                content(viewport)
                            }
                        }
                    }
                }
            }

            onRoot().captureRoboImage(
                filePath = "$SCREENSHOT_DIR/${name}_${viewport.label}.png",
                roborazziOptions = ScreenshotRoborazziOptions,
            )
        }
    }
}
