package com.dezdeqness.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
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

    val isLoading = state.status == StateStatus.Loading || state.status == StateStatus.Initial
    val isError = state.status == StateStatus.Error

    Box(
        modifier.fillMaxSize()
    ) {
        when {
            isLoading -> HomeLoading(modifier = Modifier.fillMaxSize())

            isError -> HomeError(
                modifier = Modifier.align(Alignment.Center),
                onAction = actions::onRetryClicked,
            )

            else -> {
                AdaptiveLayout { type ->
                    val isMobile = type == LayoutType.Mobile

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
