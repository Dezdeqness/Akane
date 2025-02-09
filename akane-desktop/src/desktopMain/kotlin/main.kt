import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dezdeqness.shared.App
import com.dezdeqness.shared.di.KoinModules

fun main() = application {
    KoinModules.initKoinModules()
    Window(
        onCloseRequest = ::exitApplication,
        title = "Akane",
    ) {
        App()
    }
}
