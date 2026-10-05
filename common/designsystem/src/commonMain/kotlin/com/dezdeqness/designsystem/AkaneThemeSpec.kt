package com.dezdeqness.designsystem

import androidx.compose.material3.ColorScheme
import com.dezdeqness.core.ui.theme.AppColors

data class AkaneThemeSpec(
    val colors: AppColors,
    val scheme: ColorScheme,
    val isDark: Boolean,
)

val LightTheme: AkaneThemeSpec = AkaneThemeSpec(
    colors = akaneLightColors(),
    scheme = toLightMaterialScheme(),
    isDark = false,
)

val DarkMobileTheme: AkaneThemeSpec = AkaneThemeSpec(
    colors = akaneDarkColors(),
    scheme = toDarkMaterialScheme(),
    isDark = true,
)
