package com.dezdeqness.profile.ui

import com.dezdeqness.profile.ui.model.ProfileUiItem
import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class ProfileScreenshotTest {

    @Test
    fun loaded() = screenshotViewports("profile_loaded") { _ ->
        ProfilePage(
            profileStateFlow = MutableStateFlow(
                ProfileState(
                    profile = ProfileUiItem(
                        nickname = "Danyl",
                        avatarUrl = null,
                        joinedDate = "2021 года",
                    ),
                ),
            ),
            onRetryClicked = {},
            onLogoutClicked = {},
        )
    }

    @Test
    fun loading() = screenshotViewports("profile_loading") { _ ->
        ProfilePage(
            profileStateFlow = MutableStateFlow(ProfileState(isLoading = true)),
            onRetryClicked = {},
            onLogoutClicked = {},
        )
    }

    @Test
    fun error() = screenshotViewports("profile_error") { _ ->
        ProfilePage(
            profileStateFlow = MutableStateFlow(ProfileState(isError = true)),
            onRetryClicked = {},
            onLogoutClicked = {},
        )
    }
}
