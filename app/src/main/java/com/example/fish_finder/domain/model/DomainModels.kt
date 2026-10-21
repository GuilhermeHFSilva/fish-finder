package com.example.fish_finder.domain.model

import java.util.Locale

/**
 * Representa uma caixa delimitadora (Bounding Box) detectada pelo modelo de visão computacional.
 *
 * @property boxId Identificador único da caixa na sessão
 * @property classLabel Rótulo da classe do objeto detectado (ex: "tilapia")
 * @property confidence Nível de confiança da detecção (0.0f a 1.0f)
 * @property xMin Coordenada X mínima do vértice superior esquerdo (em pixels)
 * @property yMin Coordenada Y mínima do vértice superior esquerdo (em pixels)
 * @property widthPx Largura da caixa delimitadora (W_px)
 * @property heightPx Altura da caixa delimitadora (H_px)
 * @property centroidX Coordenada X do centróide geométrico: X_min + (W_px / 2)
 * @property centroidY Coordenada Y do centróide geométrico: Y_min + (H_px / 2)
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
     * Calcula a área superficial em pixels quadrados ocupada pela caixa delimitadora (A_px = W_px * H_px).
     */
    fun getAreaPx(): Float = widthPx * heightPx

    /**
     * Formatação amigável das coordenadas e dimensões para auditoria visual.
     */
    val formattedDimensions: String
        get() = String.format(Locale.US, "%.2f × %.2f px", widthPx, heightPx)

    val formattedCentroid: String
        get() = String.format(Locale.US, "(%.2f, %.2f) px", centroidX, centroidY)

    val formattedArea: String
        get() = String.format(Locale.US, "%.2f px²", getAreaPx())

    val formattedConfidence: String
        get() = "${(confidence * 100).toInt()}%"
}

/**
 * Representa o resultado consolidado de uma rodada de inferência on-device.
 *
 * @property sessionId Identificador único da sessão (ex: "sessao_20260918_001")
 * @property timestamp Data e hora formatada da execução (ex: "18/09/2026 às 23:06:12")
 * @property modelName Nome do arquivo do modelo utilizado (ex: "best.onnx" ou "tilapia.onnx")
 * @property totalObjects Quantidade total de objetos identificados acima do limiar
 * @property averageConfidence Confiança média ponderada entre todas as detecções válidas
 * @property executionTimeMs Tempo de inferência pura em milissegundos
 * @property imageWidthPx Largura nativa da imagem processada
 * @property imageHeightPx Altura nativa da imagem processada
 * @property isSyncedWithServer Indica se os dados foram persistidos no backend
 * @property boundingBoxes Lista de caixas delimitadoras calculadas
 */
data class InferenceResult(
    val sessionId: String,
    val timestamp: String,
    val modelName: String,
    val totalObjects: Int,
    val averageConfidence: Float,
    val executionTimeMs: Long,
    val imageWidthPx: Int,
    val imageHeightPx: Int,
    val isSyncedWithServer: Boolean,
    val boundingBoxes: List<BoundingBox>
) {
    /**
     * Retorna a contagem agrupada por classe (ex: ["tilapia": 51]).
     */
    val classCounts: Map<String, Int>
        get() = boundingBoxes.groupingBy { it.classLabel }.eachCount()

    val formattedAverageConfidence: String
        get() = "${(averageConfidence * 100).toInt()}%"
}
