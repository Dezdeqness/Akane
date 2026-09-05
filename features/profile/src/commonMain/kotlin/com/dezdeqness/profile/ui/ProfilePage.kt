package com.dezdeqness.profile.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dezdeqness.core.ui.theme.AppTheme
import com.dezdeqness.core.ui.views.toolbar.AppToolbar
import com.dezdeqness.designsystem.layouts.AdaptiveLayout
import com.dezdeqness.designsystem.layouts.LayoutType
import com.dezdeqness.designsystem.layouts.LocalLayoutType
import com.dezdeqness.foundation.utils.collectAsStateOnLifecycle
import com.dezdeqness.profile.ui.composables.ProfileErrorContent
import com.dezdeqness.profile.ui.composables.ProfileInfoContent
import com.dezdeqness.profile.ui.composables.ProfileLoadingContent
import com.dezdeqness.profile.ui.model.ProfileUiItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    profileStateFlow: StateFlow<ProfileState>,
    onRetryClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
) {
    val profileState by profileStateFlow.collectAsStateOnLifecycle()

    Scaffold(
        modifier = modifier,
        containerColor = AppTheme.colors.background,
        topBar = {
            AppToolbar(
                title = "Профиль",
                navigationIcon = null,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.background,
                ),
            )
        },
    ) { paddingValues ->
        AdaptiveLayout(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            when {
                profileState.profile != null -> ProfileLoaded(
                    profile = profileState.profile!!,
                    onLogoutClicked = onLogoutClicked,
                )

                profileState.isError && !profileState.isLoading -> ProfileErrorContent(
                    onRetryClicked = onRetryClicked,
                )

                profileState.isLoading -> ProfileLoadingContent()
            }
        }
    }
}

@Composable
private fun ProfileLoaded(
    profile: ProfileUiItem,
    onLogoutClicked: () -> Unit,
) {
    when (LocalLayoutType.current) {
        LayoutType.Mobile -> ProfileInfoContent(
            modifier = Modifier.fillMaxSize(),
            profile = profile,
            onLogoutClicked = onLogoutClicked,
            avatarSize = 120.dp,
            contentMaxWidth = Dp.Unspecified,
        )

        LayoutType.Tablet -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ProfileInfoContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 520.dp),
                profile = profile,
                onLogoutClicked = onLogoutClicked,
                avatarSize = 160.dp,
                contentMaxWidth = 520.dp,
            )
        }

        LayoutType.Desktop -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            ProfileInfoContent(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 640.dp),
                profile = profile,
                onLogoutClicked = onLogoutClicked,
                avatarSize = 200.dp,
                contentMaxWidth = 640.dp,
            )
        }
    }
}
