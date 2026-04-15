package com.dezdeqness.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dezdeqness.home.ui.HomePageStandalone

const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeScreen(onItemClicked: (Long, String) -> Unit) {
    composable(HOME_ROUTE) {
        HomePageStandalone(onItemClicked = onItemClicked)
    }
}
