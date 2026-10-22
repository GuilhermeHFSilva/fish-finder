package com.example.fish_finder.data.detector

import com.example.fish_finder.domain.model.BoundingBox
import kotlin.math.max
import kotlin.math.min

/**
 * Decodifica a saída do YOLO exportado sem NMS: `[cx, cy, w, h, score]` por âncora, em pixels
 * da imagem com letterbox. Devolve caixas na escala da imagem original, após NMS.
 */
object YoloDecoder {

    fun decode(
        output: Array<FloatArray>,
        scale: Float,
        padX: Float,
        padY: Float,
        imageWidth: Int,
        imageHeight: Int,
        threshold: Float,
        iouThreshold: Float = 0.45f
    ): List<BoundingBox> {
        val candidates = ArrayList<BoundingBox>()
        for (i in output[0].indices) {
            val score = output[4][i]
            if (score < threshold) continue
            val x1 = ((output[0][i] - output[2][i] / 2 - padX) / scale).coerceIn(0f, imageWidth.toFloat())
            val y1 = ((output[1][i] - output[3][i] / 2 - padY) / scale).coerceIn(0f, imageHeight.toFloat())
            val x2 = ((output[0][i] + output[2][i] / 2 - padX) / scale).coerceIn(0f, imageWidth.toFloat())
            val y2 = ((output[1][i] + output[3][i] / 2 - padY) / scale).coerceIn(0f, imageHeight.toFloat())
            if (x2 - x1 < 1f || y2 - y1 < 1f) continue
            candidates += BoundingBox(0, "tilapia", score, x1, y1, x2 - x1, y2 - y1)
        }

        val kept = ArrayList<BoundingBox>()
        for (box in candidates.sortedByDescending { it.confidence }) {
            if (kept.none { iou(it, box) > iouThreshold }) kept += box
        }
        return kept.mapIndexed { index, box -> box.copy(boxId = index + 1) }
    }

    fun iou(a: BoundingBox, b: BoundingBox): Float {
        val w = min(a.xMin + a.widthPx, b.xMin + b.widthPx) - max(a.xMin, b.xMin)
        val h = min(a.yMin + a.heightPx, b.yMin + b.heightPx) - max(a.yMin, b.yMin)
        if (w <= 0f || h <= 0f) return 0f
        val inter = w * h
        return inter / (a.widthPx * a.heightPx + b.widthPx * b.heightPx - inter)
    }
}
