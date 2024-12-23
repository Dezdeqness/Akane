package com.dezdeqness.network.di

import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.services.FeedService
import com.dezdeqness.network.services.createFeedService
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = true
            isLenient = true
        }
    }

    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = LogLevel.INFO
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded)
            }
        }
    }

    single<Ktorfit> {
        Ktorfit
            .Builder()
            .baseUrl(BaseUrl.API_URL)
            .httpClient(get<HttpClient>())
            .converterFactories(ResponseConverterFactory())
            .build()
    }

    single<FeedService> {
        get<Ktorfit>().createFeedService()
    }
}
