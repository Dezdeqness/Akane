import com.dezdeqness.buildlogic.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class CMPFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("dezdeqness-kmp-library").get().get().pluginId)
                apply(libs.findPlugin("jetbrainsCompose").get().get().pluginId)
                apply(libs.findPlugin("compose-compiler").get().get().pluginId)
            }
        }
    }
}
