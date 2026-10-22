package com.example.fish_finder.domain.model

enum class Rarity(val label: String, val weight: Int, val baseCatchRate: Float) {
    COMUM("Comum", 60, 0.65f),
    INCOMUM("Incomum", 25, 0.50f),
    RARO("Raro", 11, 0.35f),
    LENDARIO("Lendário", 4, 0.20f)
}

/** Redes de captura: quanto mais rara, maior o multiplicador sobre a chance base. */
enum class Net(val label: String, val multiplier: Float) {
    SIMPLES("Rede simples", 1.0f),
    REFORCADA("Rede reforçada", 1.4f),
    DOURADA("Rede dourada", 1.8f)
}

data class FishStats(val power: Int, val speed: Int, val stamina: Int, val size: Int)

/** Peixe capturado; os atributos são sorteados uma única vez, no momento da captura. */
data class CaughtFish(
    val id: String,
    val name: String,
    val rarity: Rarity,
    val stats: FishStats,
    val caughtAt: Long,
    val imagePath: String
)
