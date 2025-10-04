package com.dezdeqness.designsystem

import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppColors

object AkaneLightColors {
    val Primary = Color(0xFF5D866C)
    val PrimaryVariant = Color(0xFF4B6D5A)
    val Secondary = Color(0xFFC2A68C)
    val Background = Color(0xFFF5F5F0)
    val Surface = Color(0xFFE6D8C3)
    val SurfaceVariant = Color(0xFFDCCDBF)
    val Border = Color(0xFFBFB1A0)
    val OnPrimary = Color.White
    val OnSecondary = Color.White
    val OnBackground = Color(0xFF1E293B)
    val OnSurface = Color(0xFF1E293B)
    val Error = Color(0xFFDC2626)
    val Success = Color(0xFF16A34A)
    val Warning = Color(0xFFEAB308)
    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF475569)
    val TextDisabled = Color(0xFF94A3B8)
    val Ripple = Color(0x1F000000)
    val Accent = Color(0xFFC2A68C)
}

object AkaneDarkColors {
    val Primary = Color(0xFF5D866C)
    val PrimaryVariant = Color(0xFF4B6D5A)
    val Secondary = Color(0xFFC2A68C)
    val Background = Color(0xFF1F2A24)
    val Surface = Color(0xFF2C3B33)
    val SurfaceVariant = Color(0xFF3B4B44)
    val Border = Color(0xFF4B5C55)
    val OnPrimary = Color.White
    val OnSecondary = Color.White
    val OnBackground = Color(0xFFE6D8C3)
    val OnSurface = Color(0xFFE6D8C3)
    val Error = Color(0xFFF87171)
    val Success = Color(0xFF34D399)
    val Warning = Color(0xFFFACC15)
    val TextPrimary = Color(0xFFE6D8C3)
    val TextSecondary = Color(0xFFBFC4B8)
    val TextDisabled = Color(0xFF94A3B8)
    val Ripple = Color(0x33FFFFFF)
    val Accent = Color(0xFFC2A68C)
}

fun akaneLightColors(): AppColors = AppColors(
    primaryColor = AkaneLightColors.Primary,
    primaryVariantColor = AkaneLightColors.PrimaryVariant,
    secondaryColor = AkaneLightColors.Secondary,
    backgroundColor = AkaneLightColors.Background,
    surfaceColor = AkaneLightColors.Surface,
    surfaceVariantColor = AkaneLightColors.SurfaceVariant,
    borderColor = AkaneLightColors.Border,
    onPrimaryColor = AkaneLightColors.OnPrimary,
    onSecondaryColor = AkaneLightColors.OnSecondary,
    onBackgroundColor = AkaneLightColors.OnBackground,
    onSurfaceColor = AkaneLightColors.OnSurface,
    textPrimaryColor = AkaneLightColors.TextPrimary,
    textSecondaryColor = AkaneLightColors.TextSecondary,
    textDisabledColor = AkaneLightColors.TextDisabled,
    rippleColor = AkaneLightColors.Ripple,
    errorColor = AkaneLightColors.Error,
    successColor = AkaneLightColors.Success,
    warningColor = AkaneLightColors.Warning,
    accentColor = AkaneLightColors.Accent,
)

fun akaneDarkColors(): AppColors = AppColors(
    primaryColor = AkaneDarkColors.Primary,
    primaryVariantColor = AkaneDarkColors.PrimaryVariant,
    secondaryColor = AkaneDarkColors.Secondary,
    backgroundColor = AkaneDarkColors.Background,
    surfaceColor = AkaneDarkColors.Surface,
    surfaceVariantColor = AkaneDarkColors.SurfaceVariant,
    borderColor = AkaneDarkColors.Border,
    onPrimaryColor = AkaneDarkColors.OnPrimary,
    onSecondaryColor = AkaneDarkColors.OnSecondary,
    onBackgroundColor = AkaneDarkColors.OnBackground,
    onSurfaceColor = AkaneDarkColors.OnSurface,
    textPrimaryColor = AkaneDarkColors.TextPrimary,
    textSecondaryColor = AkaneDarkColors.TextSecondary,
    textDisabledColor = AkaneDarkColors.TextDisabled,
    rippleColor = AkaneDarkColors.Ripple,
    errorColor = AkaneDarkColors.Error,
    successColor = AkaneDarkColors.Success,
    warningColor = AkaneDarkColors.Warning,
    accentColor = AkaneDarkColors.Accent,
)
