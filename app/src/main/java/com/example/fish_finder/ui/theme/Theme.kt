package com.example.fish_finder.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de cores exclusivo Light Mode
private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppSurface,
    primaryContainer = AppPrimaryLight,
    onPrimaryContainer = AppPrimaryDark,
    secondary = AppSecondary,
    onSecondary = AppSurface,
    secondaryContainer = AppSecondaryLight,
    onSecondaryContainer = AppSecondary,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AppCardBorder
)

/**
 * Tema principal do FishFinder configurado estritamente para Light Mode.
 * Por especificação técnica, darkTheme é fixo em falso e dynamicColor é desabilitado
 * para preservar a identidade visual técnica e científica do aplicativo.
 */
@Composable
fun Fish_finderTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AppBackground.toArgb()
                window.navigationBarColor = AppSurface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = true
                insetsController.isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}