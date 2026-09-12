plugins {
    alias(libs.plugins.dezdeqness.cmp.feature)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.dezdeqness.personal"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktorfit.common)
            implementation(libs.compose.material3)
            implementation(libs.kermit)
            implementation(libs.compottie)
            implementation(libs.compottie.dot)
            implementation(project(":common:analytics"))
            api(project(":contract:personal"))
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
        }

        val desktopTest by getting

        desktopTest.dependencies {
            implementation(project(":common:screenshot-testing"))
            implementation(libs.roborazzi.compose.desktop)
            implementation(libs.compose.ui.test)
            implementation(compose.desktop.currentOs)
            implementation(kotlin("test"))
        }
    }

}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
