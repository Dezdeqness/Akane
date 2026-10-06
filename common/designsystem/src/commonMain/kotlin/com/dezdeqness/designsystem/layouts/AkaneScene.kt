package com.dezdeqness.designsystem.layouts

object AkaneScene {

    enum class NavigationBars {
        Visible,
        HideBottomBar,
        Hidden,
    }

    private const val NAVIGATION_BARS = "akane.scene.navigation_bars"

    fun hideBottomBar(): Map<String, Any> = mapOf(NAVIGATION_BARS to NavigationBars.HideBottomBar)

    fun fullScreen(): Map<String, Any> = mapOf(NAVIGATION_BARS to NavigationBars.Hidden)

    fun navigationBarsOf(metadata: Map<String, Any>): NavigationBars =
        metadata[NAVIGATION_BARS] as? NavigationBars ?: NavigationBars.Visible
}
