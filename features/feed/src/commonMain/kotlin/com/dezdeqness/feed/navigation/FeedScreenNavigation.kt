package com.dezdeqness.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.dezdeqness.feed.ui.FeedPage

const val FEED_ROUTE = "feed_route"

fun NavGraphBuilder.feedScreen() {
    composable(FEED_ROUTE) {
        FeedPage()
    }
}
