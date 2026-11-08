package com.dezdeqness.stories.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.designsystem.layouts.AkaneScene
import com.dezdeqness.stories.ui.StoriesPage
import com.dezdeqness.stories.ui.StoriesViewModel
import com.dezdeqness.stories.ui.favourite.StoryFavouriteViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class StoriesRoute(val collectionIndex: Int) : NavKey

fun EntryProviderScope<NavKey>.storiesEntries(
    onBackPressed: () -> Unit,
    onEpisodeClicked: (releaseId: Long, episodeId: String) -> Unit,
    onDetailsClicked: (releaseId: Long, title: String) -> Unit,
) {
    entry<StoriesRoute>(metadata = AkaneScene.fullScreen()) { key ->
        val viewModel: StoriesViewModel = koinViewModel { parametersOf(key.collectionIndex) }
        val favouriteViewModel: StoryFavouriteViewModel = koinViewModel()
        StoriesPage(
            viewModel = viewModel,
            favouriteViewModel = favouriteViewModel,
            onClose = onBackPressed,
            onEpisodeClicked = onEpisodeClicked,
            onDetailsClicked = onDetailsClicked,
        )
    }
}

fun NavBackStack<NavKey>.navigateToStories(collectionIndex: Int) {
    add(StoriesRoute(collectionIndex))
}
