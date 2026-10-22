package com.example.fish_finder.data

import com.example.fish_finder.data.detector.YoloDecoder
import org.junit.Assert.assertEquals
import org.junit.Test

class YoloDecoderTest {

    // Colunas: [cx, cy, w, h, score]; letterbox com scale 0.5 e padY 100
    private fun output(vararg anchors: FloatArray) = Array(5) { row -> FloatArray(anchors.size) { anchors[it][row] } }

    @Test
    fun mapeiaParaImagemOriginalEFiltraPorLimiar() {
        val out = output(floatArrayOf(200f, 300f, 100f, 60f, 0.9f), floatArrayOf(50f, 50f, 10f, 10f, 0.1f))
        val boxes = YoloDecoder.decode(out, scale = 0.5f, padX = 0f, padY = 100f, imageWidth = 1000, imageHeight = 1000, threshold = 0.3f)

        assertEquals(1, boxes.size)
        assertEquals(300f, boxes[0].xMin, 0.01f)
        assertEquals(340f, boxes[0].yMin, 0.01f)
        assertEquals(200f, boxes[0].widthPx, 0.01f)
        assertEquals(120f, boxes[0].heightPx, 0.01f)
    }

    @Test
    fun nmsMantemSoAMaisConfiante() {
        val out = output(floatArrayOf(300f, 300f, 100f, 100f, 0.6f), floatArrayOf(305f, 300f, 100f, 100f, 0.9f), floatArrayOf(500f, 100f, 50f, 50f, 0.5f))
        val boxes = YoloDecoder.decode(out, 1f, 0f, 0f, 640, 640, 0.3f)

        assertEquals(listOf(0.9f, 0.5f), boxes.map { it.confidence })
        assertEquals(listOf(1, 2), boxes.map { it.boxId })
    }
}
