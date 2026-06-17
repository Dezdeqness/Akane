package com.dezdeqness.genre.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.dezdeqness.genre.ui.all.AllGenresPage
import com.dezdeqness.genre.ui.releases.GenreReleasesPage
import com.dezdeqness.genre.ui.releases.GenreReleasesViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object GenresRoute : NavKey

@Serializable
data class GenreReleasesRoute(
    val genreId: Int,
    val genreName: String,
) : NavKey

fun EntryProviderScope<NavKey>.genreEntries(
    onBackPressed: () -> Unit,
    onGenreClicked: (genreId: Int, genreName: String) -> Unit,
    onReleaseClicked: (releaseId: Long, title: String) -> Unit,
) {
    entry<GenresRoute> {
        AllGenresPage(
            onBackPressed = onBackPressed,
            onGenreClicked = onGenreClicked,
        )
    }
    entry<GenreReleasesRoute> { key ->
        val viewModel: GenreReleasesViewModel = koinViewModel { parametersOf(key.genreId) }
        GenreReleasesPage(
            genreName = key.genreName,
            onBackPressed = onBackPressed,
            onReleaseClicked = onReleaseClicked,
            viewModel = viewModel,
        )
    }
}

fun NavBackStack<NavKey>.navigateToGenres() {
    add(GenresRoute)
}

fun NavBackStack<NavKey>.navigateToGenreReleases(genreId: Int, genreName: String) {
    add(GenreReleasesRoute(genreId = genreId, genreName = genreName))
}
