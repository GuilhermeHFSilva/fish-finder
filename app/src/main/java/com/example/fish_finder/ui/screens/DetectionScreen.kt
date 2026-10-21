package com.example.fish_finder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.presentation.viewmodel.InferenceUiState
import com.example.fish_finder.ui.components.AnnotatedImageCanvas
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.components.ChipDeviceBadge
import com.example.fish_finder.ui.components.QuickSummaryCards
import com.example.fish_finder.ui.components.StatusBadge
import com.example.fish_finder.ui.components.ZoomableImageDialog
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSecondaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.TextMuted
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetectionScreen(
    state: InferenceUiState,
    onModelSelected: (String) -> Unit,
    onCustomLabelsChanged: (String) -> Unit,
    onConfidenceThresholdChanged: (Float) -> Unit,
    onTakePhoto: () -> Unit,
    onSelectGallery: () -> Unit,
    onExecuteInference: () -> Unit,
    onNewInference: () -> Unit,
    onToggleExportInfoDialog: (Boolean) -> Unit,
    onToggleZoomDialog: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -----------------------------------------------------------------
        // 1. HEADER
        // -----------------------------------------------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FishFinder AI",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Inferência Offline On-Device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            ChipDeviceBadge(label = "Local NPU/CPU")
        }

        // -----------------------------------------------------------------
        // FLUXO DINÂMICO: SE RESULTADO EXISTE, MOSTRA TELA 2 (RESULTADOS)
        // COM OPÇÃO DE NOVA INFERÊNCIA / RECONFIGURAÇÃO
        // -----------------------------------------------------------------
        if (state.currentInference != null) {
            // TELA 2: Resultados e Visualização Anotada
            InferenceResultsSection(
                state = state,
                onNewInference = onNewInference,
                onImageClick = { onToggleZoomDialog(true) }
            )
        }

        // -----------------------------------------------------------------
        // 2. CARD 1: MODELO YOLO (ONNX)
        // -----------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cabeçalho do Card com ícone, título e badge ativo
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

                // Caixa de exibição mostrando o modelo atual selecionado
                Surface(
                    color = AppSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
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
                            text = state.selectedModel,
                            style = MaterialTheme.typography.titleSmall,
                            fontFamily = FontFamily.Monospace,
                            color = AppPrimary
                        )
                    }
                }

                // Linha com 2 botões lado a lado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val nextModel = if (state.selectedModel.contains("tilapia")) {
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
                        onClick = { onToggleExportInfoDialog(true) },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppPrimary),
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

                // OutlinedTextField para rótulos personalizados
                OutlinedTextField(
                    value = state.customLabels,
                    onValueChange = onCustomLabelsChanged,
                    label = { Text("Rótulos personalizados (separados por vírgula)") },
                    placeholder = { Text("ex: tilapia, alevino, peixe") },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // -----------------------------------------------------------------
        // 3. CARD 2: CAPTURAR OU SELECIONAR IMAGEM
        // -----------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
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

                // Linha com 2 botões: Tirar Foto e Galeria
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

                // Controle de Limiar de Confiança (Confidence Threshold)
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
                                text = "${(state.confidenceThreshold * 100).toInt()}%",
                                color = AppSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Slider(
                        value = state.confidenceThreshold,
                        onValueChange = onConfidenceThresholdChanged,
                        valueRange = 0.05f..0.95f,
                        colors = SliderDefaults.colors(
                            thumbColor = AppPrimary,
                            activeTrackColor = AppPrimary,
                            inactiveTrackColor = AppCardBorder
                        )
                    )
                }

                // Botão de ação principal em destaque (largura total)
                Button(
                    onClick = onExecuteInference,
                    enabled = !state.isInferring,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppPrimary,
                        disabledContainerColor = AppPrimary.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (state.isInferring) {
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

        // -----------------------------------------------------------------
        // 4. CARD 3: PRÉ-VISUALIZAÇÃO DA IMAGEM
        // -----------------------------------------------------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Pré-visualização da Imagem de Entrada",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, AppCardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isImageSelected) {
                        // Visualização técnica estilizada do frame do tanque
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.Fish,
                                contentDescription = null,
                                tint = AppPrimaryLight,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Frame carregado: Tanque Escavado #01",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Resolução: 1920 × 1080 px • Formato RGB888",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.Gallery,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Nenhuma imagem selecionada",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // DIALOGS: ZOOM DA IMAGEM E COMO EXPORTAR ONNX
    // ---------------------------------------------------------------------
    if (state.isZoomDialogOpen && state.currentInference != null) {
        ZoomableImageDialog(
            result = state.currentInference,
            onDismiss = { onToggleZoomDialog(false) }
        )
    }

    if (state.isExportInfoDialogOpen) {
        ExportInfoDialog(onDismiss = { onToggleExportInfoDialog(false) })
    }
}

// -------------------------------------------------------------------------
// SEÇÃO DE RESULTADOS (TELA 2)
// -------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InferenceResultsSection(
    state: InferenceUiState,
    onNewInference: () -> Unit,
    onImageClick: () -> Unit
) {
    val result = state.currentInference ?: return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Título da Seção + Botão Nova Inferência
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

        // 1. Cards de Resumo Rápido (Lado a Lado)
        QuickSummaryCards(
            totalObjects = result.totalObjects,
            averageConfidence = result.averageConfidence
        )

        // 2. Metadados de Execução e Chips de Classe
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder)
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

                // Tags de contagem por classe
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

        // 3. Card da Imagem Anotada com Canvas
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

// -------------------------------------------------------------------------
// DIALOG EXPLICATIVO: COMO EXPORTAR PARA ONNX
// -------------------------------------------------------------------------

@Composable
fun ExportInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = AppPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Exportação de Modelo ONNX",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Para converter seu modelo YOLO treinado (.pt) em formato ONNX de alta performance on-device, execute:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "yolo export model=tilapia.pt format=onnx imgsz=640 simplify=True",
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Text(
                    text = "Em seguida, coloque o arquivo tilapia.onnx no diretório de assets do Android.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
            ) {
                Text("Entendido")
            }
        }
    )
}
