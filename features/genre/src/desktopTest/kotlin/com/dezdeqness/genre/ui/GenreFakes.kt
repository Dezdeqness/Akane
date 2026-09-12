package com.dezdeqness.genre.ui

import com.dezdeqness.screenshot.FAKE_IMAGE_URL
import com.dezdeqness.genre.ui.model.GenreUiModel

fun fakeGenreList(count: Int = 15): List<GenreUiModel> = List(count) { index ->
    GenreUiModel(
        id = index,
        name = genreNames[index % genreNames.size],
        imageUrl = FAKE_IMAGE_URL,
        totalReleases = (index + 1) * 11,
    )
}

private val genreNames = listOf(
    "Action", "Romance", "Comedy", "Drama", "Fantasy",
    "Sci-Fi", "Slice of Life", "Thriller", "Mystery", "Sports",
    "Horror", "Adventure", "Mecha", "Music", "Historical",
)
