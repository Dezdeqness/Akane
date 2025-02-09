import com.dezdeqness.buildlogic.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class CMPFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.rootProject.project
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("dezdeqness-kmp-library").get().get().pluginId)
                apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)
                apply(libs.findPlugin("compose-compiler").get().get().pluginId)
            }
            dependencies {
                add("commonMainImplementation", project(":core:designsystem"))
                add("commonMainImplementation", project(":core:network"))
                add("commonMainImplementation", libs.findLibrary("androidx-navigation-compose").get())
            }
        }
    }
}
