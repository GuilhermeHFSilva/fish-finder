package com.example.fish_finder.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.fish_finder.ui.components.AppIcons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.ui.components.canvas.AnnotatedImageCanvas
import com.example.fish_finder.ui.components.cards.BoundingBoxTelemetryCard
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.StatusSuccess
import com.example.fish_finder.ui.theme.TextMuted
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Tela 4: Detalhamento Geométrico das Caixas Delimitadoras (RF5 / RF8).
 * Realiza a auditoria individual de coordenadas, largura, altura, centróide e área.
 */
@Composable
fun TelemetryDetailScreen(
    session: InferenceResult,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Barra superior de navegação com botão voltar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppSurface)
                        .border(1.dp, AppCardBorder, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = "Voltar",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Detecção #${session.sessionId}",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = session.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Card de Resumo de Telemetria da Sessão (Grid 2x2)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                border = BorderStroke(1.dp, AppCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SessionMetricCell(
                            label = "MODELO",
                            value = session.modelName,
                            valueColor = AppPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        SessionMetricCell(
                            label = "TOTAL CAIXAS",
                            value = "${session.totalObjects} caixas",
                            valueColor = AppSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SessionMetricCell(
                            label = "TEMPO INFERÊNCIA",
                            value = "${session.executionTimeMs} ms",
                            valueColor = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        SessionMetricCell(
                            label = "PERSISTÊNCIA",
                            value = if (session.isSyncedWithServer) "✓ BD Servidor" else "Pendente",
                            valueColor = if (session.isSyncedWithServer) StatusSuccess else Color(0xFFE65100),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2.5 Visualização Gráfica Anotada
        if (session.boundingBoxes.isNotEmpty() || !session.imageUri.isNullOrBlank()) {
            item {
                AnnotatedImageCanvas(result = session)
            }
        }

        // 3. Cabeçalho da Lista de Caixas
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Telemetria das Caixas Delimitadoras",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "Exibindo ${session.boundingBoxes.size} de ${session.totalObjects}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 4. Lista de Cartões de Auditoria Geométrica
        if (session.boundingBoxes.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nenhuma caixa delimitadora registrada nesta sessão.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(
                items = session.boundingBoxes,
                key = { it.boxId }
            ) { box ->
                BoundingBoxTelemetryCard(box = box)
            }
        }
    }
}

@Composable
private fun SessionMetricCell(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = valueColor
            )
        }
    }
}
