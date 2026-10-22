package com.example.fish_finder.presentation.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fish_finder.data.local.LocalInferenceStorage
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
 * Inicia com histórico limpo, persiste execuções locais e gerencia preferências de tema (Modo Escuro).
 */
class InferenceViewModel(
    application: Application,
    private val repository: InferenceRepository
) : AndroidViewModel(application) {

    // Construtor padrão utilizado pela factory de ViewModel do Android
    constructor(application: Application) : this(
        application = application,
        repository = InferenceRepositoryImpl(
            localStorage = LocalInferenceStorage(application.applicationContext)
        )
    )

    private val prefs = application.getSharedPreferences("fish_finder_preferences", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        InferenceUiState(
            isDarkMode = prefs.getBoolean("key_dark_mode", false)
        )
    )
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
                    currentInference = null // Garante início limpo (sem inferência pré-carregada)
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

            is InferenceUiAction.ImageSelected -> {
                _uiState.update {
                    it.copy(
                        isImageSelected = true,
                        selectedImageUri = action.uri,
                        statusMessage = null
                    )
                }
            }

            is InferenceUiAction.TakePhoto -> {
                // Abertura nativa da câmera é orquestrada pela View
            }

            is InferenceUiAction.SelectGallery -> {
                // Seleção da galeria é orquestrada pela View
            }

            is InferenceUiAction.ExecuteInference -> {
                if (!_uiState.value.isImageSelected || _uiState.value.selectedImageUri.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(statusMessage = "Por favor, tire uma foto ou selecione uma imagem da galeria antes de executar.")
                    }
                } else {
                    executeInferenceFlow()
                }
            }

            is InferenceUiAction.NewInference -> {
                _uiState.update {
                    it.copy(
                        currentInference = null,
                        isImageSelected = false,
                        selectedImageUri = null,
                        statusMessage = null
                    )
                }
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

            is InferenceUiAction.ToggleDarkMode -> {
                prefs.edit().putBoolean("key_dark_mode", action.isDark).apply()
                _uiState.update { it.copy(isDarkMode = action.isDark) }
            }

            is InferenceUiAction.ToggleDarkModeSwitch -> {
                val newDark = !_uiState.value.isDarkMode
                prefs.edit().putBoolean("key_dark_mode", newDark).apply()
                _uiState.update { it.copy(isDarkMode = newDark) }
            }

            is InferenceUiAction.ClearStatusMessage -> {
                _uiState.update { it.copy(statusMessage = null) }
            }
        }
    }

    private fun executeInferenceFlow() {
        if (_uiState.value.isInferring) return

        viewModelScope.launch {
            _uiState.update { it.copy(isInferring = true, statusMessage = null) }

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
    // Métodos de conveniência diretos
    // -------------------------------------------------------------------------

    fun onModelSelected(modelName: String) = onAction(InferenceUiAction.SelectModel(modelName))

    fun onCustomLabelsChanged(labels: String) = onAction(InferenceUiAction.ChangeCustomLabels(labels))

    fun onConfidenceThresholdChanged(threshold: Float) = onAction(InferenceUiAction.ChangeConfidenceThreshold(threshold))

    fun onImageSelected(uri: String) = onAction(InferenceUiAction.ImageSelected(uri))

    fun onTakePhotoClicked() = onAction(InferenceUiAction.TakePhoto)

    fun onSelectGalleryClicked() = onAction(InferenceUiAction.SelectGallery)

    fun onExecuteInference() = onAction(InferenceUiAction.ExecuteInference)

    fun onNewInferenceClicked() = onAction(InferenceUiAction.NewInference)

    fun onSearchQueryChanged(query: String) = onAction(InferenceUiAction.SearchQuery(query))

    fun onSelectSessionForDetail(sessionId: String) = onAction(InferenceUiAction.SelectSessionDetail(sessionId))

    fun onClearSelectedSession() = onAction(InferenceUiAction.ClearSelectedSession)

    fun onToggleExportInfoDialog(isOpen: Boolean) = onAction(InferenceUiAction.ToggleExportInfoDialog(isOpen))

    fun onToggleZoomDialog(isOpen: Boolean) = onAction(InferenceUiAction.ToggleZoomDialog(isOpen))

    fun onToggleDarkMode() = onAction(InferenceUiAction.ToggleDarkModeSwitch)
}
