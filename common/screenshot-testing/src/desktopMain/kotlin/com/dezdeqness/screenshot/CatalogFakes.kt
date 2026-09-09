package com.dezdeqness.screenshot

import com.dezdeqness.catalog.ui.model.ReleaseListUiModel

fun fakeReleaseList(count: Int = 12): List<ReleaseListUiModel> = List(count) { index ->
    ReleaseListUiModel(
        id = index.toLong(),
        title = fakeTitles[index % fakeTitles.size],
        summary = "Sample synopsis line for a catalog release card, item #$index.",
        imageUrl = "",
    )
}

private val fakeTitles = listOf(
    "Cyber Blossom",
    "Silent Horizon",
    "Neon Katana",
    "Autumn Requiem",
    "Starbound Diaries",
    "Crimson Circuit",
    "Paper Lanterns",
    "Echoes of Tomorrow",
    "Velvet Storm",
    "Midnight Harbor",
    "Frost & Ember",
    "Wandering Signal",
)
