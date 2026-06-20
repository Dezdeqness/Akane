package com.dezdeqness.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.home.ui.adaptive.HomePageMobile
import com.dezdeqness.home.ui.adaptive.HomePageWide
import com.dezdeqness.home.ui.composables.HomeError
import com.dezdeqness.home.ui.composables.HomeLoading
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    stateFlow: StateFlow<HomeState>,
    actions: HomeActions,
) {
    val state by stateFlow.collectAsStateOnLifecycle()

    Box(
        modifier.fillMaxSize()
    ) {
        when (state.status) {
            StateStatus.Initial,
            StateStatus.Loading -> HomeLoading(modifier = Modifier.fillMaxSize())

            StateStatus.Error -> HomeError(
                modifier = Modifier.align(Alignment.Center),
                onAction = actions::onRetryClicked,
            )

            StateStatus.LoadingMore,
            StateStatus.SecondPartError,
            StateStatus.Loaded -> {
                AdaptiveLayout {
                    val isMobile = LocalLayoutType.current == LayoutType.Mobile

                    if (isMobile) {
                        HomePageMobile(state, actions)
                    } else {
                        HomePageWide(state, actions)
                    }
                }
            }
        }
    }
}
