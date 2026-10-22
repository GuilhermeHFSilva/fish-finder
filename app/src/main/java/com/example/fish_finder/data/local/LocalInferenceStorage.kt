package com.example.fish_finder.data.local

import android.content.Context
import com.example.fish_finder.data.remote.mapper.InferenceMapper
import com.example.fish_finder.domain.model.InferenceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Persistência local atômica e resiliente do histórico de inferências no armazenamento interno do app (RF7 / RF8).
 * Garante que o histórico inicie vazio e seja salvo conforme o usuário executa novas inferências.
 */
class LocalInferenceStorage(private val context: Context) {

    private val mutex = Mutex()
    private val historyFile: File by lazy {
        File(context.filesDir, "inferences_local_history.json")
    }

    /**
     * Carrega a lista de inferências salvas localmente pelo usuário.
     */
    suspend fun getHistory(): List<InferenceResult> = withContext(Dispatchers.IO) {
        mutex.withLock {
            if (!historyFile.exists()) {
                return@withContext emptyList()
            }
            try {
                val content = historyFile.readText(Charsets.UTF_8).trim()
                if (content.isEmpty() || content == "[]") {
                    return@withContext emptyList()
                }

                val jsonArray = JSONArray(content)
                val results = mutableListOf<InferenceResult>()
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    results.add(InferenceMapper.fromJsonObject(item))
                }
                // Ordenação cronológica decrescente
                results
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    /**
     * Salva uma nova sessão de inferência no histórico local.
     */
    suspend fun saveInference(result: InferenceResult, confidenceThreshold: Float = 0.25f) = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val currentList = if (historyFile.exists()) {
                    val content = historyFile.readText(Charsets.UTF_8).trim()
                    if (content.isNotEmpty()) {
                        val arr = JSONArray(content)
                        val list = mutableListOf<JSONObject>()
                        for (i in 0 until arr.length()) {
                            list.add(arr.getJSONObject(i))
                        }
                        list
                    } else mutableListOf()
                } else mutableListOf()

                // Converte o novo resultado em JSONObject
                val newJsonString = InferenceMapper.toJsonPayload(result, confidenceThreshold)
                val newJsonObj = JSONObject(newJsonString)

                // Insere no início
                currentList.add(0, newJsonObj)

                val outArray = JSONArray()
                currentList.forEach { outArray.put(it) }

                val tempFile = File(context.filesDir, "inferences_local_history.json.tmp")
                tempFile.writeText(outArray.toString(2), Charsets.UTF_8)
                if (tempFile.exists()) {
                    tempFile.renameTo(historyFile)
                }
            } catch (e: Exception) {
                // Silently handle IO exceptions without crashing
            }
        }
    }

    /**
     * Limpa completamente o histórico local.
     */
    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                if (historyFile.exists()) {
                    historyFile.delete()
                }
            } catch (e: Exception) {
                // Ignored
            }
        }
    }
}
