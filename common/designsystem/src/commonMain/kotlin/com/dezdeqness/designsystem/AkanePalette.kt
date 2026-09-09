package com.dezdeqness.designsystem

import androidx.compose.ui.graphics.Color
import com.dezdeqness.core.ui.theme.AppColors

object AkaneLightColors {
    val Primary = Color(0xFF4E7461)
    val PrimaryVariant = Color(0xFF3D5B4B)
    val Secondary = Color(0xFFC2A68C)
    val Background = Color(0xFFF5F1E8)
    val Surface = Color(0xFFECE3D3)
    val SurfaceVariant = Color(0xFFDFD3BF)
    val Border = Color(0xFFC9BBA5)
    val OnPrimary = Color.White
    val OnSecondary = Color(0xFF2E2519)
    val OnBackground = Color(0xFF2A2620)
    val OnSurface = Color(0xFF2A2620)
    val Error = Color(0xFFC0392B)
    val ErrorContainer = Color(0xFFF6D9D5)
    val Success = Color(0xFF2E7D46)
    val Warning = Color(0xFFB8860B)
    val TextPrimary = Color(0xFF2A2620)
    val TextSecondary = Color(0xFF5A5348)
    val TextDisabled = Color(0xFFA79E8F)
    val Ripple = Color(0x1F000000)
    val Accent = Color(0xFFC2A68C)
}

object AkaneDarkColors {
    val Primary = Color(0xFF86B199)
    val PrimaryVariant = Color(0xFF6B9580)
    val Secondary = Color(0xFFD8BFA3)
    val Background = Color(0xFF1B241F)
    val Surface = Color(0xFF27332C)
    val SurfaceVariant = Color(0xFF354339)
    val Border = Color(0xFF47564C)
    val OnPrimary = Color(0xFF10201A)
    val OnSecondary = Color(0xFF241C12)
    val OnBackground = Color(0xFFECE0CE)
    val OnSurface = Color(0xFFECE0CE)
    val Error = Color(0xFFF2837A)
    val ErrorContainer = Color(0xFF4A2420)
    val Success = Color(0xFF4ED08A)
    val Warning = Color(0xFFF0C651)
    val TextPrimary = Color(0xFFECE0CE)
    val TextSecondary = Color(0xFFC3BBAA)
    val TextDisabled = Color(0xFF7C7566)
    val Ripple = Color(0x33FFFFFF)
    val Accent = Color(0xFFD8BFA3)
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
