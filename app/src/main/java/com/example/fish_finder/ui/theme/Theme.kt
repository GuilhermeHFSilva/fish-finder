package com.example.fish_finder.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de cores Light Mode
private val LightColorScheme = lightColorScheme(
    primary = LightPalette.primary,
    onPrimary = LightPalette.surface,
    primaryContainer = LightPalette.primaryLight,
    onPrimaryContainer = LightPalette.primaryDark,
    secondary = LightPalette.secondary,
    onSecondary = LightPalette.surface,
    secondaryContainer = LightPalette.secondaryLight,
    onSecondaryContainer = LightPalette.secondary,
    background = LightPalette.background,
    onBackground = LightPalette.textPrimary,
    surface = LightPalette.surface,
    onSurface = LightPalette.textPrimary,
    surfaceVariant = LightPalette.surfaceVariant,
    onSurfaceVariant = LightPalette.textSecondary,
    outline = LightPalette.cardBorder
)

// Esquema de cores Dark Mode
private val DarkColorScheme = darkColorScheme(
    primary = DarkPalette.primary,
    onPrimary = DarkPalette.surface,
    primaryContainer = DarkPalette.primaryLight,
    onPrimaryContainer = DarkPalette.primaryDark,
    secondary = DarkPalette.secondary,
    onSecondary = DarkPalette.surface,
    secondaryContainer = DarkPalette.secondaryLight,
    onSecondaryContainer = DarkPalette.secondary,
    background = DarkPalette.background,
    onBackground = DarkPalette.textPrimary,
    surface = DarkPalette.surface,
    onSurface = DarkPalette.textPrimary,
    surfaceVariant = DarkPalette.surfaceVariant,
    onSurfaceVariant = DarkPalette.textSecondary,
    outline = DarkPalette.cardBorder
)

/**
 * Tema principal do FishFinder com suporte a Light e Dark Mode e bordas/barras adaptativas.
 */
@Composable
fun Fish_finderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkPalette else LightPalette
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = appColors.background.toArgb()
                window.navigationBarColor = appColors.surface.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}