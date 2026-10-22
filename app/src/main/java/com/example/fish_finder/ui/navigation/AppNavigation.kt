package com.example.fish_finder.ui.navigation

import android.net.Uri
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
import com.example.fish_finder.presentation.state.FishingUiAction
import com.example.fish_finder.presentation.state.FishingUiState
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
    AQUARIUM("Aquário", Icons.Default.Star)
}

@Composable
fun AppNavigation(
    state: FishingUiState,
    onAction: (FishingUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.FISH) }

    // Peixe detectado: tela cheia, sem barra de navegação, até capturar ou deixar para lá
    state.currentEncounter?.let { encounter ->
        EncounterScreen(
            encounter = encounter,
            remaining = state.encounters.size,
            summary = state.summary,
            isThrowing = state.isThrowing,
            result = state.result,
            onThrow = { onAction(FishingUiAction.ThrowNet(it)) },
            onNext = { onAction(FishingUiAction.NextEncounter) },
            modifier = modifier
        )
        return
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
                        onClick = { currentDestination = destination },
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
                state = state,
                onImage = { uri: Uri -> onAction(FishingUiAction.ImagePicked(uri)) },
                modifier = Modifier.padding(innerPadding)
            )

            AppDestination.AQUARIUM -> AquariumScreen(
                collection = state.collection,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
