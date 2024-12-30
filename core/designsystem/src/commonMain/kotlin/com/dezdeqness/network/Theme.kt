package com.dezdeqness.network

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColorScheme(
    primary = lightPrimary,
    primaryContainer = lightPrimaryContainer,
    onPrimary = lightOnPrimary,
    secondary = lightSecondary,
    secondaryContainer = lightSecondaryContainer,
    onSecondary = lightOnSecondary,
    tertiary = lightTertiary,
    tertiaryContainer = lightTertiaryContainer,
    onTertiary = lightOnTertiary,
    background = lightBackground,
    onBackground = lightOnBackground,
    surface = lightSurface,
    onSurface = lightOnSurface,
    error = lightError,
    onError = lightOnError,
    outline = lightOutline,
)

private val DarkColorPalette = darkColorScheme(
    primary = darkPrimary,
    primaryContainer = darkPrimaryContainer,
    onPrimary = darkOnPrimary,
    secondary = darkSecondary,
    secondaryContainer = darkSecondaryContainer,
    onSecondary = darkOnSecondary,
    tertiary = darkTertiary,
    tertiaryContainer = darkTertiaryContainer,
    onTertiary = darkOnTertiary,
    background = darkBackground,
    onBackground = darkOnBackground,
    surface = darkSurface,
    onSurface = darkOnSurface,
    error = darkError,
    onError = darkOnError,
    outline = darkOutline,
)

data class ExtendedColors(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color
)

private val LightExtendedColors = ExtendedColors(
    success = lightSuccess,
    onSuccess = lightOnSuccess,
    warning = lightWarning,
    onWarning = lightOnWarning
)

private val DarkExtendedColors = ExtendedColors(
    success = darkSuccess,
    onSuccess = darkOnSuccess,
    warning = darkWarning,
    onWarning = darkOnWarning,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun AkaneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colors,
            content = content
        )
    }
}
