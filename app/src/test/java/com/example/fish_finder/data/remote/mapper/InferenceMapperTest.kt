package com.example.fish_finder.data.remote.mapper

import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class InferenceMapperTest {

    @Test
    fun serialization_and_deserialization_preservesImageUri() {
        val original = InferenceResult(
            sessionId = "sessao_test_123",
            timestamp = "21/10/2026 às 21:00:00",
            modelName = "tilapia.onnx",
            totalObjects = 1,
            averageConfidence = 0.95f,
            executionTimeMs = 150L,
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = listOf(
                BoundingBox(
                    boxId = 1,
                    classLabel = "tilapia",
                    confidence = 0.95f,
                    xMin = 100f,
                    yMin = 150f,
                    widthPx = 200f,
                    heightPx = 100f
                )
            ),
            imageUri = "file:///data/user/0/com.example.fish_finder/files/captured_images/img_123.jpg"
        )

        val jsonString = InferenceMapper.toJsonPayload(original, 0.25f)
        val jsonObj = JSONObject(jsonString)

        assertEquals("file:///data/user/0/com.example.fish_finder/files/captured_images/img_123.jpg", jsonObj.getString("uri_imagem"))

        val deserialized = InferenceMapper.fromJsonObject(jsonObj)

        assertEquals(original.sessionId, deserialized.sessionId)
        assertEquals(original.modelName, deserialized.modelName)
        assertEquals(original.imageUri, deserialized.imageUri)
        assertEquals(1, deserialized.boundingBoxes.size)
        assertEquals(200f, deserialized.boundingBoxes[0].widthPx, 0.01f)
    }
}
