package com.example.fish_finder.data.repository

import com.example.fish_finder.data.datasource.InferenceDataSource
import com.example.fish_finder.data.datasource.MockInferenceDataSource
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.repository.InferenceRepository

/**
 * Implementação padrão do repositório de inferência (RF1, RF4, RF6).
 * Conecta casos de uso aos provedores de dados locais e remotos.
 */
class InferenceRepositoryImpl(
    private val dataSource: InferenceDataSource = MockInferenceDataSource()
) : InferenceRepository {

    override suspend fun getInitialHistory(): List<InferenceResult> {
        return dataSource.loadHistory()
    }

    override suspend fun executeInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult {
        return dataSource.runInference(
            modelName = modelName,
            confidenceThreshold = confidenceThreshold,
            customLabels = customLabels,
            imageUri = imageUri
        )
    }
}
