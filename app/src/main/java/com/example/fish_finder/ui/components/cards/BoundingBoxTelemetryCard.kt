package com.example.fish_finder.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.MetricLabelStyle
import com.example.fish_finder.ui.theme.TelemetryValueStyle
import com.example.fish_finder.ui.theme.TextPrimary
import java.util.Locale

/**
 * Card de auditoria geométrica individual de uma caixa delimitadora (Tela 4 / RF5).
 */
@Composable
fun BoundingBoxTelemetryCard(
    box: BoundingBox,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurface),
        border = BorderStroke(1.dp, AppCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Cabeçalho: ID da Caixa e Rótulo da Classe + Badge de Confiança
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

            // Dimensões (Largura e Altura em px)
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

            // Centróide Geométrico (Xc, Yc) e Área (A_px)
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                            text = String.format(Locale.US, "%.2f px²", box.areaPx),
                            style = TelemetryValueStyle,
                            color = AppSecondary
                        )
                    }
                }
            }
        }
    }
}
