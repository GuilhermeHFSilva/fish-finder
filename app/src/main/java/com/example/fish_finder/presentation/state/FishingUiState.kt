package com.example.fish_finder.presentation.state

import android.graphics.Bitmap
import android.net.Uri
import com.example.fish_finder.domain.model.CaughtFish
import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.domain.model.Rarity

/** Peixe detectado, recortado da foto e pronto para ser capturado. */
class Encounter(
    val crop: Bitmap,
    val confidence: Float,
    val rarity: Rarity,
    val boxId: Int = 1
)

/** Resultado da varredura de uma foto: todos os peixes detectados, não só os da fila. */
data class ScanSummary(val totalFound: Int, val averageConfidence: Float) {
    val formattedConfidence: String get() = "${(averageConfidence * 100).toInt()}%"
}

/** Visualização da imagem com as bounding boxes antes do início das capturas individuais. */
data class DetectionOverview(
    val image: Bitmap,
    val boxes: List<com.example.fish_finder.domain.model.BoundingBox>
)

sealed interface ThrowResult {
    data object Missed : ThrowResult
    data object Fled : ThrowResult
    data class Caught(val fish: CaughtFish) : ThrowResult
}

data class FishingUiState(
    val isScanning: Boolean = false,
    val message: String? = null,
    val summary: ScanSummary? = null,
    val detectionOverview: DetectionOverview? = null,
    val encounters: List<Encounter> = emptyList(),
    val isThrowing: Boolean = false,
    val result: ThrowResult? = null,
    val collection: List<CaughtFish> = emptyList()
) {
    val currentEncounter: Encounter? get() = encounters.firstOrNull()
}

sealed interface FishingUiAction {
    data class ImagePicked(val uri: Uri) : FishingUiAction
    data class ThrowNet(val net: Net) : FishingUiAction
    data object NextEncounter : FishingUiAction
    data object StartEncounters : FishingUiAction
    data object DismissDetectionOverview : FishingUiAction
}

