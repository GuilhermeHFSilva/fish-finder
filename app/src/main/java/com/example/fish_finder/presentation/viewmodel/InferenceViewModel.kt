package com.example.fish_finder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fish_finder.data.repository.InferenceRepositoryImpl
import com.example.fish_finder.domain.repository.InferenceRepository
import com.example.fish_finder.presentation.state.InferenceUiAction
import com.example.fish_finder.presentation.state.InferenceUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel que gerencia a inferência on-device e orquestra o estado da UI (MVVM Clean Architecture).
 */
class InferenceViewModel(
    private val repository: InferenceRepository = InferenceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(InferenceUiState())
    val uiState: StateFlow<InferenceUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val history = repository.getInitialHistory()
            _uiState.update { state ->
                state.copy(
                    historyList = history,
                    currentInference = history.firstOrNull()
                )
            }
        }
    }

    /**
     * Ponto central de despacho de intenções da interface (UDF / MVI pattern).
     */
    fun onAction(action: InferenceUiAction) {
        when (action) {
            is InferenceUiAction.SelectModel -> {
                _uiState.update { it.copy(selectedModel = action.modelName) }
            }

            is InferenceUiAction.ChangeCustomLabels -> {
                _uiState.update { it.copy(customLabels = action.labels) }
            }

            is InferenceUiAction.ChangeConfidenceThreshold -> {
                _uiState.update { it.copy(confidenceThreshold = action.threshold) }
            }

            is InferenceUiAction.TakePhoto -> {
                _uiState.update {
                    it.copy(
                        isImageSelected = true,
                        selectedImageUri = "mock://camera/tilapia_tank_capture_${System.currentTimeMillis()}.jpg"
                    )
                }
            }

            is InferenceUiAction.SelectGallery -> {
                _uiState.update {
                    it.copy(
                        isImageSelected = true,
                        selectedImageUri = "mock://gallery/tanque_escavado_piscicultura.png"
                    )
                }
            }

            is InferenceUiAction.ExecuteInference -> {
                executeInferenceFlow()
            }

            is InferenceUiAction.NewInference -> {
                _uiState.update { it.copy(currentInference = null) }
            }

            is InferenceUiAction.SearchQuery -> {
                _uiState.update { it.copy(searchQuery = action.query) }
            }

            is InferenceUiAction.SelectSessionDetail -> {
                val session = _uiState.value.historyList.find { it.sessionId == action.sessionId }
                _uiState.update { it.copy(selectedSessionForDetail = session) }
            }

            is InferenceUiAction.ClearSelectedSession -> {
                _uiState.update { it.copy(selectedSessionForDetail = null) }
            }

            is InferenceUiAction.ToggleExportInfoDialog -> {
                _uiState.update { it.copy(isExportInfoDialogOpen = action.isOpen) }
            }

            is InferenceUiAction.ToggleZoomDialog -> {
                _uiState.update { it.copy(isZoomDialogOpen = action.isOpen) }
            }
        }
    }

    private fun executeInferenceFlow() {
        if (_uiState.value.isInferring) return

        viewModelScope.launch {
            _uiState.update { it.copy(isInferring = true) }

            val currentState = _uiState.value
            val result = repository.executeInference(
                modelName = currentState.selectedModel,
                confidenceThreshold = currentState.confidenceThreshold,
                customLabels = currentState.parsedLabels,
                imageUri = currentState.selectedImageUri
            )

            _uiState.update { state ->
                state.copy(
                    isInferring = false,
                    currentInference = result,
                    historyList = listOf(result) + state.historyList
                )
            }
        }
    }

    // -------------------------------------------------------------------------
    // Métodos de conveniência diretos para manter compatibilidade com Callbacks
    // -------------------------------------------------------------------------

    fun onModelSelected(modelName: String) = onAction(InferenceUiAction.SelectModel(modelName))

    fun onCustomLabelsChanged(labels: String) = onAction(InferenceUiAction.ChangeCustomLabels(labels))

    fun onConfidenceThresholdChanged(threshold: Float) = onAction(InferenceUiAction.ChangeConfidenceThreshold(threshold))

    fun onTakePhotoClicked() = onAction(InferenceUiAction.TakePhoto)

    fun onSelectGalleryClicked() = onAction(InferenceUiAction.SelectGallery)

    fun onExecuteInference() = onAction(InferenceUiAction.ExecuteInference)

    fun onNewInferenceClicked() = onAction(InferenceUiAction.NewInference)

    fun onSearchQueryChanged(query: String) = onAction(InferenceUiAction.SearchQuery(query))

    fun onSelectSessionForDetail(sessionId: String) = onAction(InferenceUiAction.SelectSessionDetail(sessionId))

    fun onClearSelectedSession() = onAction(InferenceUiAction.ClearSelectedSession)

    fun onToggleExportInfoDialog(isOpen: Boolean) = onAction(InferenceUiAction.ToggleExportInfoDialog(isOpen))

    fun onToggleZoomDialog(isOpen: Boolean) = onAction(InferenceUiAction.ToggleZoomDialog(isOpen))
}
