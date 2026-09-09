package com.dezdeqness.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import kotlin.test.Test

class ThemeScreenshotTest {

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0F),
    )

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun lightTheme() = runDesktopComposeUiTest {
        setContent {
            AkaneTheme(theme = LightTheme) { ThemeShowcase() }
        }
        onRoot().captureRoboImage(
            filePath = "src/desktopTest/resources/screenshots/theme_light_mobile.png",
            roborazziOptions = options,
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun darkThemeMobile() = runDesktopComposeUiTest {
        setContent {
            AkaneTheme(theme = DarkMobileTheme) { ThemeShowcase() }
        }
        onRoot().captureRoboImage(
            filePath = "src/desktopTest/resources/screenshots/theme_dark_mobile.png",
            roborazziOptions = options,
        )
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun darkThemeDesktop() = runDesktopComposeUiTest {
        setContent {
            AkaneTheme(theme = DarkDesktopTheme) { ThemeShowcase() }
        }
        onRoot().captureRoboImage(
            filePath = "src/desktopTest/resources/screenshots/theme_dark_desktop.png",
            roborazziOptions = options,
        )
    }
}

@Composable
private fun ThemeShowcase() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Akane theme",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}) { Text("Primary") }
                FilledTonalButton(onClick = {}) { Text("Tonal") }
                OutlinedButton(onClick = {}) { Text("Outline") }
            }

            Card {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "Surface / Card",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Secondary text sample",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "Error container",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }

            SwatchRow(
                swatches = listOf(
                    "prim" to MaterialTheme.colorScheme.primary,
                    "sec" to MaterialTheme.colorScheme.secondary,
                    "tert" to MaterialTheme.colorScheme.tertiary,
                    "bg" to MaterialTheme.colorScheme.background,
                    "surf" to MaterialTheme.colorScheme.surface,
                    "err" to MaterialTheme.colorScheme.error,
                ),
            )
        }
    }
}

@Composable
private fun SwatchRow(swatches: List<Pair<String, Color>>) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        swatches.forEach { (label, color) ->
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Surface(
                    color = color,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(44.dp),
                ) {}
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(44.dp).padding(top = 2.dp),
                )
            }
        }
    }
}
