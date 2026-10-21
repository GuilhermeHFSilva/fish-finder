package com.example.fish_finder.data.datasource

import com.example.fish_finder.domain.model.InferenceResult

/**
 * Fonte de dados para operações de inferência e consulta de histórico.
 */
interface InferenceDataSource {

    suspend fun loadHistory(): List<InferenceResult>

    suspend fun runInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult
}
