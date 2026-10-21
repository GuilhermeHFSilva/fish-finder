package com.example.fish_finder.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InferenceResultTest {

    @Test
    fun classCounts_aggregatesCorrectlyByLabel() {
        val boxes = listOf(
            BoundingBox(1, "tilapia", 0.95f, 10f, 10f, 100f, 50f),
            BoundingBox(2, "tilapia", 0.91f, 150f, 10f, 100f, 50f),
            BoundingBox(3, "alevino", 0.85f, 300f, 10f, 50f, 30f)
        )

        val result = InferenceResult(
            sessionId = "sessao_test_01",
            timestamp = "21/10/2026 às 14:00:00",
            modelName = "tilapia.onnx",
            totalObjects = 3,
            averageConfidence = 0.903f,
            executionTimeMs = 850L,
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = boxes
        )

        val counts = result.classCounts
        assertEquals(2, counts["tilapia"])
        assertEquals(1, counts["alevino"])
        assertEquals("90%", result.formattedAverageConfidence)
        assertTrue(result.isSyncedWithServer)
    }

    @Test
    fun booleanConstructor_mapsSyncStatusProperly() {
        val pendingResult = InferenceResult(
            sessionId = "sessao_test_02",
            timestamp = "21/10/2026 às 14:05:00",
            modelName = "best.onnx",
            totalObjects = 0,
            averageConfidence = 0.0f,
            executionTimeMs = 900L,
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            isSyncedWithServer = false,
            boundingBoxes = emptyList()
        )

        assertEquals(SyncStatus.PENDING, pendingResult.syncStatus)
        assertFalse(pendingResult.isSyncedWithServer)
    }
}
