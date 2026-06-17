package com.dezdeqness.network.services

import com.dezdeqness.network.constants.ApiEndPoints.GENRES
import com.dezdeqness.network.constants.ApiEndPoints.GENRES_RANDOM
import com.dezdeqness.network.constants.ApiEndPoints.GENRE_RELEASES
import com.dezdeqness.network.models.core.GeneralResponse
import com.dezdeqness.network.models.core.Genre
import com.dezdeqness.network.models.response.ReleaseResponse
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface GenreService {
    @GET(GENRES)
    suspend fun getGenres(): Response<List<Genre>>

    @GET(GENRES_RANDOM)
    suspend fun getRandomGenres(@Query limit: Int): Response<List<Genre>>

    @GET(GENRE_RELEASES)
    suspend fun getGenreReleases(
        @Path id: Int,
        @Query page: Int,
        @Query limit: Int,
    ): Response<GeneralResponse<List<ReleaseResponse>>>
}
