package com.example.fish_finder.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.presentation.viewmodel.InferenceUiState
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.screens.DetectionScreen
import com.example.fish_finder.ui.screens.HistoryScreen
import com.example.fish_finder.ui.screens.TelemetryDetailScreen
import com.example.fish_finder.ui.theme.AppBackground
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Destinos da NavigationBar principal.
 */
enum class AppDestination(val label: String, val icon: ImageVector) {
    DETECT("Detectar", AppIcons.Detect),
    HISTORY("Histórico", AppIcons.History)
}

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
    modifier: Modifier = Modifier
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.DETECT) }

    // Intercepta botão voltar nativo do Android se estiver na Tela 4
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
                                // Se já estiver no histórico e tocar novamente, volta para a lista principal
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
                    // TELA 4: Detalhamento Geométrico das Caixas
                    TelemetryDetailScreen(
                        session = detailSession,
                        onBack = onClearSelectedSession,
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    // TELA 3: Histórico de Inferências
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
