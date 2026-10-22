package com.example.fish_finder.presentation.state

import com.example.fish_finder.domain.model.InferenceResult
import com.example.fish_finder.domain.model.SyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InferenceUiStateTest {

    private val sampleHistory = listOf(
        InferenceResult(
            sessionId = "sessao_001",
            timestamp = "18/09/2026 às 10:00:00",
            modelName = "best.onnx",
            totalObjects = 10,
            averageConfidence = 0.90f,
            executionTimeMs = 800L,
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.SYNCED,
            boundingBoxes = emptyList()
        ),
        InferenceResult(
            sessionId = "sessao_002",
            timestamp = "19/09/2026 às 11:00:00",
            modelName = "tilapia.onnx",
            totalObjects = 20,
            averageConfidence = 0.92f,
            executionTimeMs = 900L,
            imageWidthPx = 1920,
            imageHeightPx = 1080,
            syncStatus = SyncStatus.PENDING,
            boundingBoxes = emptyList()
        )
    )

    @Test
    fun initialState_isCleanWithoutMockData() {
        val state = InferenceUiState()

        assertFalse("Não deve haver imagem pré-selecionada no início", state.isImageSelected)
        assertNull("Uri da imagem deve ser nulo no início", state.selectedImageUri)
        assertNull("Não deve haver inferência em andamento ou pré-calculada", state.currentInference)
        assertTrue("Histórico deve iniciar vazio e limpo", state.historyList.isEmpty())
        assertFalse("Modo escuro deve iniciar desligado por padrão", state.isDarkMode)
    }

    @Test
    fun darkMode_togglesCorrectly() {
        val state = InferenceUiState(isDarkMode = false)
        val darkState = state.copy(isDarkMode = true)

        assertTrue(darkState.isDarkMode)
    }

    @Test
    fun aggregateMetrics_calculateCorrectly() {
        val state = InferenceUiState(historyList = sampleHistory)

        assertEquals(2, state.totalInferences)
        assertEquals(30, state.totalObjectsCounted)
        assertEquals(850L, state.averageExecutionTimeMs)
    }

    @Test
    fun filterHistory_matchesModelOrDateOrSessionId() {
        val state = InferenceUiState(
            historyList = sampleHistory,
            searchQuery = "tilapia"
        )

        val filtered = state.filteredHistory
        assertEquals(1, filtered.size)
        assertEquals("sessao_002", filtered.first().sessionId)
    }

    @Test
    fun parseCustomLabels_splitsAndTrimsTokens() {
        val state = InferenceUiState(customLabels = " tilapia , juvenis ,  alevinos  ")
        val parsed = state.parsedLabels

        assertEquals(listOf("tilapia", "juvenis", "alevinos"), parsed)
    }
}
