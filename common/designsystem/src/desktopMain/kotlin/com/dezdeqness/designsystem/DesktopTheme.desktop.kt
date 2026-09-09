package com.dezdeqness.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppColors

object AkaneDesktopDarkColors {
    val Primary = Color(0xFF7FB59A)
    val PrimaryVariant = Color(0xFF64977E)
    val Secondary = Color(0xFFCBB393)
    val Background = Color(0xFF14171A)
    val Surface = Color(0xFF1C2024)
    val SurfaceVariant = Color(0xFF262B30)
    val Border = Color(0xFF363C42)
    val OnPrimary = Color(0xFF0C1712)
    val OnSecondary = Color(0xFF221B10)
    val OnBackground = Color(0xFFE6E1D6)
    val OnSurface = Color(0xFFE6E1D6)
    val Error = Color(0xFFEF7A6E)
    val ErrorContainer = Color(0xFF41211D)
    val Success = Color(0xFF4FCB86)
    val Warning = Color(0xFFE8B84B)
    val TextPrimary = Color(0xFFE6E1D6)
    val TextSecondary = Color(0xFFA9A296)
    val TextDisabled = Color(0xFF6E6B63)
    val Ripple = Color(0x33FFFFFF)
    val Accent = Color(0xFFD8A05C)
}

private fun akaneDesktopDarkColors(): AppColors = AppColors(
    primaryColor = AkaneDesktopDarkColors.Primary,
    primaryVariantColor = AkaneDesktopDarkColors.PrimaryVariant,
    secondaryColor = AkaneDesktopDarkColors.Secondary,
    backgroundColor = AkaneDesktopDarkColors.Background,
    surfaceColor = AkaneDesktopDarkColors.Surface,
    surfaceVariantColor = AkaneDesktopDarkColors.SurfaceVariant,
    borderColor = AkaneDesktopDarkColors.Border,
    onPrimaryColor = AkaneDesktopDarkColors.OnPrimary,
    onSecondaryColor = AkaneDesktopDarkColors.OnSecondary,
    onBackgroundColor = AkaneDesktopDarkColors.OnBackground,
    onSurfaceColor = AkaneDesktopDarkColors.OnSurface,
    textPrimaryColor = AkaneDesktopDarkColors.TextPrimary,
    textSecondaryColor = AkaneDesktopDarkColors.TextSecondary,
    textDisabledColor = AkaneDesktopDarkColors.TextDisabled,
    rippleColor = AkaneDesktopDarkColors.Ripple,
    errorColor = AkaneDesktopDarkColors.Error,
    successColor = AkaneDesktopDarkColors.Success,
    warningColor = AkaneDesktopDarkColors.Warning,
    accentColor = AkaneDesktopDarkColors.Accent,
)

private fun desktopDarkMaterialScheme(): ColorScheme = akaneMaterialScheme(
    dark = true,
    primary = AkaneDesktopDarkColors.Primary,
    primaryVariant = AkaneDesktopDarkColors.PrimaryVariant,
    secondary = AkaneDesktopDarkColors.Secondary,
    accent = AkaneDesktopDarkColors.Accent,
    background = AkaneDesktopDarkColors.Background,
    surface = AkaneDesktopDarkColors.Surface,
    surfaceVariant = AkaneDesktopDarkColors.SurfaceVariant,
    border = AkaneDesktopDarkColors.Border,
    onPrimary = AkaneDesktopDarkColors.OnPrimary,
    onSecondary = AkaneDesktopDarkColors.OnSecondary,
    onBackground = AkaneDesktopDarkColors.OnBackground,
    onSurface = AkaneDesktopDarkColors.OnSurface,
    error = AkaneDesktopDarkColors.Error,
    errorContainer = AkaneDesktopDarkColors.ErrorContainer,
)

val DarkDesktopTheme: AkaneThemeSpec = AkaneThemeSpec(
    colors = akaneDesktopDarkColors(),
    scheme = desktopDarkMaterialScheme(),
)
