import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.dezdeqness.buildlogic.utils.libs

class KMPKoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("ksp").get().get().pluginId)
            }

            dependencies {
                val bom = libs.findLibrary("koin-bom").get()
                add("commonMainImplementation", platform(bom))
                add("commonMainImplementation", libs.findLibrary("koin-core").get())
            }
        }
    }

}
