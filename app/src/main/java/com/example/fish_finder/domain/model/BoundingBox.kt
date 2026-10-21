package com.example.fish_finder.domain.model

import java.util.Locale

/**
 * Representa uma caixa delimitadora (Bounding Box) detectada pelo modelo neural (RF5).
 *
 * @property boxId Identificador numérico único da caixa na sessão.
 * @property classLabel Rótulo da classe do objeto detectado (ex: "tilapia").
 * @property confidence Grau de confiança da inferência (0.0f a 1.0f).
 * @property xMin Coordenada X mínima do vértice superior esquerdo (em pixels).
 * @property yMin Coordenada Y mínima do vértice superior esquerdo (em pixels).
 * @property widthPx Largura em pixels da caixa delimitadora (W_px).
 * @property heightPx Altura em pixels da caixa delimitadora (H_px).
 * @property centroidX Coordenada X do centróide geométrico: X_min + (W_px / 2).
 * @property centroidY Coordenada Y do centróide geométrico: Y_min + (H_px / 2).
 */
data class BoundingBox(
    val boxId: Int,
    val classLabel: String,
    val confidence: Float,
    val xMin: Float,
    val yMin: Float,
    val widthPx: Float,
    val heightPx: Float,
    val centroidX: Float = xMin + (widthPx / 2f),
    val centroidY: Float = yMin + (heightPx / 2f)
) {
    /**
     * Área superficial calculada em pixels quadrados: A_px = W_px * H_px.
     */
    val areaPx: Float
        get() = widthPx * heightPx


    /**
     * Formatação amigável das dimensões para exibição e auditoria.
     */
    val formattedDimensions: String
        get() = String.format(Locale.US, "%.2f × %.2f px", widthPx, heightPx)

    /**
     * Formatação amigável do centróide geométrico.
     */
    val formattedCentroid: String
        get() = String.format(Locale.US, "(%.2f, %.2f) px", centroidX, centroidY)

    /**
     * Formatação amigável da área superficial em px².
     */
    val formattedArea: String
        get() = String.format(Locale.US, "%.2f px²", areaPx)

    /**
     * Formatação percentual da confiança.
     */
    val formattedConfidence: String
        get() = "${(confidence * 100).toInt()}%"
}
