package com.example.fish_finder.data.remote.client

import com.example.fish_finder.data.remote.BackendConfig
import com.example.fish_finder.data.remote.mapper.InferenceMapper
import com.example.fish_finder.domain.model.InferenceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Cliente HTTP para integração com a API REST do Backend (RF6 / RF7).
 */
class InferenceApiClient {

    /**
     * Envia o resultado de uma inferência para persistência no backend via HTTP POST (RF6).
     */
    suspend fun sendInference(
        result: InferenceResult,
        confidenceThreshold: Float
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(BackendConfig.inferencesEndpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = BackendConfig.TIMEOUT_CONNECT_MS
                readTimeout = BackendConfig.TIMEOUT_READ_MS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }

            val jsonPayload = InferenceMapper.toJsonPayload(result, confidenceThreshold)

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(jsonPayload)
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                val errorStream = connection.errorStream
                val errorBody = errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                throw IllegalStateException("Falha HTTP $responseCode no POST de inferência: $errorBody")
            }
        }
    }

    /**
     * Consulta a listagem histórica cronológica de sessões registradas no servidor via HTTP GET (RF7 / RF8).
     */
    suspend fun fetchHistory(): Result<List<InferenceResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL(BackendConfig.inferencesEndpoint)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = BackendConfig.TIMEOUT_CONNECT_MS
                readTimeout = BackendConfig.TIMEOUT_READ_MS
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                throw IllegalStateException("Falha HTTP $responseCode ao consultar histórico")
            }

            val responseText = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val rootJson = JSONObject(responseText)
            val dataArray = rootJson.optJSONArray("dados") ?: JSONArray()

            val list = mutableListOf<InferenceResult>()
            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                list.add(InferenceMapper.fromJsonObject(item))
            }
            list
        }
    }

    /**
     * Consulta detalhes de uma sessão específica pelo seu identificador via HTTP GET.
     */
    suspend fun fetchInferenceDetails(sessionId: String): Result<InferenceResult?> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("${BackendConfig.inferencesEndpoint}/$sessionId")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = BackendConfig.TIMEOUT_CONNECT_MS
                readTimeout = BackendConfig.TIMEOUT_READ_MS
                setRequestProperty("Accept", "application/json")
            }

            val responseCode = connection.responseCode
            if (responseCode == 404) {
                return@runCatching null
            }
            if (responseCode !in 200..299) {
                throw IllegalStateException("Falha HTTP $responseCode ao buscar detalhes da sessão")
            }

            val responseText = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val rootJson = JSONObject(responseText)
            val dataObj = rootJson.optJSONObject("dados") ?: rootJson
            InferenceMapper.fromJsonObject(dataObj)
        }
    }
}
