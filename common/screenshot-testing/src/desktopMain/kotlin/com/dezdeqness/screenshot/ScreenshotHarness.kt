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
import com.dezdeqness.designsystem.AkaneThemeSpec
import com.dezdeqness.designsystem.DarkDesktopTheme
import com.dezdeqness.designsystem.DarkMobileTheme
import com.dezdeqness.designsystem.LightTheme
import com.dezdeqness.designsystem.imageloader.getImageLoader
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

const val SCREENSHOT_DIR: String = "src/desktopTest/resources/screenshots"

private val ScreenshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01F),
)

data class ThemeShot(
    val themeLabel: String,
    val spec: AkaneThemeSpec,
    val viewport: Viewport,
)

val DefaultShots: List<ThemeShot> = buildList {
    listOf(Viewport.Mobile, Viewport.Tablet).forEach { viewport ->
        add(ThemeShot("light", LightTheme, viewport))
        add(ThemeShot("dark", DarkMobileTheme, viewport))
    }
    add(ThemeShot("dark", DarkDesktopTheme, Viewport.Desktop))
}

@OptIn(ExperimentalTestApi::class)
fun screenshotViewports(
    name: String,
    shots: List<ThemeShot> = DefaultShots,
    content: @Composable (Viewport) -> Unit,
) {
    shots.forEach { shot ->
        runDesktopComposeUiTest(width = shot.viewport.widthPx, height = shot.viewport.heightPx) {
            mainClock.autoAdvance = false

            setContent {
                CompositionLocalProvider(LocalAstImageLoader provides getImageLoader()) {
                    AkaneTheme(theme = shot.spec) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background,
                        ) {
                            AdaptiveLayout(modifier = Modifier.fillMaxSize()) {
                                content(shot.viewport)
                            }
                        }
                    }
                }
            }

            onRoot().captureRoboImage(
                filePath = "$SCREENSHOT_DIR/${name}_${shot.themeLabel}_${shot.viewport.label}.png",
                roborazziOptions = ScreenshotRoborazziOptions,
            )
        }
    }
}
