package com.dezdeqness.personal.ui

import com.dezdeqness.screenshot.IMAGE_LOAD_ADVANCE_MS
import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import com.dezdeqness.personal.ui.model.PersonalUiModel
import com.dezdeqness.screenshot.screenshotViewports
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test

class PersonalScreenshotTest {

    @Test
    fun loaded() = screenshotViewports("personal_loaded", advanceMs = IMAGE_LOAD_ADVANCE_MS) { _ ->
        PersonalPage(
            stateFlow = MutableStateFlow(
                PersonalState(list = fakePersonalList(), status = Status.Loaded),
            ),
            actions = NoOpPersonalActions,
        )
    }

    @Test
    fun loading() = screenshotViewports("personal_loading") { _ ->
        PersonalPage(
            stateFlow = MutableStateFlow(PersonalState(status = Status.Loading)),
            actions = NoOpPersonalActions,
        )
    }

    @Test
    fun empty() = screenshotViewports("personal_empty") { _ ->
        PersonalPage(
            stateFlow = MutableStateFlow(PersonalState(status = Status.Empty)),
            actions = NoOpPersonalActions,
        )
    }

    @Test
    fun error() = screenshotViewports("personal_error") { _ ->
        PersonalPage(
            stateFlow = MutableStateFlow(PersonalState(status = Status.Error)),
            actions = NoOpPersonalActions,
        )
    }

    @Test
    fun unauthorized() = screenshotViewports("personal_unauthorized") { _ ->
        PersonalPage(
            stateFlow = MutableStateFlow(PersonalState(status = Status.Unauthorized)),
            actions = NoOpPersonalActions,
        )
    }
}

private fun fakePersonalList(count: Int = 12): List<PersonalUiModel> = List(count) { index ->
    PersonalUiModel(
        id = index.toLong(),
        name = "Saved release $index",
        poster = FAKE_IMAGE_URL,
    )
}

private object NoOpPersonalActions : PersonalActions {
    override fun onItemClicked(details: PersonalUiModel) = Unit
    override fun onEmptyListActionClicked() = Unit
    override fun onLoadMore() = Unit
    override fun onRetryClicked() = Unit
    override fun onNavigateToProfile() = Unit
}
