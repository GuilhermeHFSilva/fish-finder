package com.example.fish_finder.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.fish_finder.presentation.state.InferenceUiState
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.components.badges.ChipDeviceBadge
import com.example.fish_finder.ui.components.dialogs.ExportInfoDialog
import com.example.fish_finder.ui.components.dialogs.ZoomableImageDialog
import com.example.fish_finder.ui.screens.detection.ImageAcquisitionCard
import com.example.fish_finder.ui.screens.detection.ImagePreviewCard
import com.example.fish_finder.ui.screens.detection.InferenceResultsSection
import com.example.fish_finder.ui.screens.detection.ModelConfigCard
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary
import com.example.fish_finder.ui.utils.ImageUtils

/**
 * Tela principal de Detecção e Configuração (Tela 1 & Tela 2).
 * Orquestra a captura de imagem nativa via Câmera/Galeria, parametrização do modelo,
 * alternância de Modo Escuro e visualização dos resultados on-device.
 */
@Composable
fun DetectionScreen(
    state: InferenceUiState,
    onModelSelected: (String) -> Unit,
    onCustomLabelsChanged: (String) -> Unit,
    onConfidenceThresholdChanged: (Float) -> Unit,
    onTakePhoto: () -> Unit = {},
    onSelectGallery: () -> Unit = {},
    onImageSelected: (String) -> Unit = {},
    onExecuteInference: () -> Unit,
    onNewInference: () -> Unit,
    onToggleExportInfoDialog: (Boolean) -> Unit,
    onToggleZoomDialog: (Boolean) -> Unit,
    onToggleDarkMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para captura de foto em resolução total com a Câmera
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            onImageSelected(tempCameraUri.toString())
        }
    }

    // Launcher para solicitação de permissão de Câmera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val (_, uri) = ImageUtils.createCameraPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(
                context,
                "Permissão de câmera é necessária para capturar imagens diretamente.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Launcher para seleção de imagens da Galeria / Arquivos
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri ->
        if (selectedUri != null) {
            val persistedUri = ImageUtils.copyUriToInternalStorage(context, selectedUri)
            onImageSelected(persistedUri.toString())
        }
    }

    val triggerCamera = {
        onTakePhoto()
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val (_, uri) = ImageUtils.createCameraPhotoUri(context)
            tempCameraUri = uri
            takePictureLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val triggerGallery = {
        onSelectGallery()
        galleryLauncher.launch("image/*")
    }

    // Exibe toasts informativos do estado quando necessário
    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cabeçalho Principal com Alternador de Modo Escuro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Botão de alternância de Modo Escuro / Claro
                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .size(40.dp)
                        .background(AppSurfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.isDarkMode) AppIcons.LightMode else AppIcons.DarkMode,
                        contentDescription = if (state.isDarkMode) "Ativar Modo Claro" else "Ativar Modo Escuro",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                ChipDeviceBadge(label = "Local NPU/CPU")
            }
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
            isImageSelected = state.isImageSelected,
            onTakePhoto = triggerCamera,
            onSelectGallery = triggerGallery,
            onConfidenceThresholdChanged = onConfidenceThresholdChanged,
            onExecuteInference = onExecuteInference
        )

        // 5. Card de Pré-visualização do Frame
        ImagePreviewCard(
            isImageSelected = state.isImageSelected,
            imageUri = state.selectedImageUri
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
