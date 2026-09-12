package com.dezdeqness.screenshot

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import coil3.Image
import coil3.ImageLoader
import coil3.asImage
import coil3.compose.LocalPlatformContext
import coil3.decode.DataSource
import coil3.request.SuccessResult
import coil3.test.FakeImageLoaderEngine
import kotlin.coroutines.EmptyCoroutineContext
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Shader
import com.dezdeqness.core.ui.views.image.LocalAstImageLoader
import com.dezdeqness.designsystem.AkaneTheme
import com.dezdeqness.designsystem.AkaneThemeSpec
import com.dezdeqness.designsystem.DarkDesktopTheme
import com.dezdeqness.designsystem.DarkMobileTheme
import com.dezdeqness.designsystem.LightTheme
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage

const val SCREENSHOT_DIR: String = "src/desktopTest/resources/screenshots"

private fun fakePosterImage(width: Int = 300, height: Int = 450): Image {
    val bitmap = Bitmap().apply { allocN32Pixels(width, height) }
    val paint = Paint().apply {
        shader = Shader.makeLinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(0xFF4E6E8E.toInt(), 0xFF25303B.toInt()),
        )
    }
    Canvas(bitmap).drawRect(Rect.makeWH(width.toFloat(), height.toFloat()), paint)
    return bitmap.asImage()
}

@Composable
private fun rememberFakeImageLoader(): ImageLoader {
    val context = LocalPlatformContext.current
    return remember(context) {
        val engine = FakeImageLoaderEngine.Builder()
            .default { chain ->
                SuccessResult(
                    image = fakePosterImage(),
                    request = chain.request,
                    dataSource = DataSource.MEMORY_CACHE,
                )
            }
            .build()
        ImageLoader.Builder(context)
            .components { add(engine) }
            .interceptorCoroutineContext(EmptyCoroutineContext)
            .fetcherCoroutineContext(EmptyCoroutineContext)
            .decoderCoroutineContext(EmptyCoroutineContext)
            .build()
    }
}

private val ScreenshotRoborazziOptions = RoborazziOptions(
    compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01F),
)

const val IMAGE_LOAD_ADVANCE_MS: Long = 100L

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
    advanceMs: Long = 0L,
    shots: List<ThemeShot> = DefaultShots,
    content: @Composable (Viewport) -> Unit,
) {
    shots.forEach { shot ->
        runDesktopComposeUiTest(width = shot.viewport.widthPx, height = shot.viewport.heightPx) {
            mainClock.autoAdvance = false

            setContent {
                CompositionLocalProvider(LocalAstImageLoader provides rememberFakeImageLoader()) {
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

            if (advanceMs > 0L) mainClock.advanceTimeBy(advanceMs)

            onRoot().captureRoboImage(
                filePath = "$SCREENSHOT_DIR/${name}_${shot.themeLabel}_${shot.viewport.label}.png",
                roborazziOptions = ScreenshotRoborazziOptions,
            )
        }
    }
}
