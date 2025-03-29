package app.merp.kmp.own.digital.sign.com.saver

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import app.merp.kmp.own.digital.sign.com.Platform
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.jetbrains.skia.Image
import org.jetbrains.skiko.OS
import org.khronos.webgl.Uint8Array
import org.w3c.dom.*
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

val uint8Array: Uint8Array = js("new Uint8Array(pngBytes)")

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun imageBitmapToBlob(imageBitmap: ImageBitmap): Blob {
    if (OS.Windows.isWindows) {
        val canvas = window.document.createElement("canvas") as HTMLCanvasElement
        canvas.width = imageBitmap.width
        canvas.height = imageBitmap.height
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.drawImage(imageBitmap as CanvasImageSource, 0.0, 0.0)

        return suspendCancellableCoroutine { continuation ->
            canvas.toBlob({ blob ->
                if (blob != null) {
                    continuation.resume(blob) {
                        println("printing Blob is null")
                        Throwable("Error -> Blob is null")
                    }
                } else {
                    continuation.cancel(Throwable("Failed to convert canvas to Blob"))
                }
            }, "image/png")
        }
    } else {
        throw UnsupportedOperationException("ImageBitmap to Blob conversion is only supported on JS platform.")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
actual suspend fun imageBitmapSaver(imageBitmap: ImageBitmap) {
    var blob = Blob()
    val canvas = window.document.createElement("canvas") as HTMLCanvasElement
    canvas.getContext("")
    canvas.width = imageBitmap.width
    canvas.height = imageBitmap.height
    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
    val cas = ctx.createImageData(0.0,0.0)

    val myBlob = suspendCancellableCoroutine { continuation ->
        ctx.canvas.toBlob({ newBlob ->
            if (newBlob != null) {
                continuation.resume(newBlob) {
                    println("printing Blob is null")
                    Throwable("Error -> Blob is null")
                }
            } else {
                continuation.cancel(Throwable("Failed to convert canvas to Blob"))
            }
        }, "image/png")
    }


    if (OS.Windows.isWindows) {
        //val blob = imageBitmapToBlob(imageBitmap)
        val url = URL.createObjectURL(myBlob)
        val link = window.document.createElement("a") as HTMLAnchorElement
        link.href = url
        link.download = "Signature.png"
        link.click()
        URL.revokeObjectURL(url)
    } else {
        // Handle non-JS platforms (e.g., Android, iOS)
        println("ImageBitmap download not supported on this platform.")
    }
}