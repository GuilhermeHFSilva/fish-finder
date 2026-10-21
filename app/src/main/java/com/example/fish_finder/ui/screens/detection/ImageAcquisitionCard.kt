package com.example.fish_finder.ui.screens.detection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSecondaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Card para captura de imagem (Câmera/Galeria), ajuste dinâmico do limiar de confiança
 * e gatilho de execução da inferência local (RF2 / RF3 / RF4).
 */
@Composable
fun ImageAcquisitionCard(
    confidenceThreshold: Float,
    isInferring: Boolean,
    onTakePhoto: () -> Unit,
    onSelectGallery: () -> Unit,
    onConfidenceThresholdChanged: (Float) -> Unit,
    onExecuteInference: () -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Capturar ou Selecionar Imagem",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )

            // Botões de Entrada: Câmera ou Galeria
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onTakePhoto,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = AppIcons.Camera,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Tirar Foto", color = TextPrimary, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onSelectGallery,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = AppIcons.Gallery,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Galeria", color = TextPrimary, fontSize = 13.sp)
                }
            }

            // Slider interativo de Limiar de Confiança (Confidence Threshold)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Limiar de Confiança (IoU / Confidence)",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Surface(
                        color = AppSecondaryLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${(confidenceThreshold * 100).toInt()}%",
                            color = AppSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Slider(
                    value = confidenceThreshold,
                    onValueChange = onConfidenceThresholdChanged,
                    valueRange = 0.05f..0.95f,
                    colors = SliderDefaults.colors(
                        thumbColor = AppPrimary,
                        activeTrackColor = AppPrimary,
                        inactiveTrackColor = AppCardBorder
                    )
                )
            }

            // Botão Principal de Execução da Inferência
            Button(
                onClick = onExecuteInference,
                enabled = !isInferring,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppPrimary,
                    disabledContainerColor = AppPrimary.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isInferring) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Processando ONNX na NPU...",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = AppIcons.Play,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "▶ Executar inferência",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
