package com.example.fish_finder.ui.screens.detection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.ui.components.canvas.AnnotatedImageCanvas
import com.example.fish_finder.ui.components.cards.QuickSummaryCards
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSecondaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.TextPrimary

/**
 * Seção de apresentação dos resultados gráficos e métricos da inferência (Tela 2).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InferenceResultsSection(
    result: InferenceResult,
    onNewInference: () -> Unit,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Título da Seção + Botão de Nova Inferência
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Resultado da Inferência",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            TextButton(onClick = onNewInference) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Nova Inferência",
                    color = AppPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // 1. Cards de Resumo Rápido (Objetos e Confiança Média)
        QuickSummaryCards(
            totalObjects = result.totalObjects,
            averageConfidence = result.averageConfidence
        )

        // 2. Metadados de Execução e Tags de Classes Detectadas
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = BorderStroke(1.dp, AppCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tempo de execução: ${result.executionTimeMs} ms (Local NPU/CPU)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Surface(
                        color = AppPrimaryLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = result.modelName,
                            color = AppPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Tags com contagem agregada por classe
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    result.classCounts.forEach { (label, count) ->
                        Surface(
                            color = AppSecondaryLight,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "$label: $count",
                                color = AppSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Canvas da Imagem Anotada com caixas delimitadoras e centróides
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Imagem Anotada (Toque para expandir)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            AnnotatedImageCanvas(
                result = result,
                onImageClick = onImageClick
            )
        }
    }
}
