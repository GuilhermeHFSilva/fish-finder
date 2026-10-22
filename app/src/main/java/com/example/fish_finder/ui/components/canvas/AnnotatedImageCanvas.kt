package com.example.fish_finder.ui.components.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.theme.BoundingBoxColor1
import com.example.fish_finder.ui.theme.BoundingBoxColor2
import com.example.fish_finder.ui.theme.BoundingBoxColor3
import com.example.fish_finder.ui.theme.BoundingBoxColor4
import com.example.fish_finder.ui.utils.ImageUtils

private val BOX_PALETTE = listOf(
    BoundingBoxColor1,
    BoundingBoxColor2,
    BoundingBoxColor3,
    BoundingBoxColor4
)

/**
 * Canvas customizado para sobreposição gráfica e renderização vetorizada
 * de caixas delimitadoras e centróides sobre a imagem real de captura (Tela 2 / RF5).
 */
@Composable
fun AnnotatedImageCanvas(
    result: InferenceResult,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val textMeasurer = rememberTextMeasurer()

    val bitmap = remember(result.imageUri) {
        if (!result.imageUri.isNullOrBlank()) {
            ImageUtils.loadBitmapFromUri(context, result.imageUri)
        } else {
            null
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onImageClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            val canvasWidth = constraints.maxWidth.toFloat()
            val canvasHeight = constraints.maxHeight.toFloat()

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (bitmap != null) {
                    val srcSize = IntSize(bitmap.width, bitmap.height)
                    val dstSize = IntSize(size.width.toInt(), size.height.toInt())
                    drawImage(
                        image = bitmap.asImageBitmap(),
                        srcOffset = IntOffset.Zero,
                        srcSize = srcSize,
                        dstOffset = IntOffset.Zero,
                        dstSize = dstSize
                    )
                } else {
                    drawRect(color = Color(0xFF0B192C))
                }

                val imgW = if (bitmap != null) {
                    bitmap.width.toFloat()
                } else if (result.imageWidthPx > 0) {
                    result.imageWidthPx.toFloat()
                } else {
                    1920f
                }

                val imgH = if (bitmap != null) {
                    bitmap.height.toFloat()
                } else if (result.imageHeightPx > 0) {
                    result.imageHeightPx.toFloat()
                } else {
                    1080f
                }

                val scaleX = canvasWidth / imgW
                val scaleY = canvasHeight / imgH

                // Renderiza cada Bounding Box proporcionalmente
                result.boundingBoxes.forEachIndexed { index, box ->
                    val color = BOX_PALETTE[index % BOX_PALETTE.size]

                    val scaledX = box.xMin * scaleX
                    val scaledY = box.yMin * scaleY
                    val scaledW = box.widthPx * scaleX
                    val scaledH = box.heightPx * scaleY

                    // 1. Retângulo da caixa delimitadora
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(scaledX, scaledY),
                        size = Size(scaledW, scaledH),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // 2. Centróide geométrico
                    val cX = box.centroidX * scaleX
                    val cY = box.centroidY * scaleY
                    drawCircle(
                        color = color,
                        radius = 3.dp.toPx(),
                        center = Offset(cX, cY)
                    )

                    // 3. Etiqueta informativa (#ID classe conf%)
                    val tagText = "#${box.boxId} ${box.classLabel} ${(box.confidence * 100).toInt()}%"
                    val textLayout = textMeasurer.measure(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)) {
                                append(tagText)
                            }
                        }
                    )

                    val tagHeight = textLayout.size.height + 6.dp.toPx()
                    val tagWidth = textLayout.size.width + 10.dp.toPx()
                    val tagY = (scaledY - tagHeight).coerceAtLeast(2f)

                    // Fundo da etiqueta
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(scaledX, tagY),
                        size = Size(tagWidth, tagHeight),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )

                    // Texto da etiqueta
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(scaledX + 5.dp.toPx(), tagY + 3.dp.toPx())
                    )
                }
            }

            // Indicador de toque para tela cheia / zoom
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.ZoomIn,
                        contentDescription = "Expandir",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Toque para expandir",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
