package com.dezdeqness.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileStandalonePage(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = koinViewModel(),
) {
    ProfilePage(
        modifier = modifier,
        profileStateFlow = profileViewModel.state,
        onRetryClicked = profileViewModel::onRetryClicked,
        onLogoutClicked = profileViewModel::onLogoutClicked,
    )
}
