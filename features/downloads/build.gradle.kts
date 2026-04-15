plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ktorfit)
}

android {
    namespace = "com.dezdeqness.downloads"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.material.icons.core)
            implementation(libs.kermit)
            implementation(project(":common:analytics"))
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.okio)
            implementation(libs.ktor.core)
            implementation(libs.bundles.ktorfit.common)

            implementation(libs.compottie)
            implementation(libs.compottie.dot)
        }
        androidMain.dependencies {
            implementation(libs.media3.transformer)
            implementation(libs.media3.common)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)

    add("kspCommonMainMetadata", libs.ktorfit.ksp)
    add("kspAndroid", libs.ktorfit.ksp)
    add("kspIosX64", libs.ktorfit.ksp)
    add("kspIosArm64", libs.ktorfit.ksp)
    add("kspIosSimulatorArm64", libs.ktorfit.ksp)
    add("kspDesktop", libs.ktorfit.ksp)
}

room {
    schemaDirectory("$projectDir/schemas")
}
