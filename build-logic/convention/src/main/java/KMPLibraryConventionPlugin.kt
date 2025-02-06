import com.android.build.gradle.LibraryExtension
import com.dezdeqness.buildlogic.core.configureKmp
import com.dezdeqness.buildlogic.core.configureKotlinAndroid
import com.dezdeqness.buildlogic.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class KMPLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("androidLibrary").get().get().pluginId)
                apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
                apply(libs.findPlugin("dezdeqness-kmp-koin").get().get().pluginId)
            }

            configureKmp()

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs
                    .findVersion("android-targetSdk")
                    .get()
                    .requiredVersion.toInt()
            }

        }
    }
}
