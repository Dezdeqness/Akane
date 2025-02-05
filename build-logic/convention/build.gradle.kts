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

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "com.dezdeqness.kmp.library"
            implementationClass = "KMPLibraryConventionPlugin"
        }
    }
}