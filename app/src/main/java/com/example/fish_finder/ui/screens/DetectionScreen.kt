package com.example.fish_finder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fish_finder.presentation.state.InferenceUiState
import com.example.fish_finder.ui.components.badges.ChipDeviceBadge
import com.example.fish_finder.ui.components.dialogs.ExportInfoDialog
import com.example.fish_finder.ui.components.dialogs.ZoomableImageDialog
import com.example.fish_finder.ui.screens.detection.ImageAcquisitionCard
import com.example.fish_finder.ui.screens.detection.ImagePreviewCard
import com.example.fish_finder.ui.screens.detection.InferenceResultsSection
import com.example.fish_finder.ui.screens.detection.ModelConfigCard
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Tela principal de Detecção e Configuração (Tela 1 & Tela 2).
 * Orquestra a captura de imagem, parametrização do modelo e visualização dos resultados.
 */
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
        // 1. Cabeçalho Principal
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

        // 2. Tela 2: Resultados da Inferência (Apresentado se houver inferência calculada)
        val currentResult = state.currentInference
        if (currentResult != null) {
            InferenceResultsSection(
                result = currentResult,
                onNewInference = onNewInference,
                onImageClick = { onToggleZoomDialog(true) }
            )
        }

        // 3. Card de Configuração do Modelo YOLO (ONNX)
        ModelConfigCard(
            selectedModel = state.selectedModel,
            customLabels = state.customLabels,
            onModelSelected = onModelSelected,
            onCustomLabelsChanged = onCustomLabelsChanged,
            onOpenExportInfo = { onToggleExportInfoDialog(true) }
        )

        // 4. Card de Aquisição de Imagem e Parâmetros
        ImageAcquisitionCard(
            confidenceThreshold = state.confidenceThreshold,
            isInferring = state.isInferring,
            onTakePhoto = onTakePhoto,
            onSelectGallery = onSelectGallery,
            onConfidenceThresholdChanged = onConfidenceThresholdChanged,
            onExecuteInference = onExecuteInference
        )

        // 5. Card de Pré-visualização do Frame
        ImagePreviewCard(
            isImageSelected = state.isImageSelected
        )
    }

    // Modais e Diálogos
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
