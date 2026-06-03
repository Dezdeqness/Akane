package com.dezdeqness.network.di

import com.dezdeqness.network.auth.AuthInterceptor
import com.dezdeqness.network.auth.TokenProvider
import com.dezdeqness.network.constants.BaseUrl
import com.dezdeqness.network.error.ApiErrorMapper
import com.dezdeqness.network.error.ErrorMapper
import com.dezdeqness.network.services.AuthService
import com.dezdeqness.network.services.CalendarService
import com.dezdeqness.network.services.CatalogService
import com.dezdeqness.network.services.FavoritesService
import com.dezdeqness.network.services.FranchiseService
import com.dezdeqness.network.services.ProfileService
import com.dezdeqness.network.services.createAuthService
import com.dezdeqness.network.services.createCalendarService
import com.dezdeqness.network.services.createCatalogService
import com.dezdeqness.network.services.createFavoritesService
import com.dezdeqness.network.services.createFranchiseService
import com.dezdeqness.network.services.createProfileService
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single<ErrorMapper> { ApiErrorMapper() }

    single<Json> {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = true
            isLenient = true
        }
    }

    single<HttpClient> {
        val tokenProvider: TokenProvider = get()
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.Companion.SIMPLE
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            install(AuthInterceptor) {
                this.tokenProvider = tokenProvider
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

    single<CatalogService> {
        get<Ktorfit>().createCatalogService()
    }

    single<FranchiseService> {
        get<Ktorfit>().createFranchiseService()
    }

    single<CalendarService> {
        get<Ktorfit>().createCalendarService()
    }

    single<AuthService> {
        get<Ktorfit>().createAuthService()
    }

    single<ProfileService> {
        get<Ktorfit>().createProfileService()
    }

    single<FavoritesService> {
        get<Ktorfit>().createFavoritesService()
    }
}
