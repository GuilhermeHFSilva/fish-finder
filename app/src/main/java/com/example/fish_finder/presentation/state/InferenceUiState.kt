package com.example.fish_finder.presentation.state

import com.example.fish_finder.domain.model.InferenceResult

/**
 * Estado reativo imutável da interface do FishFinder.
 * Gerenciado pelo ViewModel e consumido pelas telas Jetpack Compose via StateFlow.
 * Inicia sem dados pré-carregados (histórico e imagem limpos).
 */
data class InferenceUiState(
    val selectedModel: String = "tilapia.onnx (Modelo Embutido)",
    val customLabels: String = "tilapia, juvenis, alevinos",
    val confidenceThreshold: Float = 0.25f,
    val isInferring: Boolean = false,
    val isImageSelected: Boolean = false,
    val selectedImageUri: String? = null,
    val currentInference: InferenceResult? = null,
    val historyList: List<InferenceResult> = emptyList(),
    val searchQuery: String = "",
    val selectedSessionForDetail: InferenceResult? = null,
    val isExportInfoDialogOpen: Boolean = false,
    val isZoomDialogOpen: Boolean = false,
    val isDarkMode: Boolean = false,
    val statusMessage: String? = null
) {
    /**
     * Lista filtrada de históricos com base na busca por data, modelo ou ID de sessão.
     */
    val filteredHistory: List<InferenceResult>
        get() = if (searchQuery.isBlank()) {
            historyList
        } else {
            historyList.filter {
                it.modelName.contains(searchQuery, ignoreCase = true) ||
                it.timestamp.contains(searchQuery, ignoreCase = true) ||
                it.sessionId.contains(searchQuery, ignoreCase = true)
            }
        }

    /**
     * Quantidade total de inferências no histórico.
     */
    val totalInferences: Int
        get() = historyList.size

    /**
     * Total acumulado de objetos contados em todas as sessões históricas.
     */
    val totalObjectsCounted: Int
        get() = historyList.sumOf { it.totalObjects }

    /**
     * Média aritmética do tempo de inferência neural (em ms).
     */
    val averageExecutionTimeMs: Long
        get() = if (historyList.isNotEmpty()) {
            historyList.map { it.executionTimeMs }.average().toLong()
        } else {
            0L
        }

    /**
     * Extrai a lista de rótulos configurados como tokens limpos.
     */
    val parsedLabels: List<String>
        get() = customLabels.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
