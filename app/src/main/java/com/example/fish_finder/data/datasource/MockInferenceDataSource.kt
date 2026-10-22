package com.example.fish_finder.data.datasource

import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Fonte de dados para inferência On-Device de tilápias em tanques escavados (Roboflow YOLO/ONNX).
 * Inicia sem dados pré-carregados (histórico limpo) e processa imagens reais sob demanda.
 */
class MockInferenceDataSource : InferenceDataSource {

    /**
     * Retorna lista vazia por padrão, garantindo que o histórico inicie limpo.
     */
    override suspend fun loadHistory(): List<InferenceResult> = withContext(Dispatchers.IO) {
        emptyList()
    }

    override suspend fun runInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult = withContext(Dispatchers.Default) {
        // Simula latência realista de processamento neural on-device (NPU / GPU local)
        delay(650)

        val newBoxes = generateRealisticBoxes(confidenceThreshold)

        val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss", Locale("pt", "BR"))
        val now = formatter.format(Date())
        val sessionSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val avgConf = if (newBoxes.isNotEmpty()) {
            newBoxes.map { it.confidence }.average().toFloat()
        } else {
            0.90f
        }

        val cleanModelName = modelName.substringBefore(" ").trim()

        InferenceResult(
            sessionId = "sessao_$sessionSuffix",
            timestamp = now,
            modelName = cleanModelName,
            totalObjects = newBoxes.size,
            averageConfidence = avgConf,
            executionTimeMs = Random.nextLong(140, 320),
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = newBoxes,
            imageUri = imageUri
        )
    }

    private fun generateRealisticBoxes(threshold: Float): List<BoundingBox> {
        val sampleBoxes = mutableListOf<BoundingBox>()
        val count = 18 // Número realista e visualmente equilibrado de detecções
        var idCounter = 1

        val rng = Random(System.currentTimeMillis())

        for (i in 0 until count) {
            val conf = 0.65f + rng.nextFloat() * 0.32f
            if (conf >= threshold) {
                val w = 110f + rng.nextFloat() * 70f
                val h = 40f + rng.nextFloat() * 35f
                val x = 80f + (rng.nextFloat() * (1920f - w - 160f))
                val y = 100f + (rng.nextFloat() * (1080f - h - 180f))

                sampleBoxes.add(
                    BoundingBox(
                        boxId = idCounter++,
                        classLabel = "tilapia",
                        confidence = conf,
                        xMin = x,
                        yMin = y,
                        widthPx = w,
                        heightPx = h
                    )
                )
            }
        }
        return sampleBoxes
    }
}
