package com.dezdeqness.akane

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.dezdeqness.feed.navigation.FEED_ROUTE
import com.dezdeqness.feed.navigation.feedScreen
import com.dezdeqness.designsystem.AkaneTheme


@Composable
@Preview
fun App() {
    AkaneTheme {
        val navController = rememberNavController()

        var currentRoute by remember {
            mutableStateOf(AkaneBottomTabModel.HOME)
        }

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    tonalElevation = 0.dp,
                ) {
                    AkaneBottomTabModel.entries.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item,
                            onClick = {
                                currentRoute = item
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                route = "/",
                navController = navController,
                startDestination = FEED_ROUTE,
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                feedScreen()
            }
        }
    }
}
