package com.example.fish_finder.data.storage

import android.graphics.Bitmap
import com.example.fish_finder.domain.model.CaughtFish
import com.example.fish_finder.domain.model.FishStats
import com.example.fish_finder.domain.model.Rarity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Persiste o aquário em `collection.json` e o recorte de cada peixe em `fish_<id>.png`. */
class CollectionStore(private val dir: File) {

    private val index = File(dir, "collection.json")

    fun imagePathFor(id: String): String = File(dir, "fish_$id.png").absolutePath

    fun load(): List<CaughtFish> {
        if (!index.exists()) return emptyList()
        val array = JSONArray(index.readText())
        return List(array.length()) { array.getJSONObject(it).toFish() }
    }

    fun add(fish: CaughtFish, crop: Bitmap) {
        File(fish.imagePath).outputStream().use { crop.compress(Bitmap.CompressFormat.PNG, 100, it) }
        val array = JSONArray(load().plus(fish).map { it.toJson() })
        index.writeText(array.toString())
    }

    private fun CaughtFish.toJson() = JSONObject()
        .put("id", id).put("name", name).put("rarity", rarity.name).put("caughtAt", caughtAt)
        .put("imagePath", imagePath)
        .put("power", stats.power).put("speed", stats.speed)
        .put("stamina", stats.stamina).put("size", stats.size)

    private fun JSONObject.toFish() = CaughtFish(
        id = getString("id"),
        name = getString("name"),
        rarity = Rarity.valueOf(getString("rarity")),
        stats = FishStats(getInt("power"), getInt("speed"), getInt("stamina"), getInt("size")),
        caughtAt = getLong("caughtAt"),
        imagePath = getString("imagePath")
    )
}
