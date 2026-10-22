package com.example.fish_finder.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import com.example.fish_finder.presentation.state.FishingUiAction
import com.example.fish_finder.presentation.state.FishingUiState
import com.example.fish_finder.presentation.state.InferenceUiAction
import com.example.fish_finder.presentation.state.InferenceUiState
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.screens.DetectionScreen
import com.example.fish_finder.ui.screens.HistoryScreen
import com.example.fish_finder.ui.screens.TelemetryDetailScreen
import com.example.fish_finder.ui.screens.fishing.AquariumScreen
import com.example.fish_finder.ui.screens.fishing.EncounterScreen
import com.example.fish_finder.ui.screens.fishing.FishingScreen
import com.example.fish_finder.ui.theme.AppBackground
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Destinos da barra de navegação inferior (Bottom Navigation).
 */
enum class AppDestination(val label: String, val icon: ImageVector) {
    FISH("Pescar", Icons.Default.Search),
    AQUARIUM("Aquário", Icons.Default.Star),
    DETECT("Detectar", AppIcons.Detect),
    HISTORY("Histórico", AppIcons.History)
}

/**
 * Ponto de entrada de navegação reativo com despacho unificado de ações (UDF / MVI).
 */
@Composable
fun AppNavigation(
    state: InferenceUiState,
    onAction: (InferenceUiAction) -> Unit,
    modifier: Modifier = Modifier,
    fishing: FishingUiState = FishingUiState(),
    onFishingAction: (FishingUiAction) -> Unit = {}
) {
    AppNavigation(
        state = state,
        fishing = fishing,
        onFishingAction = onFishingAction,
        onModelSelected = { onAction(InferenceUiAction.SelectModel(it)) },
        onCustomLabelsChanged = { onAction(InferenceUiAction.ChangeCustomLabels(it)) },
        onConfidenceThresholdChanged = { onAction(InferenceUiAction.ChangeConfidenceThreshold(it)) },
        onTakePhoto = { onAction(InferenceUiAction.TakePhoto) },
        onSelectGallery = { onAction(InferenceUiAction.SelectGallery) },
        onExecuteInference = { onAction(InferenceUiAction.ExecuteInference) },
        onNewInference = { onAction(InferenceUiAction.NewInference) },
        onSearchQueryChanged = { onAction(InferenceUiAction.SearchQuery(it)) },
        onSelectSessionForDetail = { onAction(InferenceUiAction.SelectSessionDetail(it)) },
        onClearSelectedSession = { onAction(InferenceUiAction.ClearSelectedSession) },
        onToggleExportInfoDialog = { onAction(InferenceUiAction.ToggleExportInfoDialog(it)) },
        onToggleZoomDialog = { onAction(InferenceUiAction.ToggleZoomDialog(it)) },
        modifier = modifier
    )
}

/**
 * Sobrecarga detalhada com callbacks individuais para máxima flexibilidade e compatibilidade.
 */
@Composable
fun AppNavigation(
    state: InferenceUiState,
    onModelSelected: (String) -> Unit,
    onCustomLabelsChanged: (String) -> Unit,
    onConfidenceThresholdChanged: (Float) -> Unit,
    onTakePhoto: () -> Unit,
    onSelectGallery: () -> Unit,
    onExecuteInference: () -> Unit,
    onNewInference: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectSessionForDetail: (String) -> Unit,
    onClearSelectedSession: () -> Unit,
    onToggleExportInfoDialog: (Boolean) -> Unit,
    onToggleZoomDialog: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    fishing: FishingUiState = FishingUiState(),
    onFishingAction: (FishingUiAction) -> Unit = {}
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.FISH) }

    // Peixe detectado: tela cheia, sem barra de navegação, até capturar ou deixar para lá
    fishing.currentEncounter?.let { encounter ->
        EncounterScreen(
            encounter = encounter,
            remaining = fishing.encounters.size,
            isThrowing = fishing.isThrowing,
            result = fishing.result,
            onThrow = { onFishingAction(FishingUiAction.ThrowNet(it)) },
            onNext = { onFishingAction(FishingUiAction.NextEncounter) },
            modifier = modifier
        )
        return
    }

    // Intercepta botão voltar do dispositivo se estiver no detalhe geométrico (Tela 4)
    if (currentDestination == AppDestination.HISTORY && state.selectedSessionForDetail != null) {
        BackHandler {
            onClearSelectedSession()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(
                containerColor = AppSurface,
                tonalElevation = 8.dp
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (destination == AppDestination.HISTORY && currentDestination == AppDestination.HISTORY) {
                                onClearSelectedSession()
                            }
                            currentDestination = destination
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppPrimary,
                            selectedTextColor = AppPrimary,
                            indicatorColor = AppPrimaryLight,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentDestination) {
            AppDestination.FISH -> FishingScreen(
                state = fishing,
                onImage = { onFishingAction(FishingUiAction.ImagePicked(it)) },
                modifier = Modifier.padding(innerPadding)
            )

            AppDestination.AQUARIUM -> AquariumScreen(
                collection = fishing.collection,
                modifier = Modifier.padding(innerPadding)
            )

            AppDestination.DETECT -> {
                DetectionScreen(
                    state = state,
                    onModelSelected = onModelSelected,
                    onCustomLabelsChanged = onCustomLabelsChanged,
                    onConfidenceThresholdChanged = onConfidenceThresholdChanged,
                    onTakePhoto = onTakePhoto,
                    onSelectGallery = onSelectGallery,
                    onExecuteInference = onExecuteInference,
                    onNewInference = onNewInference,
                    onToggleExportInfoDialog = onToggleExportInfoDialog,
                    onToggleZoomDialog = onToggleZoomDialog,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppDestination.HISTORY -> {
                val detailSession = state.selectedSessionForDetail
                if (detailSession != null) {
                    TelemetryDetailScreen(
                        session = detailSession,
                        onBack = onClearSelectedSession,
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    HistoryScreen(
                        state = state,
                        onSearchQueryChanged = onSearchQueryChanged,
                        onSelectSession = onSelectSessionForDetail,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
