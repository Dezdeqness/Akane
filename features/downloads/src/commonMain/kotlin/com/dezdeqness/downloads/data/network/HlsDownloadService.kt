package com.dezdeqness.downloads.data.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Streaming
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.statement.HttpStatement

interface HlsDownloadService {

    @GET("")
    suspend fun fetchM3u8(@Url url: String): String

    @Streaming
    @GET("")
    suspend fun downloadSegment(@Url url: String): HttpStatement
}
