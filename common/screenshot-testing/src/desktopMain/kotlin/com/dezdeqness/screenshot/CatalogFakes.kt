package com.dezdeqness.screenshot

import com.dezdeqness.catalog.ui.model.ReleaseListUiModel

const val FAKE_IMAGE_URL: String = "fake://poster.jpg"

fun fakeReleaseList(count: Int = 12): List<ReleaseListUiModel> = List(count) { index ->
    ReleaseListUiModel(
        id = index.toLong(),
        title = fakeTitles[index % fakeTitles.size],
        summary = "Sample synopsis line for a catalog release card, item #$index.",
        imageUrl = FAKE_IMAGE_URL,
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
