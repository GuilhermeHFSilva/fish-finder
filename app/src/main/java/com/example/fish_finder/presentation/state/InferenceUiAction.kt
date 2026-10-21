package com.example.fish_finder.presentation.state

/**
 * Intenções de UI (Ações do Usuário) modeladas de forma estrita para UDF (Unidirectional Data Flow).
 */
sealed interface InferenceUiAction {
    data class SelectModel(val modelName: String) : InferenceUiAction
    data class ChangeCustomLabels(val labels: String) : InferenceUiAction
    data class ChangeConfidenceThreshold(val threshold: Float) : InferenceUiAction
    data object TakePhoto : InferenceUiAction
    data object SelectGallery : InferenceUiAction
    data object ExecuteInference : InferenceUiAction
    data object NewInference : InferenceUiAction
    data class SearchQuery(val query: String) : InferenceUiAction
    data class SelectSessionDetail(val sessionId: String) : InferenceUiAction
    data object ClearSelectedSession : InferenceUiAction
    data class ToggleExportInfoDialog(val isOpen: Boolean) : InferenceUiAction
    data class ToggleZoomDialog(val isOpen: Boolean) : InferenceUiAction
}
