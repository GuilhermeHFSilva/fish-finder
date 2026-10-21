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
 * Fonte de dados simulada para inferência On-Device de tilápias em tanques escavados (Roboflow YOLO/ONNX).
 * Garante reprodutibilidade visual e comportamental enquanto prepara a arquitetura para integração nativa com ONNX Runtime.
 */
class MockInferenceDataSource : InferenceDataSource {

    override suspend fun loadHistory(): List<InferenceResult> = withContext(Dispatchers.IO) {
        createRichMockHistory()
    }

    override suspend fun runInference(
        modelName: String,
        confidenceThreshold: Float,
        customLabels: List<String>,
        imageUri: String?
    ): InferenceResult = withContext(Dispatchers.Default) {
        // Simula latência realista de processamento neural on-device (NPU / GPU local)
        delay(850)

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
            executionTimeMs = Random.nextLong(820, 940),
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = newBoxes
        )
    }

    private fun generateRealisticBoxes(threshold: Float): List<BoundingBox> {
        val sampleBoxes = mutableListOf<BoundingBox>()
        val count = 51 // Contagem padronizada da especificação
        var idCounter = 1

        val rng = Random(42) // Semente estável para consistência visual

        for (i in 0 until count) {
            val conf = 0.70f + rng.nextFloat() * 0.28f
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

    private fun createRichMockHistory(): List<InferenceResult> {
        val s1Boxes = generateRealisticBoxes(0.25f)

        val s2Boxes = listOf(
            BoundingBox(1, "tilapia", 0.96f, 150f, 200f, 130f, 50f),
            BoundingBox(2, "tilapia", 0.94f, 320f, 280f, 125f, 48f),
            BoundingBox(3, "tilapia", 0.91f, 480f, 310f, 140f, 55f),
            BoundingBox(4, "tilapia", 0.89f, 650f, 190f, 120f, 46f),
            BoundingBox(5, "tilapia", 0.87f, 820f, 410f, 135f, 52f),
            BoundingBox(6, "tilapia", 0.93f, 980f, 350f, 128f, 49f),
            BoundingBox(7, "tilapia", 0.85f, 1150f, 260f, 122f, 47f),
            BoundingBox(8, "tilapia", 0.92f, 1320f, 480f, 138f, 53f)
        )

        val s3Boxes = listOf(
            BoundingBox(1, "tilapia", 0.95f, 200f, 220f, 126f, 48f),
            BoundingBox(2, "tilapia", 0.92f, 420f, 340f, 132f, 51f),
            BoundingBox(3, "tilapia", 0.90f, 680f, 290f, 118f, 45f),
            BoundingBox(4, "tilapia", 0.88f, 910f, 410f, 142f, 54f)
        )

        return listOf(
            InferenceResult(
                sessionId = "2026-09-18-001",
                timestamp = "18/09/2026 às 23:06:12",
                modelName = "best.onnx",
                totalObjects = 51,
                averageConfidence = 0.90f,
                executionTimeMs = 909,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                syncStatus = SyncStatus.SYNCED,
                boundingBoxes = s1Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-18-002",
                timestamp = "18/09/2026 às 23:18:45",
                modelName = "tilapia.onnx",
                totalObjects = 38,
                averageConfidence = 0.91f,
                executionTimeMs = 884,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                syncStatus = SyncStatus.SYNCED,
                boundingBoxes = s2Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-19-001",
                timestamp = "19/09/2026 às 08:42:10",
                modelName = "tilapia.onnx",
                totalObjects = 44,
                averageConfidence = 0.89f,
                executionTimeMs = 915,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                syncStatus = SyncStatus.PENDING,
                boundingBoxes = s3Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-19-002",
                timestamp = "19/09/2026 às 14:15:33",
                modelName = "best.onnx",
                totalObjects = 29,
                averageConfidence = 0.93f,
                executionTimeMs = 870,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                syncStatus = SyncStatus.SYNCED,
                boundingBoxes = s2Boxes.take(4)
            )
        )
    }
}
