package com.example.fish_finder.data.detector

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import com.example.fish_finder.domain.model.BoundingBox
import java.nio.FloatBuffer
import kotlin.math.min

/** Detector de tilápias 100% on-device (tilapia.onnx, entrada 640x640 com letterbox). */
class FishDetector(context: Context) : AutoCloseable {

    private val env = OrtEnvironment.getEnvironment()
    private val session: OrtSession =
        env.createSession(context.assets.open(MODEL_ASSET).use { it.readBytes() })

    fun detect(bitmap: Bitmap, threshold: Float = 0.3f): List<BoundingBox> {
        val scale = min(SIZE / bitmap.width.toFloat(), SIZE / bitmap.height.toFloat())
        val padX = (SIZE - bitmap.width * scale) / 2f
        val padY = (SIZE - bitmap.height * scale) / 2f

        val letterbox = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        Canvas(letterbox).apply {
            drawColor(Color.rgb(114, 114, 114))
            translate(padX, padY)
            scale(scale, scale)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        val pixels = IntArray(SIZE * SIZE).also { letterbox.getPixels(it, 0, SIZE, 0, 0, SIZE, SIZE) }
        letterbox.recycle()

        val plane = SIZE * SIZE
        val input = FloatArray(3 * plane)
        for (i in 0 until plane) {
            input[i] = Color.red(pixels[i]) / 255f
            input[plane + i] = Color.green(pixels[i]) / 255f
            input[2 * plane + i] = Color.blue(pixels[i]) / 255f
        }

        OnnxTensor.createTensor(env, FloatBuffer.wrap(input), longArrayOf(1, 3, SIZE.toLong(), SIZE.toLong())).use { tensor ->
            session.run(mapOf("images" to tensor)).use { result ->
                @Suppress("UNCHECKED_CAST")
                val output = (result[0].value as Array<Array<FloatArray>>)[0]
                return YoloDecoder.decode(output, scale, padX, padY, bitmap.width, bitmap.height, threshold)
            }
        }
    }

    override fun close() {
        session.close()
    }

    private companion object {
        const val MODEL_ASSET = "tilapia.onnx"
        const val SIZE = 640
    }
}
