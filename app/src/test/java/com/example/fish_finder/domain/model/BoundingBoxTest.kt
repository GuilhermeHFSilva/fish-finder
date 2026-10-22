package com.example.fish_finder.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class BoundingBoxTest {

    @Test
    fun calculateCentroid_returnsCorrectCoordinates() {
        val box = BoundingBox(
            boxId = 1,
            classLabel = "tilapia",
            confidence = 0.95f,
            xMin = 100f,
            yMin = 200f,
            widthPx = 150f,
            heightPx = 80f
        )

        assertEquals(175f, box.centroidX, 0.001f)
        assertEquals(240f, box.centroidY, 0.001f)
    }

    @Test
    fun calculateArea_returnsWidthTimesHeight() {
        val box = BoundingBox(
            boxId = 2,
            classLabel = "tilapia",
            confidence = 0.88f,
            xMin = 50f,
            yMin = 50f,
            widthPx = 200f,
            heightPx = 100f
        )

        assertEquals(20000f, box.areaPx, 0.001f)
    }

    @Test
    fun formattingHelpers_produceExpectedStrings() {
        val box = BoundingBox(
            boxId = 3,
            classLabel = "tilapia",
            confidence = 0.92f,
            xMin = 10f,
            yMin = 20f,
            widthPx = 120.5f,
            heightPx = 60.5f
        )

        assertEquals("120.50 × 60.50 px", box.formattedDimensions)
        assertEquals("(70.25, 50.25) px", box.formattedCentroid)
        assertEquals("7290.25 px²", box.formattedArea)
        assertEquals("92%", box.formattedConfidence)
    }
}
