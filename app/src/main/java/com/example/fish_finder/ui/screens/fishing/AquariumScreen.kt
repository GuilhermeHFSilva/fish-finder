package com.example.fish_finder.ui.screens.fishing

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.CaughtFish
import com.example.fish_finder.ui.components.fishing.OceanBackground
import com.example.fish_finder.ui.components.fishing.RarityChip
import com.example.fish_finder.ui.components.fishing.StatBars
import com.example.fish_finder.ui.components.fishing.color
import com.example.fish_finder.ui.theme.OceanDeep

/** Coleção dos peixes capturados, com a foto recortada e os atributos sorteados na captura. */
@Composable
fun AquariumScreen(collection: List<CaughtFish>, modifier: Modifier = Modifier) {
    OceanBackground(modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(Modifier.padding(top = 24.dp, bottom = 4.dp)) {
                    Text("Meu aquário", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${collection.size} capturados", color = Color.White.copy(alpha = 0.8f))
                }
            }
            if (collection.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Seu aquário está vazio. Vá pescar para encher!",
                        color = Color.White, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp)
                    )
                }
            }
            items(collection.asReversed(), key = { it.id }) { FishCard(it) }
        }
    }
}

@Composable
private fun FishCard(fish: CaughtFish) {
    // Recorte pequeno e salvo localmente; decodificado uma vez por card.
    val image = remember(fish.imagePath) { BitmapFactory.decodeFile(fish.imagePath)?.asImageBitmap() }
    val accent = fish.rarity.color()

    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, accent),
        colors = CardDefaults.cardColors(containerColor = OceanDeep.copy(alpha = 0.85f))
    ) {
        Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(14.dp)).padding(0.dp)) {
                if (image != null) {
                    Image(image, fish.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
            Text(fish.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            RarityChip(fish.rarity, Modifier.padding(vertical = 6.dp))
            StatBars(fish.stats, accent)
        }
    }
}
