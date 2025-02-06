import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.dezdeqness.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "com.dezdeqness.kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "com.dezdeqness.cmp.feature"
            implementationClass = "CMPFeatureConventionPlugin"
        }
        register("kmpKoin") {
            id = "com.dezdeqness.kmp.koin"
            implementationClass = "KMPKoinConventionPlugin"
        }
    }
}
