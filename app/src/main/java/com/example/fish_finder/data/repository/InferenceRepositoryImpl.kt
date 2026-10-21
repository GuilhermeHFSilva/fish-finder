package com.example.fish_finder.data.repository

import com.example.fish_finder.data.datasource.InferenceDataSource
import com.example.fish_finder.data.datasource.MockInferenceDataSource
import com.example.fish_finder.data.remote.client.InferenceApiClient
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import com.example.fish_finder.domain.repository.InferenceRepository

/**
 * Implementação do repositório de inferência com suporte a sincronização com Backend REST (RF1, RF4, RF6, RF7).
 * Integra o processamento de inferência local ao cliente de rede HTTP com resiliência offline-first.
 */
class InferenceRepositoryImpl(
    private val localDataSource: InferenceDataSource = MockInferenceDataSource(),
    private val apiClient: InferenceApiClient = InferenceApiClient()
) : InferenceRepository {

    /**
     * Carrega histórico priorizando o servidor backend (RF7 / RF8).
     * Em caso de indisponibilidade de rede, recorre com segurança ao armazenamento local.
     */
    override suspend fun getInitialHistory(): List<InferenceResult> {
        val remoteResult = apiClient.fetchHistory()

        return remoteResult.getOrNull()?.takeIf { it.isNotEmpty() }
            ?: localDataSource.loadHistory()
    }

    /**
     * Executa a inferência neural e transmite automaticamente o resultado para o servidor backend (RF6).
     */
    override suspend fun executeInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult {
        // 1. Processamento da inferência local (ONNX / Mock)
        val inference = localDataSource.runInference(
            modelName = modelName,
            confidenceThreshold = confidenceThreshold,
            customLabels = customLabels,
            imageUri = imageUri
        )

        // 2. Transmissão assíncrona ao servidor backend (RF6)
        val sendResult = apiClient.sendInference(inference, confidenceThreshold)

        // 3. Atualização do status de persistência e auditoria (RF8)
        val finalSyncStatus = if (sendResult.isSuccess) {
            SyncStatus.SYNCED
        } else {
            SyncStatus.PENDING
        }

        return inference.copy(syncStatus = finalSyncStatus)
    }
}
