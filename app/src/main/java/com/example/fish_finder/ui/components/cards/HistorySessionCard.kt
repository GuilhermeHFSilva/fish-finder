package com.example.fish_finder.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.ui.components.badges.StatusBadge
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.TextMuted
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary
import java.util.Locale

/**
 * Card individual representando um registro cronológico de inferência na lista (Tela 3).
 */
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
        border = BorderStroke(1.dp, AppCardBorder),
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

            // Linha métrica central: 3 tags informativas
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

            // Rodapé: Status de persistência e link de navegação
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
