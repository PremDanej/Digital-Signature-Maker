package app.merp.kmp.own.digital.sign.com.saver

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.awt.Dialog
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Paths

actual suspend fun imageBitmapSaver(imageBitmap: ImageBitmap) {
    val paths = Paths.get(System.getProperty("user.home"), "Downloads", "signature.png")
    val bitmap = imageBitmap.asSkiaBitmap()
    val skiaImage = Image.makeFromBitmap(bitmap)
    val byteArray = skiaImage.encodeToData()?.bytes ?: ByteArray(0)
    withContext(Dispatchers.IO) {
        Files.write(paths, byteArray)
        println("Save successfully")
    }
}