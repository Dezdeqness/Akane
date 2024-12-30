package com.dezdeqness.akane

import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.feed.navigation.feedScreen
import com.dezdeqness.network.AkaneTheme

@Composable
@Preview
fun App() {
    AkaneTheme {
        val navController = rememberNavController()
        NavHost(
            route = "/",
            navController = navController,
            startDestination = FEED_ROUTE
        ) {
            feedScreen()
        }
    }
}
