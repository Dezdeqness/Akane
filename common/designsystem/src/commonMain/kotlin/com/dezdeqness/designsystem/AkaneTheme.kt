package com.dezdeqness.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppCustomTheme
import com.dezdeqness.core.ui.theme.AppShapes
import com.dezdeqness.core.ui.theme.AppTypography

@Composable
fun AkaneTheme(
    theme: AkaneThemeSpec = if (isSystemInDarkTheme()) DarkMobileTheme else LightTheme,
    typography: AppTypography = AppTypography(),
    shapes: AppShapes = AppShapes(),
    content: @Composable () -> Unit
) {
    val rememberedColors = remember { theme.colors.copy() }.apply { updateColorsFrom(theme.colors) }

    MaterialTheme(colorScheme = theme.scheme) {
        AppCustomTheme(
            colors = rememberedColors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

fun akaneMaterialScheme(
    dark: Boolean,
    primary: Color,
    primaryVariant: Color,
    secondary: Color,
    accent: Color,
    background: Color,
    surface: Color,
    surfaceVariant: Color,
    border: Color,
    onPrimary: Color,
    onSecondary: Color,
    onBackground: Color,
    onSurface: Color,
    error: Color,
    errorContainer: Color,
): ColorScheme {
    val params: ColorScheme = if (dark) darkColorScheme() else lightColorScheme()
    return params.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryVariant,
        onPrimaryContainer = onPrimary,

        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = surfaceVariant,
        onSecondaryContainer = onSurface,

        tertiary = accent,
        onTertiary = onSecondary,
        tertiaryContainer = surfaceVariant,
        onTertiaryContainer = onSurface,

        background = background,
        onBackground = onBackground,

        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurface,

        surfaceContainerLowest = background,
        surfaceContainerLow = surface,
        surfaceContainer = surface,
        surfaceContainerHigh = surface,
        surfaceContainerHighest = surfaceVariant,
        surfaceBright = if (dark) surfaceVariant else surface,
        surfaceDim = background,
        surfaceTint = primary,

        error = error,
        onError = Color.White,
        errorContainer = errorContainer,
        onErrorContainer = error,

        outline = border,
        inverseOnSurface = background,
        inverseSurface = onBackground,
        inversePrimary = primaryVariant,

        scrim = Color.Black,
    )
}

fun toLightMaterialScheme(): ColorScheme = akaneMaterialScheme(
    dark = false,
    primary = AkaneLightColors.Primary,
    primaryVariant = AkaneLightColors.PrimaryVariant,
    secondary = AkaneLightColors.Secondary,
    accent = AkaneLightColors.Accent,
    background = AkaneLightColors.Background,
    surface = AkaneLightColors.Surface,
    surfaceVariant = AkaneLightColors.SurfaceVariant,
    border = AkaneLightColors.Border,
    onPrimary = AkaneLightColors.OnPrimary,
    onSecondary = AkaneLightColors.OnSecondary,
    onBackground = AkaneLightColors.OnBackground,
    onSurface = AkaneLightColors.OnSurface,
    error = AkaneLightColors.Error,
    errorContainer = AkaneLightColors.ErrorContainer,
)

fun toDarkMaterialScheme(): ColorScheme = akaneMaterialScheme(
    dark = true,
    primary = AkaneDarkColors.Primary,
    primaryVariant = AkaneDarkColors.PrimaryVariant,
    secondary = AkaneDarkColors.Secondary,
    accent = AkaneDarkColors.Accent,
    background = AkaneDarkColors.Background,
    surface = AkaneDarkColors.Surface,
    surfaceVariant = AkaneDarkColors.SurfaceVariant,
    border = AkaneDarkColors.Border,
    onPrimary = AkaneDarkColors.OnPrimary,
    onSecondary = AkaneDarkColors.OnSecondary,
    onBackground = AkaneDarkColors.OnBackground,
    onSurface = AkaneDarkColors.OnSurface,
    error = AkaneDarkColors.Error,
    errorContainer = AkaneDarkColors.ErrorContainer,
)
