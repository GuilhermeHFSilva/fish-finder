package com.example.fish_finder.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Ícones vetoriais autocontidos e customizados para o FishFinder.
 * Garante independência de bibliotecas extras mantendo alta fidelidade visual técnica.
 */
object AppIcons {

    // Ícone de Alvo / Visão Computacional para o destino "Detectar"
    val Detect: ImageVector by lazy {
        ImageVector.Builder(
            name = "Detect",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Alvo externo
                moveTo(12f, 2f); lineTo(12f, 6f)
                moveTo(12f, 18f); lineTo(12f, 22f)
                moveTo(2f, 12f); lineTo(6f, 12f)
                moveTo(18f, 12f); lineTo(22f, 12f)
            }
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f
            ) {
                // Círculo concêntrico
                moveTo(19f, 12f)
                arcTo(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5f, 12f)
                arcTo(7f, 7f, 0f, isMoreThanHalf = true, isPositiveArc = true, 19f, 12f)
            }
            path(fill = SolidColor(Color.Black)) {
                // Ponto central
                moveTo(13.5f, 12f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 10.5f, 12f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 13.5f, 12f)
            }
        }.build()
    }

    // Ícone de Histórico / Relógio
    val History: ImageVector by lazy {
        ImageVector.Builder(
            name = "History",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Relógio
                moveTo(21f, 12f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 12f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 21f, 12f)
                // Ponteiros
                moveTo(12f, 7f); lineTo(12f, 12f); lineTo(15.5f, 14f)
            }
        }.build()
    }

    // Ícone de Chip / Hardware Local (On-Device NPU/CPU)
    val Chip: ImageVector by lazy {
        ImageVector.Builder(
            name = "Chip",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Corpo do chip
                moveTo(6f, 4f)
                lineTo(18f, 4f)
                lineTo(18f, 20f)
                lineTo(6f, 20f)
                close()
                // Núcleo interno
                moveTo(9f, 8f); lineTo(15f, 8f); lineTo(15f, 16f); lineTo(9f, 16f); close()
                // Pinos laterais
                moveTo(1f, 9f); lineTo(4f, 9f)
                moveTo(1f, 15f); lineTo(4f, 15f)
                moveTo(20f, 9f); lineTo(23f, 9f)
                moveTo(20f, 15f); lineTo(23f, 15f)
                moveTo(9f, 1f); lineTo(9f, 4f)
                moveTo(15f, 1f); lineTo(15f, 4f)
                moveTo(9f, 20f); lineTo(9f, 23f)
                moveTo(15f, 20f); lineTo(15f, 23f)
            }
        }.build()
    }

    // Ícone de Câmera
    val Camera: ImageVector by lazy {
        ImageVector.Builder(
            name = "Camera",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(23f, 19f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 21f)
                lineTo(3f, 21f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 19f)
                lineTo(1f, 8f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 6f)
                lineTo(7f, 6f)
                lineTo(9f, 3f)
                lineTo(15f, 3f)
                lineTo(17f, 6f)
                lineTo(21f, 6f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 23f, 8f)
                close()
                // Lente
                moveTo(16f, 13f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8f, 13f)
                arcTo(4f, 4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 16f, 13f)
            }
        }.build()
    }

    // Ícone de Galeria
    val Gallery: ImageVector by lazy {
        ImageVector.Builder(
            name = "Gallery",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Moldura externa
                moveTo(3f, 3f); lineTo(21f, 3f); lineTo(21f, 19f); lineTo(3f, 19f); close()
                // Montanhas / Imagem
                moveTo(3f, 15f); lineTo(8f, 10f); lineTo(14f, 16f); lineTo(17f, 13f); lineTo(21f, 17f)
                // Sol
                moveTo(8.5f, 7.5f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5.5f, 7.5f)
                arcTo(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 8.5f, 7.5f)
            }
        }.build()
    }

    // Ícone de Play / Executar
    val Play: ImageVector by lazy {
        ImageVector.Builder(
            name = "Play",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(8f, 5f)
                lineTo(19f, 12f)
                lineTo(8f, 19f)
                close()
            }
        }.build()
    }

    // Ícone de Peixe / Tilápia
    val Fish: ImageVector by lazy {
        ImageVector.Builder(
            name = "Fish",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(2f, 16f)
                cubicTo(8f, 22f, 17f, 18f, 22f, 12f)
                cubicTo(17f, 6f, 8f, 2f, 2f, 8f)
                cubicTo(5f, 12f, 5f, 12f, 2f, 16f)
                close()
                // Olho
                moveTo(18f, 11f); lineTo(18.01f, 11f)
            }
        }.build()
    }

    // Ícone de Zoom / Expandir
    val ZoomIn: ImageVector by lazy {
        ImageVector.Builder(
            name = "ZoomIn",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                // Cantos de expansão
                moveTo(15f, 3f); lineTo(21f, 3f); lineTo(21f, 9f)
                moveTo(9f, 21f); lineTo(3f, 21f); lineTo(3f, 15f)
                moveTo(21f, 15f); lineTo(21f, 21f); lineTo(15f, 21f)
                moveTo(3f, 9f); lineTo(3f, 3f); lineTo(9f, 3f)
            }
        }.build()
    }
}
