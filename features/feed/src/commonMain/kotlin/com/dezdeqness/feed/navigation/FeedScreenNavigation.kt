package com.dezdeqness.feed.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.feed.ui.FeedPage
import kotlinx.serialization.Serializable

@Serializable
data object FeedRoute : NavKey

fun EntryProviderScope<NavKey>.feedEntries(onReleaseClicked: (Long, String) -> Unit) {
    entry<FeedRoute> {
        FeedPage(onReleaseClicked = onReleaseClicked)
    }
}
