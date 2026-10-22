package com.example.fish_finder.domain.game

import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.domain.model.Rarity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class FishingRulesTest {

    @Test
    fun redeMelhorEPeixeMaisRaroAlteramAChance() {
        val simples = FishingRules.catchChance(Rarity.COMUM, Net.SIMPLES, 0.8f)
        assertTrue(FishingRules.catchChance(Rarity.COMUM, Net.DOURADA, 0.8f) > simples)
        assertTrue(FishingRules.catchChance(Rarity.LENDARIO, Net.SIMPLES, 0.8f) < simples)
    }

    @Test
    fun chanceNaoPassaDoTetoDe95Porcento() {
        assertEquals(0.95f, FishingRules.catchChance(Rarity.COMUM, Net.DOURADA, 1f), 0.0001f)
        assertEquals(0.12f, FishingRules.catchChance(Rarity.LENDARIO, Net.SIMPLES, 0f), 0.0001f)
    }

    @Test
    fun atributosRespeitamAFaixaDaRaridade() {
        repeat(200) {
            val fish = FishingRules.newFish("id", "/x.png", Rarity.LENDARIO, 0L, Random(it))
            listOf(fish.stats.power, fish.stats.speed, fish.stats.stamina, fish.stats.size).forEach { v ->
                assertTrue(v in 55..100)
            }
        }
    }

    @Test
    fun todasAsRaridadesSaemNoSorteio() {
        val rng = Random(1)
        val seen = List(2000) { FishingRules.rollRarity(rng) }.toSet()
        assertEquals(Rarity.entries.toSet(), seen)
    }
}
