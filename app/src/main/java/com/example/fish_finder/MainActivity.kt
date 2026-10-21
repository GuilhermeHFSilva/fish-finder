package com.example.fish_finder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.fish_finder.presentation.viewmodel.InferenceUiState
import com.example.fish_finder.presentation.viewmodel.InferenceViewModel
import com.example.fish_finder.ui.navigation.AppNavigation
import com.example.fish_finder.ui.theme.Fish_finderTheme

class MainActivity : ComponentActivity() {

    private val viewModel: InferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Fish_finderTheme(darkTheme = false) {
                val state by viewModel.uiState.collectAsState()

                AppNavigation(
                    state = state,
                    onModelSelected = viewModel::onModelSelected,
                    onCustomLabelsChanged = viewModel::onCustomLabelsChanged,
                    onConfidenceThresholdChanged = viewModel::onConfidenceThresholdChanged,
                    onTakePhoto = viewModel::onTakePhotoClicked,
                    onSelectGallery = viewModel::onSelectGalleryClicked,
                    onExecuteInference = viewModel::onExecuteInference,
                    onNewInference = viewModel::onNewInferenceClicked,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onSelectSessionForDetail = viewModel::onSelectSessionForDetail,
                    onClearSelectedSession = viewModel::onClearSelectedSession,
                    onToggleExportInfoDialog = viewModel::onToggleExportInfoDialog,
                    onToggleZoomDialog = viewModel::onToggleZoomDialog
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppPreview() {
    Fish_finderTheme(darkTheme = false) {
        AppNavigation(
            state = InferenceUiState(),
            onModelSelected = {},
            onCustomLabelsChanged = {},
            onConfidenceThresholdChanged = {},
            onTakePhoto = {},
            onSelectGallery = {},
            onExecuteInference = {},
            onNewInference = {},
            onSearchQueryChanged = {},
            onSelectSessionForDetail = {},
            onClearSelectedSession = {},
            onToggleExportInfoDialog = {},
            onToggleZoomDialog = {}
        )
    }
}