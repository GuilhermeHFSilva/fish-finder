package com.example.fish_finder.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.ui.theme.AppBackground
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.BoundingBoxColor1
import com.example.fish_finder.ui.theme.BoundingBoxColor2
import com.example.fish_finder.ui.theme.BoundingBoxColor3
import com.example.fish_finder.ui.theme.BoundingBoxColor4
import com.example.fish_finder.ui.theme.MetricLabelStyle
import com.example.fish_finder.ui.theme.StatusPending
import com.example.fish_finder.ui.theme.StatusPendingBg
import com.example.fish_finder.ui.theme.StatusSuccess
import com.example.fish_finder.ui.theme.StatusSuccessBg
import com.example.fish_finder.ui.theme.TelemetryValueStyle
import com.example.fish_finder.ui.theme.TextMuted
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary
import java.util.Locale

// -------------------------------------------------------------------------
// BADGES E STATUS
// -------------------------------------------------------------------------

@Composable
fun StatusBadge(
    text: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSuccess) StatusSuccessBg else StatusPendingBg
    val contentColor = if (isSuccess) StatusSuccess else StatusPending

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(contentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )
        }
    }
}

@Composable
fun ChipDeviceBadge(
    label: String = "Local NPU",
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppPrimaryLight,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = AppIcons.Chip,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = AppPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// -------------------------------------------------------------------------
// CARDS DE RESUMO RÁPIDO (TELA 2)
// -------------------------------------------------------------------------

@Composable
fun QuickSummaryCards(
    totalObjects: Int,
    averageConfidence: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bloco 1: Total de objetos (Fundo Azul Primário)
        Card(
            modifier = Modifier
                .weight(1f)
                .height(105.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "OBJETOS DETECTADOS",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = totalObjects.toString(),
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Tilápias classificadas",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }

        // Bloco 2: Confiança média (Fundo Branco com borda suave)
        Card(
            modifier = Modifier
                .weight(1f)
                .height(105.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CONFIANÇA MÉDIA",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${(averageConfidence * 100).toInt()}%",
                    color = AppSecondary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Precisão ponderada",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// BANNER DE ESTATÍSTICAS GLOBAIS (TELA 3)
// -------------------------------------------------------------------------

@Composable
fun GlobalStatsBanner(
    totalInferences: Int,
    totalObjects: Int,
    averageTimeMs: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coluna 1: Total de inferências
            StatColumnItem(
                value = "$totalInferences",
                label = "INFERÊNCIAS",
                modifier = Modifier.weight(1f)
            )

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            // Coluna 2: Total de objetos
            StatColumnItem(
                value = "$totalObjects",
                label = "OBJETOS CONTADOS",
                modifier = Modifier.weight(1.2f)
            )

            // Divisor vertical
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            // Coluna 3: Média de tempo
            StatColumnItem(
                value = "$averageTimeMs ms",
                label = "MÉDIA TEMPO",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatColumnItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
    }
}

// -------------------------------------------------------------------------
// ITEM DO HISTÓRICO (TELA 3)
// -------------------------------------------------------------------------

@Composable
fun HistorySessionCard(
    session: InferenceResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Topo: ID da Sessão e Data/Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sessão #${session.sessionId}",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Text(
                    text = session.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meio: 3 mini-caixas com fundo cinza claro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniMetricTag(
                    label = "Modelo",
                    value = session.modelName,
                    modifier = Modifier.weight(1.1f)
                )
                MiniMetricTag(
                    label = "Total",
                    value = "${session.totalObjects} objs",
                    modifier = Modifier.weight(0.9f)
                )
                MiniMetricTag(
                    label = "Tempo",
                    value = "${session.executionTimeMs} ms",
                    modifier = Modifier.weight(0.9f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rodapé: Status de persistência na esquerda e botão ver detalhes na direita
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (session.isSyncedWithServer) {
                    StatusBadge(text = "✓ Enviado ao Servidor", isSuccess = true)
                } else {
                    StatusBadge(text = "Pendente Sincronização", isSuccess = false)
                }

                Text(
                    text = "Ver detalhes →",
                    color = AppPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun MiniMetricTag(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = label.uppercase(Locale.ROOT),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}

// -------------------------------------------------------------------------
// CARD DE TELEMETRIA GEOMÉTRICA (TELA 4)
// -------------------------------------------------------------------------

@Composable
fun BoundingBoxTelemetryCard(
    box: BoundingBox,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Cabeçalho do Card: Título da Caixa + Badge Azul de Confiança
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AppPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Caixa #${String.format(Locale.US, "%02d", box.boxId)} (${box.classLabel})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = AppPrimaryLight,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Confiança: ${(box.confidence * 100).toInt()}%",
                        color = AppPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linha de Dimensões: Duas Colunas (Largura e Altura)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = AppSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "LARGURA (W_px)",
                            style = MetricLabelStyle
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f px", box.widthPx),
                            style = TelemetryValueStyle
                        )
                    }
                }

                Surface(
                    color = AppSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ALTURA (H_px)",
                            style = MetricLabelStyle
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = String.format(Locale.US, "%.2f px", box.heightPx),
                            style = TelemetryValueStyle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Linha de Centróide e Área: Caixa destacada com fundo suave
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CENTRÓIDE (X_c, Y_c):",
                            style = MetricLabelStyle
                        )
                        Text(
                            text = String.format(Locale.US, "(%.2f, %.2f) px", box.centroidX, box.centroidY),
                            style = TelemetryValueStyle,
                            color = AppPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ÁREA (A_px):",
                            style = MetricLabelStyle
                        )
                        Text(
                            text = String.format(Locale.US, "%.2f px²", box.getAreaPx()),
                            style = TelemetryValueStyle,
                            color = AppSecondary
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// CANVAS DINÂMICO PARA BOUNDING BOXES (TELA 2)
// -------------------------------------------------------------------------

@Composable
fun AnnotatedImageCanvas(
    result: InferenceResult,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {}
) {
    val textMeasurer = rememberTextMeasurer()
    val boxPalette = listOf(BoundingBoxColor1, BoundingBoxColor2, BoundingBoxColor3, BoundingBoxColor4)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onImageClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Fundo escuro imersivo simulando tanque de água
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
                // Grade técnica de fundo e gradiente de água
                drawRect(color = Color(0xFF0B192C))

                val imgW = if (result.imageWidthPx > 0) result.imageWidthPx.toFloat() else 1920f
                val imgH = if (result.imageHeightPx > 0) result.imageHeightPx.toFloat() else 1080f

                val scaleX = canvasWidth / imgW
                val scaleY = canvasHeight / imgH

                // Desenho das Bounding Boxes
                result.boundingBoxes.forEachIndexed { index, box ->
                    val color = boxPalette[index % boxPalette.size]

                    val scaledX = box.xMin * scaleX
                    val scaledY = box.yMin * scaleY
                    val scaledW = box.widthPx * scaleX
                    val scaledH = box.heightPx * scaleY

                    // Retângulo delimitador
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(scaledX, scaledY),
                        size = Size(scaledW, scaledH),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // Marcador de centróide
                    val cX = box.centroidX * scaleX
                    val cY = box.centroidY * scaleY
                    drawCircle(
                        color = color,
                        radius = 3.dp.toPx(),
                        center = Offset(cX, cY)
                    )

                    // Tag com texto superior (#id classe conf%)
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

            // Badge flutuante indicando toque para expandir
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

// -------------------------------------------------------------------------
// DIALOG COM ZOOM E PAN (TELA 2)
// -------------------------------------------------------------------------

@Composable
fun ZoomableImageDialog(
    result: InferenceResult,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += panChange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Canvas com zoom/pan habilitado
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = transformState),
                    contentAlignment = Alignment.Center
                ) {
                    AnnotatedImageCanvas(
                        result = result,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onImageClick = { /* Já em modo tela cheia */ }
                    )
                }

                // Barra superior de controle com botão de fechar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Zoom: ${(scale * 100).toInt()}% • Pince para aproximar",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
