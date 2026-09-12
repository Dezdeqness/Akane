package com.dezdeqness.home.ui

import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class HomeScreenshotTest {

    @Test
    fun loaded() = screenshotViewports("home_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        HomePage(
            stateFlow = MutableStateFlow(fakeHomeState(StateStatus.Loaded)),
            actions = NoOpHomeActions,
        )
    }

    @Test
    fun loading() = screenshotViewports("home_loading") { _ ->
        HomePage(
            stateFlow = MutableStateFlow(fakeHomeState(StateStatus.Loading)),
            actions = NoOpHomeActions,
        )
    }

    @Test
    fun error() = screenshotViewports("home_error") { _ ->
        HomePage(
            stateFlow = MutableStateFlow(fakeHomeState(StateStatus.Error)),
            actions = NoOpHomeActions,
        )
    }
}
