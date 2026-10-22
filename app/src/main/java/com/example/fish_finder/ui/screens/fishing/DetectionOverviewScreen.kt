package com.example.fish_finder.ui.screens.fishing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.presentation.state.DetectionOverview
import com.example.fish_finder.presentation.state.ScanSummary
import com.example.fish_finder.ui.components.fishing.NetIcon
import com.example.fish_finder.ui.components.fishing.OceanBackground
import com.example.fish_finder.ui.theme.BoundingBoxColor1
import com.example.fish_finder.ui.theme.BoundingBoxColor2
import com.example.fish_finder.ui.theme.BoundingBoxColor3
import com.example.fish_finder.ui.theme.BoundingBoxColor4
import com.example.fish_finder.ui.theme.GoldAccent
import com.example.fish_finder.ui.theme.OceanDeep

private val BOX_PALETTE = listOf(
    BoundingBoxColor1, // Azul
    BoundingBoxColor2, // Verde-água
    BoundingBoxColor3, // Laranja
    BoundingBoxColor4, // Roxo
    GoldAccent         // Dourado
)

private fun colorForBox(boxId: Int): Color =
    BOX_PALETTE[(boxId - 1).coerceAtLeast(0) % BOX_PALETTE.size]

/**
 * Tela cheia que exibe a imagem completa com as caixas delimitadoras (bounding boxes)
 * detectadas pela IA antes de o jogador iniciar a captura individual dos peixes.
 */
@Composable
fun DetectionOverviewScreen(
    overview: DetectionOverview,
    summary: ScanSummary?,
    onStartEncounters: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onDismiss)

    val imageBitmap = remember(overview.image) { overview.image.asImageBitmap() }
    val textMeasurer = rememberTextMeasurer()

    var selectedBoxId by remember { mutableStateOf<Int?>(null) }
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    OceanBackground(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabeçalho da tela
            Text(
                text = "Detecções da IA",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Veja onde o modelo detectou os peixes na imagem",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            // Badges informativas (quantidade de detecções e confiança média)
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${overview.boxes.size} ${if (overview.boxes.size == 1) "peixe encontrado" else "peixes encontrados"}",
                    color = OceanDeep,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(GoldAccent, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                summary?.let {
                    Text(
                        text = "confiança média ${it.formattedConfidence}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(50))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Área central com a imagem preservando o aspect ratio e com caixas delimitadoras
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val availableWidth = constraints.maxWidth.toFloat()
                val availableHeight = constraints.maxHeight.toFloat()
                val imgWidth = overview.image.width.toFloat()
                val imgHeight = overview.image.height.toFloat()

                val imageAspect = imgWidth / imgHeight
                val containerAspect = availableWidth / availableHeight

                val (contentWidthPx, contentHeightPx) = if (imageAspect > containerAspect) {
                    availableWidth to (availableWidth / imageAspect)
                } else {
                    (availableHeight * imageAspect) to availableHeight
                }

                val density = LocalDensity.current
                val contentWidthDp = with(density) { contentWidthPx.toDp() }
                val contentHeightDp = with(density) { contentHeightPx.toDp() }
                val scale = contentWidthPx / imgWidth

                Box(
                    modifier = Modifier
                        .size(contentWidthDp, contentHeightDp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(OceanDeep)
                        .border(1.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .clipToBounds()
                        .pointerInput(overview.boxes, contentWidthPx, contentHeightPx) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                zoomScale = (zoomScale * zoom).coerceIn(1f, 4f)
                                if (zoomScale > 1f) {
                                    val maxPanX = (contentWidthPx * (zoomScale - 1f)) / 2f
                                    val maxPanY = (contentHeightPx * (zoomScale - 1f)) / 2f
                                    panOffset = Offset(
                                        x = (panOffset.x + pan.x).coerceIn(-maxPanX, maxPanX),
                                        y = (panOffset.y + pan.y).coerceIn(-maxPanY, maxPanY)
                                    )
                                } else {
                                    panOffset = Offset.Zero
                                }
                            }
                        }
                        .pointerInput(overview.boxes, scale) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (zoomScale > 1f) {
                                        zoomScale = 1f
                                        panOffset = Offset.Zero
                                    } else {
                                        zoomScale = 2f
                                    }
                                },
                                onTap = { tapOffset ->
                                    val unscaledX = tapOffset.x / scale
                                    val unscaledY = tapOffset.y / scale
                                    val tappedBox = overview.boxes.firstOrNull { box ->
                                        unscaledX in box.xMin..(box.xMin + box.widthPx) &&
                                                unscaledY in box.yMin..(box.yMin + box.heightPx)
                                    }
                                    selectedBoxId = if (selectedBoxId == tappedBox?.boxId) null else tappedBox?.boxId
                                }
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoomScale
                                scaleY = zoomScale
                                translationX = panOffset.x
                                translationY = panOffset.y
                            }
                    ) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "Imagem original com detecções",
                            modifier = Modifier.fillMaxSize()
                        )

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            overview.boxes.forEach { box ->
                                val color = colorForBox(box.boxId)
                                val isSelected = (box.boxId == selectedBoxId)

                                val sx = box.xMin * scale
                                val sy = box.yMin * scale
                                val sw = box.widthPx * scale
                                val sh = box.heightPx * scale

                                // Fundo semi-transparente da caixa
                                drawRoundRect(
                                    color = color.copy(alpha = if (isSelected) 0.32f else 0.15f),
                                    topLeft = Offset(sx, sy),
                                    size = Size(sw, sh),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )

                                // Contorno da caixa delimitadora
                                drawRoundRect(
                                    color = color,
                                    topLeft = Offset(sx, sy),
                                    size = Size(sw, sh),
                                    cornerRadius = CornerRadius(4.dp.toPx()),
                                    style = Stroke(width = if (isSelected) 3.5.dp.toPx() else 2.2.dp.toPx())
                                )

                                // Cantoneiras estilo HUD nos 4 vértices
                                val bracketLen = (minOf(sw, sh) * 0.25f).coerceIn(8.dp.toPx(), 22.dp.toPx())
                                val bracketStroke = (if (isSelected) 4.dp else 2.8.dp).toPx()
                                // Top-Left
                                drawLine(color, Offset(sx, sy), Offset(sx + bracketLen, sy), bracketStroke)
                                drawLine(color, Offset(sx, sy), Offset(sx, sy + bracketLen), bracketStroke)
                                // Top-Right
                                drawLine(color, Offset(sx + sw, sy), Offset(sx + sw - bracketLen, sy), bracketStroke)
                                drawLine(color, Offset(sx + sw, sy), Offset(sx + sw, sy + bracketLen), bracketStroke)
                                // Bottom-Left
                                drawLine(color, Offset(sx, sy + sh), Offset(sx + bracketLen, sy + sh), bracketStroke)
                                drawLine(color, Offset(sx, sy + sh), Offset(sx, sy + sh - bracketLen), bracketStroke)
                                // Bottom-Right
                                drawLine(color, Offset(sx + sw, sy + sh), Offset(sx + sw - bracketLen, sy + sh), bracketStroke)
                                drawLine(color, Offset(sx + sw, sy + sh), Offset(sx + sw, sy + sh - bracketLen), bracketStroke)

                                // Centróide geométrico
                                val cx = box.centroidX * scale
                                val cy = box.centroidY * scale
                                drawCircle(
                                    color = color,
                                    radius = if (isSelected) 5.dp.toPx() else 3.5.dp.toPx(),
                                    center = Offset(cx, cy)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 1.5.dp.toPx(),
                                    center = Offset(cx, cy)
                                )

                                // Etiqueta identificadora (#ID Classe Conf%)
                                val labelText = "#${box.boxId} ${box.classLabel.replaceFirstChar { it.uppercase() }} ${(box.confidence * 100).toInt()}%"
                                val textLayout = textMeasurer.measure(
                                    text = buildAnnotatedString {
                                        withStyle(SpanStyle(color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)) {
                                            append(labelText)
                                        }
                                    }
                                )

                                val tagPadH = 6.dp.toPx()
                                val tagPadV = 3.dp.toPx()
                                val tagWidth = textLayout.size.width + (tagPadH * 2)
                                val tagHeight = textLayout.size.height + (tagPadV * 2)

                                val tagY = if (sy - tagHeight >= 2.dp.toPx()) {
                                    sy - tagHeight - 2.dp.toPx()
                                } else {
                                    sy + 2.dp.toPx()
                                }

                                drawRoundRect(
                                    color = color,
                                    topLeft = Offset(sx, tagY),
                                    size = Size(tagWidth, tagHeight),
                                    cornerRadius = CornerRadius(3.dp.toPx())
                                )

                                drawText(
                                    textLayoutResult = textLayout,
                                    topLeft = Offset(sx + tagPadH, tagY + tagPadV)
                                )
                            }
                        }
                    }

                    // Indicador de zoom ativo com botão de redefinir
                    if (zoomScale > 1f) {
                        Text(
                            text = "Zoom ${(zoomScale * 10).toInt() / 10f}x · Toque duplo para redefinir",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                .clickable {
                                    zoomScale = 1f
                                    panOffset = Offset.Zero
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Carrossel horizontal de seleção rápida dos peixes detectados
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                overview.boxes.forEach { box ->
                    val color = colorForBox(box.boxId)
                    val isSelected = (box.boxId == selectedBoxId)

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) color.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.12f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) color else Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedBoxId = if (selectedBoxId == box.boxId) null else box.boxId
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "#${box.boxId}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${(box.confidence * 100).toInt()}%",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Detalhes da caixa selecionada (se houver)
            AnimatedVisibility(
                visible = selectedBoxId != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val selected = overview.boxes.firstOrNull { it.boxId == selectedBoxId }
                if (selected != null) {
                    Text(
                        text = "Peixe #${selected.boxId}: ${selected.formattedDimensions} · Área ${selected.formattedArea}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Botão principal: Iniciar a pescaria dos peixes detectados
            Button(
                onClick = onStartEncounters,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldAccent,
                    contentColor = OceanDeep
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NetIcon(color = OceanDeep, size = 22.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Começar pescaria (${minOf(overview.boxes.size, 10)})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                }
            }

            // Ação secundária: descartar e escolher outra foto
            Text(
                text = "Tirar outra foto",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(onClick = onDismiss)
            )
        }
    }
}
