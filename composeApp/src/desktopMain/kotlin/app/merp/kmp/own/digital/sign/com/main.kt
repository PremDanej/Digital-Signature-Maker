package app.merp.kmp.own.digital.sign.com

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Digital Sign Maker",
    ) {
        App()
    }
}