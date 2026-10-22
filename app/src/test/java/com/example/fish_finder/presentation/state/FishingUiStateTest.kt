package com.example.fish_finder.presentation.state

import com.example.fish_finder.domain.model.BoundingBox
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FishingUiStateTest {

    @Test
    fun initialState_hasNoOverviewAndNoEncounters() {
        val state = FishingUiState()

        assertNull(state.detectionOverview)
        assertNull(state.currentEncounter)
        assertEquals(0, state.encounters.size)
    }

    @Test
    fun scanSummary_formatsConfidenceCorrectly() {
        val summary = ScanSummary(totalFound = 4, averageConfidence = 0.876f)

        assertEquals(4, summary.totalFound)
        assertEquals("87%", summary.formattedConfidence)
    }

    @Test
    fun scanSummary_formatsZeroConfidence() {
        val summary = ScanSummary(totalFound = 0, averageConfidence = 0f)

        assertEquals(0, summary.totalFound)
        assertEquals("0%", summary.formattedConfidence)
    }

    @Test
    fun boundingBoxes_canBeStoredAndIdentified() {
        val boxes = listOf(
            BoundingBox(1, "tilapia", 0.92f, 10f, 20f, 100f, 50f),
            BoundingBox(2, "tilapia", 0.85f, 120f, 80f, 90f, 45f)
        )

        assertEquals(2, boxes.size)
        assertEquals(1, boxes.first().boxId)
        assertEquals(2, boxes.last().boxId)
    }
}
