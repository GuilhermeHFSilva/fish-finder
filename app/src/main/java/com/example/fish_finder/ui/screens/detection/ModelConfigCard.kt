package com.example.fish_finder.ui.screens.detection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.components.badges.StatusBadge
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Card para parametrização e seleção do modelo YOLO/ONNX (RF1 / Tela 1).
 */
@Composable
fun ModelConfigCard(
    selectedModel: String,
    customLabels: String,
    onModelSelected: (String) -> Unit,
    onCustomLabelsChanged: (String) -> Unit,
    onOpenExportInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            // Cabeçalho do Card com ícone, título e badge de modelo ativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.Chip,
                            contentDescription = null,
                            tint = AppPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Modelo YOLO (ONNX)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                }

                StatusBadge(text = "✓ Ativo", isSuccess = true)
            }

            // Caixa com o nome do modelo carregado
            Surface(
                color = AppSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AppCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Modelo:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedModel,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Monospace,
                        color = AppPrimary
                    )
                }
            }

            // Ações: Alternar modelo e Guia de Exportação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val nextModel = if (selectedModel.contains("tilapia")) {
                            "best.onnx (Modelo Embutido)"
                        } else {
                            "tilapia.onnx (Modelo Embutido)"
                        }
                        onModelSelected(nextModel)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Selecionar modelo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                OutlinedButton(
                    onClick = onOpenExportInfo,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AppPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Como exportar",
                        color = AppPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Campo de rótulos/classes customizadas
            OutlinedTextField(
                value = customLabels,
                onValueChange = onCustomLabelsChanged,
                label = { Text("Rótulos personalizados (separados por vírgula)") },
                placeholder = { Text("ex: tilapia, alevino, peixe") },
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
