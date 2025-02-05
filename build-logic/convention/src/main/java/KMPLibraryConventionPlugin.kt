import com.dezdeqness.buildlogic.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KMPLibraryConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("androidLibrary").get().get().pluginId)
                apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
            }
        }
    }
}
