package com.example.fish_finder.domain.game

import com.example.fish_finder.domain.model.CaughtFish
import com.example.fish_finder.domain.model.FishStats
import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.domain.model.Rarity
import kotlin.random.Random

/** Regras puras do jogo de captura (sorteios recebem o [Random] para serem testáveis). */
object FishingRules {

    private val adjectives = listOf("Veloz", "Brilhante", "Sombrio", "Dourado", "Espinhoso", "Sereno", "Faiscante", "Marujo")
    private val nouns = listOf("Tilápio", "Escamado", "Nadador", "Ondino", "Caudal", "Barbatana")

    fun rollRarity(rng: Random): Rarity {
        var roll = rng.nextInt(Rarity.entries.sumOf { it.weight })
        return Rarity.entries.first { roll -= it.weight; roll < 0 }
    }

    /** Maior confiança de detecção (peixe bem visível) facilita a captura. */
    fun catchChance(rarity: Rarity, net: Net, confidence: Float): Float =
        (rarity.baseCatchRate * net.multiplier * (0.6f + 0.4f * confidence)).coerceIn(0.05f, 0.95f)

    /** Chance de o peixe fugir após uma rede mal-sucedida. */
    fun fleeChance(rarity: Rarity): Float = 0.10f + 0.05f * rarity.ordinal

    fun newFish(id: String, imagePath: String, rarity: Rarity, now: Long, rng: Random): CaughtFish {
        val low = 10 + 15 * rarity.ordinal
        val high = 50 + 16 * rarity.ordinal
        fun stat() = rng.nextInt(low, high + 1).coerceAtMost(100)
        return CaughtFish(
            id = id,
            name = "${nouns.random(rng)} ${adjectives.random(rng)}",
            rarity = rarity,
            stats = FishStats(power = stat(), speed = stat(), stamina = stat(), size = stat()),
            caughtAt = now,
            imagePath = imagePath
        )
    }
}
