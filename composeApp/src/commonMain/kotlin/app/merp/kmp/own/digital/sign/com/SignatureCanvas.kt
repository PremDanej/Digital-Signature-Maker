package app.merp.kmp.own.digital.sign.com

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import app.merp.kmp.own.digital.sign.com.saver.imageBitmapSaver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class CanvasProperty(
    val path: Path,
    val color: Color
)

@Composable
fun SignatureCanvas() {

    // List to hold multiple paths with color
    var paths by remember { mutableStateOf(mutableListOf<CanvasProperty>()) }

    // Temporary current path while drawing
    var currentPath by remember { mutableStateOf(Path()) }
    var brushColor by remember { mutableStateOf(Color.Black) }

    var lastX by remember { mutableStateOf(0f) }
    var lastY by remember { mutableStateOf(0f) }


    // Store the canvas image as bitmap
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Color picker row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val colors = listOf(
                Color.Black,
                Color.Red,
                Color.Blue,
                Color.Green,
                Color.Magenta,
                Color.Cyan,
                Color.Yellow
            )

            colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, shape = CircleShape)
                        .clickable {
                            brushColor = color   // Set selected color on click
                        }
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Color.White)
                .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply {
                                moveTo(offset.x, offset.y)
                            }
                            lastX = offset.x
                            lastY = offset.y
                        },
                        onDrag = { change, dragAmount ->
                            currentPath = Path().apply {
                                addPath(currentPath)
                                lineTo(lastX + dragAmount.x, lastY + dragAmount.y)
                            }
                            lastX += dragAmount.x
                            lastY += dragAmount.y
                        },
                        onDragEnd = {
                            // Add the finished path to the list
                            paths = paths
                                .toMutableList()
                                .apply {
                                    add(CanvasProperty(currentPath, brushColor))
                                }
                            currentPath = Path()
                        }

                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                paths.forEach { strokePath ->
                    drawPath(
                        path = strokePath.path,
                        color = strokePath.color,
                        style = Stroke(width = 5f)
                    )
                }

                drawIntoCanvas { canvas ->
                    canvas.drawPath(
                        currentPath,
                        Paint().apply {
                            color = brushColor
                            style = PaintingStyle.Stroke
                            strokeWidth = 5f
                        }
                    )
                }
                bitmap = ImageBitmap(size.width.toInt(), size.height.toInt()).also { bmp ->
                    val drawableCanvas = Canvas(bmp)

                    paths.forEach { strokePath ->
                        drawableCanvas.drawPath(strokePath.path, Paint().apply {
                            color = strokePath.color
                            style = PaintingStyle.Stroke
                            strokeWidth = 5f
                        })
                    }
                }
            }
        }

        // Buttons for saving and clearing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { paths = mutableListOf() }, // Clear canvas
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        bitmap?.let {
                            imageBitmapSaver(it)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save as PNG")
            }
        }
    }
}