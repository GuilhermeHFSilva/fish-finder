package com.example.fish_finder.domain.repository

import com.example.fish_finder.domain.model.InferenceResult

/**
 * Contrato de repositório para o domínio de inferência neural (RF1, RF4, RF6, RF7).
 * Isola a lógica de negócio das implementações concretas (ONNX Runtime, Mock, Retrofit).
 */
interface InferenceRepository {

    /**
     * Recupera o histórico consolidado de inferências passadas.
     */
    suspend fun getInitialHistory(): List<InferenceResult>

    /**
     * Executa a inferência neural on-device sobre a imagem especificada
     * e registra a sessão de telemetria geométrica.
     *
     * @param modelName Nome do modelo selecionado (ex: "tilapia.onnx").
     * @param confidenceThreshold Limiar mínimo de corte para caixas delimitadoras.
     * @param customLabels Lista de classes válidas a detectar.
     * @param imageUri Identificador da imagem adquirida via câmera ou galeria.
     */
    suspend fun executeInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult
}
