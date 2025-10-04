package com.dezdeqness.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppColors
import com.dezdeqness.core.ui.theme.AppCustomTheme
import com.dezdeqness.core.ui.theme.AppShapes
import com.dezdeqness.core.ui.theme.AppTypography

@Composable
fun AkaneTheme(
    colors: AppColors = if (isSystemInDarkTheme()) akaneDarkColors() else akaneLightColors(),
    materialDefaultTheme: ColorScheme = if (isSystemInDarkTheme()) toDarkMaterialScheme() else toLightMaterialScheme(),
    typography: AppTypography = AppTypography(),
    shapes: AppShapes = AppShapes(),
    content: @Composable () -> Unit
) {
    val rememberedColors = remember { colors.copy() }.apply { updateColorsFrom(colors) }

    MaterialTheme(colorScheme = materialDefaultTheme) {
        AppCustomTheme(
            colors = rememberedColors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}

fun toLightMaterialScheme(): ColorScheme = lightColorScheme(
    primary = AkaneLightColors.Primary,
    onPrimary = AkaneLightColors.OnPrimary,
    primaryContainer = AkaneLightColors.PrimaryVariant,
    onPrimaryContainer = AkaneLightColors.OnPrimary,

    secondary = AkaneLightColors.Secondary,
    onSecondary = AkaneLightColors.OnSecondary,
    secondaryContainer = AkaneLightColors.SurfaceVariant,
    onSecondaryContainer = AkaneLightColors.OnSecondary,

    tertiary = AkaneLightColors.Accent,
    onTertiary = AkaneLightColors.OnSecondary,
    tertiaryContainer = AkaneLightColors.SurfaceVariant,
    onTertiaryContainer = AkaneLightColors.OnSurface,

    background = AkaneLightColors.Background,
    onBackground = AkaneLightColors.OnBackground,

    surface = AkaneLightColors.Surface,
    onSurface = AkaneLightColors.OnSurface,
    surfaceVariant = AkaneLightColors.SurfaceVariant,
    onSurfaceVariant = AkaneLightColors.OnSurface,

    error = AkaneLightColors.Error,
    onError = Color.White,
    errorContainer = AkaneLightColors.Error.copy(alpha = 0.1f),
    onErrorContainer = AkaneLightColors.Error,

    outline = AkaneLightColors.Border,
    inverseOnSurface = AkaneLightColors.Surface,
    inverseSurface = AkaneLightColors.OnBackground,
    inversePrimary = AkaneLightColors.PrimaryVariant,

    scrim = Color.Black
)

fun toDarkMaterialScheme(): ColorScheme = darkColorScheme(
    primary = AkaneDarkColors.Primary,
    onPrimary = AkaneDarkColors.OnPrimary,
    primaryContainer = AkaneDarkColors.PrimaryVariant,
    onPrimaryContainer = AkaneDarkColors.OnPrimary,

    secondary = AkaneDarkColors.Secondary,
    onSecondary = AkaneDarkColors.OnSecondary,
    secondaryContainer = AkaneDarkColors.SurfaceVariant,
    onSecondaryContainer = AkaneDarkColors.OnSecondary,

    tertiary = AkaneDarkColors.Accent,
    onTertiary = AkaneDarkColors.OnSecondary,
    tertiaryContainer = AkaneDarkColors.SurfaceVariant,
    onTertiaryContainer = AkaneDarkColors.OnSurface,

    background = AkaneDarkColors.Background,
    onBackground = AkaneDarkColors.OnBackground,

    surface = AkaneDarkColors.Surface,
    onSurface = AkaneDarkColors.OnSurface,
    surfaceVariant = AkaneDarkColors.SurfaceVariant,
    onSurfaceVariant = AkaneDarkColors.OnSurface,

    error = AkaneDarkColors.Error,
    onError = Color.White,
    errorContainer = AkaneDarkColors.Error.copy(alpha = 0.2f),
    onErrorContainer = AkaneDarkColors.Error,

    outline = AkaneDarkColors.Border,
    inverseOnSurface = AkaneDarkColors.Surface,
    inverseSurface = AkaneDarkColors.OnBackground,
    inversePrimary = AkaneDarkColors.PrimaryVariant,

    scrim = Color.Black
)
