package com.example.fish_finder.domain.model

/**
 * Representa o resultado consolidado de uma rodada de inferência on-device (RF4 / RF5 / RF8).
 *
 * @property sessionId Identificador único da sessão (ex: "sessao_20260918_001").
 * @property timestamp Data e hora formatada da execução.
 * @property modelName Nome do arquivo do modelo neural utilizado (ex: "tilapia.onnx").
 * @property totalObjects Quantidade total de objetos identificados acima do limiar.
 * @property averageConfidence Confiança média ponderada entre todas as detecções válidas.
 * @property executionTimeMs Tempo de inferência pura em milissegundos.
 * @property imageWidthPx Largura nativa da imagem processada.
 * @property imageHeightPx Altura nativa da imagem processada.
 * @property syncStatus Estado de persistência e sincronização com o backend.
 * @property boundingBoxes Lista de caixas delimitadoras calculadas.
 * @property imageUri URI local ou de arquivo da imagem associada.
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
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val boundingBoxes: List<BoundingBox> = emptyList(),
    val imageUri: String? = null
) {
    /**
     * Construtor de compatibilidade para código que passa [isSyncedWithServer] como booleano.
     */
    constructor(
        sessionId: String,
        timestamp: String,
        modelName: String,
        totalObjects: Int,
        averageConfidence: Float,
        executionTimeMs: Long,
        imageWidthPx: Int,
        imageHeightPx: Int,
        isSyncedWithServer: Boolean,
        boundingBoxes: List<BoundingBox>,
        imageUri: String? = null
    ) : this(
        sessionId = sessionId,
        timestamp = timestamp,
        modelName = modelName,
        totalObjects = totalObjects,
        averageConfidence = averageConfidence,
        executionTimeMs = executionTimeMs,
        imageWidthPx = imageWidthPx,
        imageHeightPx = imageHeightPx,
        syncStatus = if (isSyncedWithServer) SyncStatus.SYNCED else SyncStatus.PENDING,
        boundingBoxes = boundingBoxes,
        imageUri = imageUri
    )

    /**
     * Propriedade booleana para compatibilidade com a camada de visualização existente.
     */
    val isSyncedWithServer: Boolean
        get() = syncStatus == SyncStatus.SYNCED

    /**
     * Retorna a contagem agrupada por classe (ex: ["tilapia": 51]).
     */
    val classCounts: Map<String, Int>
        get() = boundingBoxes.groupingBy { it.classLabel }.eachCount()

    /**
     * Formatação amigável da confiança média ponderada.
     */
    val formattedAverageConfidence: String
        get() = "${(averageConfidence * 100).toInt()}%"
}
