package com.example.fish_finder.data.remote.mapper

import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Conversor entre modelos de domínio e os esquemas JSON exigidos pela API REST do backend (RF6 / RF7).
 */
object InferenceMapper {

    /**
     * Serializa uma [InferenceResult] no payload JSON padronizado especificado no README.
     */
    fun toJsonPayload(result: InferenceResult, confidenceThreshold: Float): String {
        val json = JSONObject()

        json.put("id_sessao", result.sessionId)
        json.put("nome_modelo", result.modelName)
        json.put("data_hora", formatIsoTimestamp(result.timestamp))
        json.put("tempo_execucao_ms", result.executionTimeMs.toDouble())

        val dimensionObj = JSONObject()
        dimensionObj.put("largura", result.imageWidthPx)
        dimensionObj.put("altura", result.imageHeightPx)
        json.put("dimensao_imagem", dimensionObj)

        json.put("total_objetos", result.totalObjects)
        json.put("limiar_confianca_utilizado", confidenceThreshold.toDouble())
        result.imageUri?.let { json.put("uri_imagem", it) }

        val boxesArray = JSONArray()
        result.boundingBoxes.forEach { box ->
            val boxJson = JSONObject()
            boxJson.put("id_caixa", box.boxId)
            boxJson.put("classe", box.classLabel)
            boxJson.put("confianca", box.confidence.toDouble())

            val coordsJson = JSONObject()
            coordsJson.put("x_min", box.xMin.toDouble())
            coordsJson.put("y_min", box.yMin.toDouble())
            coordsJson.put("x_max", (box.xMin + box.widthPx).toDouble())
            coordsJson.put("y_max", (box.yMin + box.heightPx).toDouble())
            boxJson.put("coordenadas", coordsJson)

            val metricsJson = JSONObject()
            metricsJson.put("largura_px", box.widthPx.toDouble())
            metricsJson.put("altura_px", box.heightPx.toDouble())

            val centroidJson = JSONObject()
            centroidJson.put("x", box.centroidX.toDouble())
            centroidJson.put("y", box.centroidY.toDouble())
            metricsJson.put("centroide", centroidJson)
            metricsJson.put("area_px2", box.areaPx.toDouble())

            boxJson.put("metricas_geometricas", metricsJson)
            boxesArray.put(boxJson)
        }
        json.put("caixas_delimitadoras", boxesArray)

        return json.toString()
    }

    /**
     * Desserializa um [JSONObject] retornado pelo backend em um [InferenceResult].
     */
    fun fromJsonObject(json: JSONObject): InferenceResult {
        val sessionId = json.optString("id_sessao", "sessao_desconhecida")
        val modelName = json.optString("nome_modelo", "tilapia.onnx")
        val isoDate = json.optString("data_hora", "")
        val formattedTimestamp = formatDisplayTimestamp(isoDate)

        val executionTimeMs = json.optDouble("tempo_execucao_ms", 0.0).toLong()
        val totalObjects = json.optInt("total_objetos", 0)

        val dimensionObj = json.optJSONObject("dimensao_imagem")
        val imageWidth = dimensionObj?.optInt("largura", 1920) ?: 1920
        val imageHeight = dimensionObj?.optInt("altura", 1080) ?: 1080

        val boxes = mutableListOf<BoundingBox>()
        val boxesArray = json.optJSONArray("caixas_delimitadoras") ?: JSONArray()

        for (i in 0 until boxesArray.length()) {
            val boxObj = boxesArray.getJSONObject(i)
            val boxId = boxObj.optInt("id_caixa", i + 1)
            val label = boxObj.optString("classe", "tilapia")
            val confidence = boxObj.optDouble("confianca", 0.9).toFloat()

            val metricsObj = boxObj.optJSONObject("metricas_geometricas")
            val coordsObj = boxObj.optJSONObject("coordenadas")

            val xMin = coordsObj?.optDouble("x_min", 0.0)?.toFloat() ?: 0f
            val yMin = coordsObj?.optDouble("y_min", 0.0)?.toFloat() ?: 0f
            val widthPx = metricsObj?.optDouble("largura_px", 100.0)?.toFloat() ?: 100f
            val heightPx = metricsObj?.optDouble("altura_px", 50.0)?.toFloat() ?: 50f

            boxes.add(
                BoundingBox(
                    boxId = boxId,
                    classLabel = label,
                    confidence = confidence,
                    xMin = xMin,
                    yMin = yMin,
                    widthPx = widthPx,
                    heightPx = heightPx
                )
            )
        }

        val avgConf = if (boxes.isNotEmpty()) {
            boxes.map { it.confidence }.average().toFloat()
        } else {
            0.0f
        }

        val imageUri = if (json.has("uri_imagem") && !json.isNull("uri_imagem")) {
            json.getString("uri_imagem").takeIf { it.isNotBlank() }
        } else null

        return InferenceResult(
            sessionId = sessionId,
            timestamp = formattedTimestamp,
            modelName = modelName,
            totalObjects = totalObjects,
            averageConfidence = avgConf,
            executionTimeMs = executionTimeMs,
            imageWidthPx = imageWidth,
            imageHeightPx = imageHeight,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = boxes,
            imageUri = imageUri
        )
    }

    private fun formatIsoTimestamp(input: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss", Locale("pt", "BR"))
            val parsedDate = inputFormat.parse(input) ?: Date()
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            isoFormat.format(parsedDate)
        } catch (e: Exception) {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            isoFormat.format(Date())
        }
    }

    private fun formatDisplayTimestamp(isoInput: String): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val cleanInput = isoInput.substringBefore("Z").substringBefore(".")
            val parsedDate = isoFormat.parse(cleanInput) ?: Date()
            val outputFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss", Locale("pt", "BR"))
            outputFormat.format(parsedDate)
        } catch (e: Exception) {
            isoInput
        }
    }
}
