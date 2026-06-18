# Akane — agent instructions

KMP + Compose Multiplatform anime app (Android / iOS / Desktop). Koin for DI, Ktorfit/Ktor for networking, Kotlin Coroutines + Flow.

## Building feature/contract slices — MANDATORY

Before creating or extending any backend-backed slice — a new `:contract:*` module, a network service/DTO in `:common:network`, a feature data layer, a repository impl, or Koin DI wiring — **ALWAYS invoke the `akane-feature-module` skill via the Skill tool first.** It defines the canonical `contract → network → data → domain → DI` pattern for this repo.

UI work (Compose sections/screens, navigation + route registration in the `NavKey` `SerializersModule`, adaptive mobile/wide layouts, plural string resources) is **not** covered by that skill — follow the existing `features/feed`, `features/home`, `features/genre` code.

## Module layout

- `contract/<name>` — pure entities + repository interfaces (`com.dezdeqness.<name>.contract`), no Compose.
- `common/<name>` — shared infrastructure: `network`, `designsystem`, `foundation`, `analytics`.
- `shared/<name>` — shared presentation (e.g. `:shared:catalog-ui` — `ReleaseCard` / `ReleaseGrid` / `ReleaseUiMapper`).
- `features/<name>` — feature implementation (data + UI + DI); plugin `dezdeqness.cmp.feature`.
- Register every module in root `settings.gradle.kts`; aggregate Koin modules in `akane-shared/.../shared/di/KoinModules.kt`.

## Conventions

- Entities: `data class …Entity`. Repository methods: `suspend fun … : Result<…>`.
- DTOs: `@Serializable`, suffix `Response`, snake_case via `@SerialName`, in `:common:network`. Server pagination = shared `GeneralResponse<T>` + `Meta` + `Pagination`.
- Reuse shared models instead of duplicating: catalog `ReleaseEntity` for release-list items; `:shared:catalog-ui` for the release grid/card.
- Every navigable route must be registered in the `NavKey` polymorphic `SerializersModule` (`akane-shared/.../NavSavedStateConfiguration.kt`) — otherwise navigation crashes at runtime.
- No auto-commits.

## Verify

`./gradlew :akane-android:compileDebugKotlin` compiles common + Android (catches most cross-module breakage). Module-scoped checks: `./gradlew :<path>:compileCommonMainKotlinMetadata`.

Note: when checking build status via a pipe (`./gradlew … | grep …`), the reported exit code is the pipe's last stage, not Gradle's. Capture Gradle's real status explicitly, e.g. `./gradlew … > build.log 2>&1; echo "EXIT=$?"`.
