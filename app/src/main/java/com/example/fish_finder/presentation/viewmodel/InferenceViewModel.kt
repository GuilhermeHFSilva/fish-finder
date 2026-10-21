package com.example.fish_finder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.InferenceResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Estado reativo da interface (UiState) seguindo Unidirectional Data Flow.
 */
data class InferenceUiState(
    val selectedModel: String = "tilapia.onnx (Modelo Embutido)",
    val customLabels: String = "tilapia, juvenis, alevinos",
    val confidenceThreshold: Float = 0.25f,
    val isInferring: Boolean = false,
    val isImageSelected: Boolean = true,
    val selectedImageUri: String? = "mock://camera/tilapia_tank_frame_01.jpg",
    val currentInference: InferenceResult? = null,
    val historyList: List<InferenceResult> = emptyList(),
    val searchQuery: String = "",
    val selectedSessionForDetail: InferenceResult? = null,
    val isExportInfoDialogOpen: Boolean = false,
    val isZoomDialogOpen: Boolean = false
) {
    /**
     * Lista filtrada de históricos com base na busca por data ou modelo.
     */
    val filteredHistory: List<InferenceResult>
        get() = if (searchQuery.isBlank()) {
            historyList
        } else {
            historyList.filter {
                it.modelName.contains(searchQuery, ignoreCase = true) ||
                it.timestamp.contains(searchQuery, ignoreCase = true) ||
                it.sessionId.contains(searchQuery, ignoreCase = true)
            }
        }

    /**
     * Total global de inferências realizadas.
     */
    val totalInferences: Int get() = historyList.size

    /**
     * Total acumulado de objetos contados em todas as sessões.
     */
    val totalObjectsCounted: Int get() = historyList.sumOf { it.totalObjects }

    /**
     * Média de tempo de inferência entre todas as sessões.
     */
    val averageExecutionTimeMs: Long
        get() = if (historyList.isNotEmpty()) {
            historyList.map { it.executionTimeMs }.average().toLong()
        } else {
            0L
        }
}

/**
 * ViewModel que gerencia a inferência on-device, dados mockados e estados de tela.
 */
class InferenceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(InferenceUiState())
    val uiState: StateFlow<InferenceUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val initialHistory = createRichMockHistory()
        _uiState.update { state ->
            state.copy(
                historyList = initialHistory,
                // Define uma inferência recente já visível ou pronta
                currentInference = initialHistory.firstOrNull()
            )
        }
    }

    fun onModelSelected(modelName: String) {
        _uiState.update { it.copy(selectedModel = modelName) }
    }

    fun onCustomLabelsChanged(labels: String) {
        _uiState.update { it.copy(customLabels = labels) }
    }

    fun onConfidenceThresholdChanged(threshold: Float) {
        _uiState.update { it.copy(confidenceThreshold = threshold) }
    }

    fun onTakePhotoClicked() {
        _uiState.update {
            it.copy(
                isImageSelected = true,
                selectedImageUri = "mock://camera/tilapia_tank_capture_${System.currentTimeMillis()}.jpg"
            )
        }
    }

    fun onSelectGalleryClicked() {
        _uiState.update {
            it.copy(
                isImageSelected = true,
                selectedImageUri = "mock://gallery/tanque_escavado_piscicultura.png"
            )
        }
    }

    fun onExecuteInference() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInferring = true) }

            // Simula latência real de processamento de rede neural via NPU/CPU local (600ms a 950ms)
            delay(850)

            val currentThreshold = _uiState.value.confidenceThreshold
            val newBoxes = generateRealisticBoxes(currentThreshold)

            val formatter = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss", Locale("pt", "BR"))
            val now = formatter.format(Date())
            val sessionSuffix = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

            val avgConf = if (newBoxes.isNotEmpty()) {
                newBoxes.map { it.confidence }.average().toFloat()
            } else {
                0.90f
            }

            val newResult = InferenceResult(
                sessionId = "sessao_$sessionSuffix",
                timestamp = now,
                modelName = _uiState.value.selectedModel.substringBefore(" "),
                totalObjects = newBoxes.size,
                averageConfidence = avgConf,
                executionTimeMs = Random.nextLong(820, 940),
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                isSyncedWithServer = true,
                boundingBoxes = newBoxes
            )

            _uiState.update { state ->
                state.copy(
                    isInferring = false,
                    currentInference = newResult,
                    historyList = listOf(newResult) + state.historyList
                )
            }
        }
    }

    fun onNewInferenceClicked() {
        _uiState.update { it.copy(currentInference = null) }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSelectSessionForDetail(sessionId: String) {
        val session = _uiState.value.historyList.find { it.sessionId == sessionId }
        _uiState.update { it.copy(selectedSessionForDetail = session) }
    }

    fun onClearSelectedSession() {
        _uiState.update { it.copy(selectedSessionForDetail = null) }
    }

    fun onToggleExportInfoDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isExportInfoDialogOpen = isOpen) }
    }

    fun onToggleZoomDialog(isOpen: Boolean) {
        _uiState.update { it.copy(isZoomDialogOpen = isOpen) }
    }

    // -------------------------------------------------------------------------
    // GERADORES DE DADOS MOCKADOS ULTRA-REALISTAS PARA AQUICULTURA (TILÁPIA)
    // -------------------------------------------------------------------------

    private fun generateRealisticBoxes(threshold: Float): List<BoundingBox> {
        val sampleBoxes = mutableListOf<BoundingBox>()
        val count = 51 // Contagem exata do wireframe da especificação
        var idCounter = 1

        val rng = Random(42) // Semente estável para consistência visual

        for (i in 0 until count) {
            val conf = 0.70f + rng.nextFloat() * 0.28f
            if (conf >= threshold) {
                val w = 110f + rng.nextFloat() * 70f
                val h = 40f + rng.nextFloat() * 35f
                val x = 80f + (rng.nextFloat() * (1920f - w - 160f))
                val y = 100f + (rng.nextFloat() * (1080f - h - 180f))

                sampleBoxes.add(
                    BoundingBox(
                        boxId = idCounter++,
                        classLabel = "tilapia",
                        confidence = conf,
                        xMin = x,
                        yMin = y,
                        widthPx = w,
                        heightPx = h
                    )
                )
            }
        }
        return sampleBoxes
    }

    private fun createRichMockHistory(): List<InferenceResult> {
        val s1Boxes = generateRealisticBoxes(0.25f)

        val s2Boxes = listOf(
            BoundingBox(1, "tilapia", 0.96f, 150f, 200f, 130f, 50f),
            BoundingBox(2, "tilapia", 0.94f, 320f, 280f, 125f, 48f),
            BoundingBox(3, "tilapia", 0.91f, 480f, 310f, 140f, 55f),
            BoundingBox(4, "tilapia", 0.89f, 650f, 190f, 120f, 46f),
            BoundingBox(5, "tilapia", 0.87f, 820f, 410f, 135f, 52f),
            BoundingBox(6, "tilapia", 0.93f, 980f, 350f, 128f, 49f),
            BoundingBox(7, "tilapia", 0.85f, 1150f, 260f, 122f, 47f),
            BoundingBox(8, "tilapia", 0.92f, 1320f, 480f, 138f, 53f)
        )

        val s3Boxes = listOf(
            BoundingBox(1, "tilapia", 0.95f, 200f, 220f, 126f, 48f),
            BoundingBox(2, "tilapia", 0.92f, 420f, 340f, 132f, 51f),
            BoundingBox(3, "tilapia", 0.90f, 680f, 290f, 118f, 45f),
            BoundingBox(4, "tilapia", 0.88f, 910f, 410f, 142f, 54f)
        )

        return listOf(
            InferenceResult(
                sessionId = "2026-09-18-001",
                timestamp = "18/09/2026 às 23:06:12",
                modelName = "best.onnx",
                totalObjects = 51,
                averageConfidence = 0.90f,
                executionTimeMs = 909,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                isSyncedWithServer = true,
                boundingBoxes = s1Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-18-002",
                timestamp = "18/09/2026 às 23:18:45",
                modelName = "tilapia.onnx",
                totalObjects = 38,
                averageConfidence = 0.91f,
                executionTimeMs = 884,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                isSyncedWithServer = true,
                boundingBoxes = s2Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-19-001",
                timestamp = "19/09/2026 às 08:42:10",
                modelName = "tilapia.onnx",
                totalObjects = 44,
                averageConfidence = 0.89f,
                executionTimeMs = 915,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                isSyncedWithServer = false,
                boundingBoxes = s3Boxes
            ),
            InferenceResult(
                sessionId = "2026-09-19-002",
                timestamp = "19/09/2026 às 14:15:33",
                modelName = "best.onnx",
                totalObjects = 29,
                averageConfidence = 0.93f,
                executionTimeMs = 870,
                imageWidthPx = 1920,
                imageHeightPx = 1080,
                isSyncedWithServer = true,
                boundingBoxes = s2Boxes.take(4)
            )
        )
    }
}
