package com.example.fish_finder.presentation.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fish_finder.data.detector.FishDetector
import com.example.fish_finder.data.storage.CollectionStore
import com.example.fish_finder.domain.game.FishingRules
import com.example.fish_finder.domain.model.BoundingBox
import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.presentation.state.DetectionOverview
import com.example.fish_finder.presentation.state.Encounter
import com.example.fish_finder.presentation.state.FishingUiAction
import com.example.fish_finder.presentation.state.FishingUiState
import com.example.fish_finder.presentation.state.ScanSummary
import com.example.fish_finder.presentation.state.ThrowResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.max
import kotlin.random.Random

/** Orquestra o ciclo do jogo: foto → detecção → visão geral das boxes → encontro → captura → aquário. */
class FishingViewModel(app: Application) : AndroidViewModel(app) {

    private val detectorDelegate = lazy { FishDetector(app) }
    private val detector by detectorDelegate
    private val store = CollectionStore(app.filesDir)
    private val rng = Random.Default

    private val _uiState = MutableStateFlow(FishingUiState())
    val uiState: StateFlow<FishingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val saved = withContext(Dispatchers.IO) { runCatching { store.load() }.getOrDefault(emptyList()) }
            _uiState.update { it.copy(collection = saved) }
        }
    }

    fun onAction(action: FishingUiAction) {
        when (action) {
            is FishingUiAction.ImagePicked -> scan(action.uri)
            is FishingUiAction.ThrowNet -> throwNet(action.net)
            FishingUiAction.NextEncounter -> _uiState.update {
                it.copy(encounters = it.encounters.drop(1), result = null, isThrowing = false)
            }
            FishingUiAction.StartEncounters -> _uiState.update {
                it.copy(detectionOverview = null)
            }
            FishingUiAction.DismissDetectionOverview -> _uiState.update {
                it.copy(detectionOverview = null, encounters = emptyList(), summary = null)
            }
        }
    }

    private data class InternalScanResult(
        val summary: ScanSummary,
        val boxes: List<BoundingBox>,
        val encounters: List<Encounter>,
        val bitmap: Bitmap
    )

    private fun scan(uri: Uri) {
        if (_uiState.value.isScanning) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(isScanning = true, message = null, summary = null, detectionOverview = null)
            }
            val scan = withContext(Dispatchers.Default) {
                runCatching {
                    val bitmap = decode(uri) ?: return@runCatching null
                    val boxes = detector.detect(bitmap)
                    val summary = ScanSummary(boxes.size, boxes.map { it.confidence }.average().toFloat())
                    val encounters = boxes.take(MAX_ENCOUNTERS)
                        .map { Encounter(crop(bitmap, it), it.confidence, FishingRules.rollRarity(rng), it.boxId) }
                    InternalScanResult(summary, boxes, encounters, bitmap)
                }.getOrNull()
            }
            val encounters = scan?.encounters
            val boxes = scan?.boxes.orEmpty()
            val bitmap = scan?.bitmap
            _uiState.update {
                it.copy(
                    isScanning = false,
                    encounters = encounters.orEmpty(),
                    summary = scan?.summary?.takeIf { s -> s.totalFound > 0 },
                    detectionOverview = if (bitmap != null && boxes.isNotEmpty()) {
                        DetectionOverview(image = bitmap, boxes = boxes)
                    } else null,
                    result = null,
                    message = when {
                        scan == null -> "Não foi possível abrir ou analisar a imagem."
                        boxes.isEmpty() -> "Nenhum peixe encontrado. Tente outra imagem."
                        else -> null
                    }
                )
            }
        }
    }

    private fun throwNet(net: Net) {
        val state = _uiState.value
        val encounter = state.currentEncounter ?: return
        if (state.isThrowing || state.result is ThrowResult.Caught || state.result is ThrowResult.Fled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isThrowing = true, result = null, message = null) }
            delay(THROW_DURATION_MS)

            val caught = rng.nextFloat() < FishingRules.catchChance(encounter.rarity, net, encounter.confidence)
            if (caught) {
                val id = UUID.randomUUID().toString()
                val fish = FishingRules.newFish(id, store.imagePathFor(id), encounter.rarity, System.currentTimeMillis(), rng)
                val saved = withContext(Dispatchers.IO) { runCatching { store.add(fish, encounter.crop) }.isSuccess }
                _uiState.update {
                    if (saved) it.copy(isThrowing = false, result = ThrowResult.Caught(fish), collection = it.collection + fish)
                    else it.copy(isThrowing = false, result = ThrowResult.Missed, message = "Não foi possível salvar a captura. Tente de novo.")
                }
            } else {
                val fled = rng.nextFloat() < FishingRules.fleeChance(encounter.rarity)
                _uiState.update { it.copy(isThrowing = false, result = if (fled) ThrowResult.Fled else ThrowResult.Missed) }
            }
        }
    }

    /** Decodifica reduzindo a imagem (lado maior ≤ 2048 px) e corrigindo a rotação EXIF. */
    private fun decode(uri: Uri): Bitmap? {
        val resolver = getApplication<Application>().contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE_PX) sample *= 2

        val bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: return null

        val orientation = resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        }
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(degrees) }, true)
    }

    /** Recorta a caixa com 10% de margem, limitada às bordas da imagem. */
    private fun crop(source: Bitmap, box: BoundingBox): Bitmap {
        val marginX = box.widthPx * 0.1f
        val marginY = box.heightPx * 0.1f
        val left = (box.xMin - marginX).toInt().coerceIn(0, source.width - 1)
        val top = (box.yMin - marginY).toInt().coerceIn(0, source.height - 1)
        val right = (box.xMin + box.widthPx + marginX).toInt().coerceIn(left + 1, source.width)
        val bottom = (box.yMin + box.heightPx + marginY).toInt().coerceIn(top + 1, source.height)
        return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
    }

    override fun onCleared() {
        if (detectorDelegate.isInitialized()) detector.close()
    }

    private companion object {
        const val MAX_ENCOUNTERS = 10
        const val MAX_SIDE_PX = 2048
        const val THROW_DURATION_MS = 1500L
    }
}
