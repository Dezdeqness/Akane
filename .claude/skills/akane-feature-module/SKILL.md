---
name: akane-feature-module
description: Build a new vertical feature slice in the Akane KMP app — contract module (entity + repository interface), network layer (Ktorfit service + DTOs in :common:network), data layer (datasource + mapper + repository impl), domain usecases, and Koin DI wiring. Use when adding a new backend-backed domain (genre, comments, search, collections, etc.) or any new :contract:* module.
---

# Adding a feature module in Akane (KMP)

Akane separates **pure contracts** (`:contract:*`) from **feature implementations** (`:features:*`) and from the **shared network layer** (`:common:network`). A new backend-backed domain touches up to 5 layers. Build them in this order, stopping at whatever the task asks for (contract-only is a valid stopping point).

Conventions, verified against the codebase:
- Entities are `data class …Entity`, packages `com.dezdeqness.<name>.contract.{model,repository}`.
- Repository methods are `suspend fun`, return `Result<…>`.
- DTOs are `@Serializable`, in `:common:network`, suffix `Response`, snake_case JSON via `@SerialName`.
- Pagination entity shape: `items / currentPage / nextPage / hasNextPage`. Server pagination DTO is the shared `GeneralResponse<T>` + `Meta` + `Pagination` (already exists).
- Reuse existing entities/DTOs instead of duplicating (e.g. genre releases reuse feed's `ReleaseEntity`; `Genre`/`Image` DTOs already live in `:common:network`).
- No auto-commits.

---

## Layer 1 — Contract module (`:contract:<name>`)

Pure Kotlin, no Compose. Plugin `dezdeqness.kmp.library`.

`contract/<name>/build.gradle.kts`:
```kotlin
plugins { alias(libs.plugins.dezdeqness.kmp.library) }
android { namespace = "com.dezdeqness.<name>.contract" }
kotlin {
    sourceSets {
        commonMain.dependencies {
            // api(libs.kotlinx.coroutines.core)   // ONLY if a repo signature returns Flow
            // api(project(":contract:feed"))       // ONLY to reuse another contract's entity
        }
    }
}
```

Files under `contract/<name>/src/commonMain/kotlin/com/dezdeqness/<name>/contract/`:
- `model/<X>Entity.kt` — `data class …Entity(...)`
- `repository/<Name>Repository.kt` — `interface` with `suspend fun … : Result<…>`

Register in root `settings.gradle.kts`: add `include(":contract:<name>")` next to the other `:contract:*` lines.

Verify: `./gradlew :contract:<name>:compileKotlinMetadata`

---

## Layer 2 — Network (`:common:network`)

Add endpoints to `constants/ApiEndPoints.kt` (reuse the private `ANIME = "anime/"` prefix):
```kotlin
const val GENRES = "${ANIME}genres"
const val GENRE_RELEASES = "${ANIME}genres/{id}/releases"
const val GENRES_RANDOM = "${ANIME}genres/random"
```

DTOs in `models/core/` (shared, e.g. `Genre`, `Image`) or `models/response/` (endpoint-specific). Check first — many already exist. `@Serializable`, `@SerialName` for snake_case:
```kotlin
@Serializable
data class Genre(
    val id: Long,
    val name: String,
    val image: Image,
    @SerialName("total_releases") val totalReleases: Long,
)
```

Ktorfit service in `services/<Name>Service.kt`:
```kotlin
interface GenreService {
    @GET(ApiEndPoints.GENRES)
    suspend fun getGenres(): Response<List<Genre>>

    @GET(ApiEndPoints.GENRE_RELEASES)
    suspend fun getGenreReleases(
        @Path id: Int,
        @Query page: Int,
        @Query limit: Int,
    ): Response<GeneralResponse<List<ReleaseResponse>>>

    @GET(ApiEndPoints.GENRES_RANDOM)
    suspend fun getRandomGenres(@Query limit: Int): Response<List<Genre>>
}
```
Ktorfit generates `Ktorfit.create<Name>Service()`. Register it in `di/NetworkModule.kt`:
```kotlin
single<GenreService> { get<Ktorfit>().createGenreService() }
```

---

## Layer 3 — Data (in the feature module `:features:<name>`)

Datasource interface + impl, mapper, repository impl. Impl extends `BaseDataSource` (gives `tryWithCatchSuspend { … }` for error mapping) and calls the service.

`data/datasource/<Name>ApiDatasource.kt` (interface, returns contract entities) + `data/datasource/impl/<Name>ApiDatasourceImpl.kt`:
```kotlin
class GenreApiDatasourceImpl(
    private val genreService: GenreService,
    private val genreMapper: GenreMapper,
    errorMapper: ErrorMapper,
) : BaseDataSource(errorMapper), GenreApiDatasource {

    override suspend fun getGenres() = tryWithCatchSuspend {
        val response = genreService.getGenres()
        if (response.isSuccessful) {
            Result.success(response.body().orEmpty().map(genreMapper::map))
        } else {
            throw response.createApiException()
        }
    }
}
```

`data/mapper/<Name>Mapper.kt` — DTO → Entity. Prefix image paths with `BaseUrl.BASE_URL_IMAGES`:
```kotlin
class GenreMapper {
    fun map(response: Genre) = GenreEntity(
        id = response.id.toInt(),
        name = response.name,
        image = GenreImageEntity(
            preview = BaseUrl.BASE_URL_IMAGES + response.image.preview,
            thumbnail = BaseUrl.BASE_URL_IMAGES + response.image.thumbnail,
            optimizedPreview = BaseUrl.BASE_URL_IMAGES + response.image.optimized.src.orEmpty(),
            optimizedThumbnail = BaseUrl.BASE_URL_IMAGES + response.image.optimized.thumbnail.orEmpty(),
        ),
        totalReleases = response.totalReleases.toInt(),
    )
}
```
For paginated responses, build the entity from `body.meta.pagination` exactly like `FeedApiDatasourceImpl` (`nextPage = currentPage + 1`, `hasNextPage = nextPage < totalPages`).

`data/repository/<Name>RepositoryImpl.kt` — implements the contract interface, delegates to the datasource.

---

## Layer 4 — Domain usecases (optional)

Only when there's logic beyond a passthrough. Class with `suspend operator fun invoke(...)`, depends on contract repository interfaces. By convention usecases live in the feature module under `domain/usecase/`.

---

## Layer 5 — Koin DI wiring

Three module files in the feature's `di/`, then aggregate.

`di/DataModule.kt`:
```kotlin
val dataModule = module {
    single { GenreMapper() }
    single<GenreApiDatasource> {
        GenreApiDatasourceImpl(genreService = get(), genreMapper = get(), errorMapper = get())
    }
}
```
`di/DomainModule.kt`:
```kotlin
val domainModule = module {
    single<GenreRepository> { GenreRepositoryImpl(genreApiDatasource = get()) }
}
```
`di/<Name>Module.kt` — `includes(dataModule, domainModule)` + `viewModel { … }` declarations.

Register the feature module in `akane-shared/.../shared/di/KoinModules.kt` → `modules()` `buildList { add(<name>Module) }`.

Feature `build.gradle.kts` uses plugin `dezdeqness.cmp.feature`, `api(libs.bundles.ktorfit.common)`, and `api(project(":contract:<name>"))`.

---

## Reference files (read these for exact current syntax)

- Contract example: `contract/genre/`, `contract/feed/`
- Network: `common/network/.../services/CatalogService.kt`, `models/core/GeneralResponse.kt`, `di/NetworkModule.kt`, `datasource/BaseDataSource.kt`, `constants/ApiEndPoints.kt`
- Data: `features/feed/.../data/datasource/impl/FeedApiDatasourceImpl.kt`, `data/mapper/FeedMapper.kt`, `data/repository/FeedRepositoryImpl.kt`
- DI: `features/feed/.../di/{DataModule,DomainModule,FeedModule}.kt`, `akane-shared/.../shared/di/KoinModules.kt`
