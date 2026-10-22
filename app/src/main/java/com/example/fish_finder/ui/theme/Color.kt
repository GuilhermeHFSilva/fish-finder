package com.example.fish_finder.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Esquema de cores completo para Light e Dark Mode no FishFinder.
 */
data class AppColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val primaryLight: Color,
    val primaryDark: Color,
    val secondary: Color,
    val secondaryLight: Color,
    val statusSuccess: Color,
    val statusSuccessBg: Color,
    val statusPending: Color,
    val statusPendingBg: Color,
    val isDark: Boolean
)

val LightPalette = AppColorScheme(
    background = Color(0xFFF4F7FB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF1F5F9),
    cardBorder = Color(0xFFE2E8F0),
    textPrimary = Color(0xFF1E293B),
    textSecondary = Color(0xFF64748B),
    textMuted = Color(0xFF94A3B8),
    primary = Color(0xFF0288D1),
    primaryLight = Color(0xFFE1F5FE),
    primaryDark = Color(0xFF01579B),
    secondary = Color(0xFF00897B),
    secondaryLight = Color(0xFFE0F2F1),
    statusSuccess = Color(0xFF2E7D32),
    statusSuccessBg = Color(0xFFE8F5E9),
    statusPending = Color(0xFFE65100),
    statusPendingBg = Color(0xFFFFF3E0),
    isDark = false
)

val DarkPalette = AppColorScheme(
    background = Color(0xFF0B132B),
    surface = Color(0xFF1C2541),
    surfaceVariant = Color(0xFF2E3A59),
    cardBorder = Color(0xFF3A4B70),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    primary = Color(0xFF38BDF8),
    primaryLight = Color(0xFF0C4A6E),
    primaryDark = Color(0xFF0284C7),
    secondary = Color(0xFF2DD4BF),
    secondaryLight = Color(0xFF134E4A),
    statusSuccess = Color(0xFF4ADE80),
    statusSuccessBg = Color(0xFF064E3B),
    statusPending = Color(0xFFFB923C),
    statusPendingBg = Color(0xFF7C2D12),
    isDark = true
)

val LocalAppColors = staticCompositionLocalOf { LightPalette }

// Propriedades acessíveis de forma transparente por Composables em Light ou Dark Mode
val AppBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.background

val AppSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surface

val AppCardBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.cardBorder

val AppSurfaceVariant: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.surfaceVariant

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textPrimary

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textSecondary

val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.textMuted

val AppPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.primary

val AppPrimaryLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.primaryLight

val AppPrimaryDark: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.primaryDark

val AppSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.secondary

val AppSecondaryLight: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.secondaryLight

val StatusSuccess: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.statusSuccess

val StatusSuccessBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.statusSuccessBg

val StatusPending: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.statusPending

val StatusPendingBg: Color
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current.statusPendingBg

// Cores para visualização técnica de Bounding Boxes
val BoundingBoxColor1 = Color(0xFF0288D1) // Azul
val BoundingBoxColor2 = Color(0xFF00897B) // Verde-água
val BoundingBoxColor3 = Color(0xFFFF6D00) // Laranja vibrante
val BoundingBoxColor4 = Color(0xFF7C4DFF) // Roxo destaque