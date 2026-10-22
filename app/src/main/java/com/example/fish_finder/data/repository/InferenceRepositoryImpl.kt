package com.example.fish_finder.data.repository

import com.example.fish_finder.data.datasource.InferenceDataSource
import com.example.fish_finder.data.datasource.MockInferenceDataSource
import com.example.fish_finder.data.local.LocalInferenceStorage
import com.example.fish_finder.data.remote.client.InferenceApiClient
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import com.example.fish_finder.domain.repository.InferenceRepository

/**
 * Implementação do repositório de inferência com persistência local durável e sincronização com backend (RF1, RF4, RF6, RF7).
 */
class InferenceRepositoryImpl(
    private val localDataSource: InferenceDataSource = MockInferenceDataSource(),
    private val apiClient: InferenceApiClient = InferenceApiClient(),
    private val localStorage: LocalInferenceStorage? = null
) : InferenceRepository {

    /**
     * Carrega histórico: inicia limpo por padrão e recupera as inferências persistidas conforme o app é utilizado.
     */
    override suspend fun getInitialHistory(): List<InferenceResult> {
        val localHistory = localStorage?.getHistory() ?: emptyList()
        if (localHistory.isNotEmpty()) {
            return localHistory
        }

        val remoteResult = apiClient.fetchHistory()
        val remoteList = remoteResult.getOrNull()?.takeIf { it.isNotEmpty() }
        if (remoteList != null) {
            return remoteList
        }

        return localDataSource.loadHistory()
    }

    /**
     * Executa a inferência neural, salva localmente e transmite ao servidor backend (RF6 / RF7).
     */
    override suspend fun executeInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult {
        // 1. Processamento da inferência local on-device
        val inference = localDataSource.runInference(
            modelName = modelName,
            confidenceThreshold = confidenceThreshold,
            customLabels = customLabels,
            imageUri = imageUri
        )

        // 2. Transmissão assíncrona ao servidor backend (RF6)
        val sendResult = apiClient.sendInference(inference, confidenceThreshold)

        // 3. Atualização do status de persistência e auditoria
        val finalSyncStatus = if (sendResult.isSuccess) {
            SyncStatus.SYNCED
        } else {
            SyncStatus.PENDING
        }
        val finalResult = inference.copy(syncStatus = finalSyncStatus)

        // 4. Salva a inferência no armazenamento persistente do dispositivo
        localStorage?.saveInference(finalResult, confidenceThreshold)

        return finalResult
    }
}
