package app.merp.kmp.own.digital.sign.com.saver

import android.graphics.Bitmap
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


actual suspend fun imageBitmapSaver(imageBitmap: ImageBitmap) {

    val bitmap: Bitmap = imageBitmap.asAndroidBitmap()
    val directory = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "SignatureApp"
    )

    if (!directory.exists()) {
        directory.mkdirs()
    }

    val fileName = "signature_${System.currentTimeMillis()}.png"
    val file = File(directory, fileName)

    try {
        withContext(Dispatchers.IO) {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        }
        println("Saved to: ${file.absolutePath}")
    } catch (e: IOException) {
        e.printStackTrace()
    }
}