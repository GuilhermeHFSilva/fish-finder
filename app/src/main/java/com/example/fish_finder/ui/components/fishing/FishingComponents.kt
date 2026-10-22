package com.example.fish_finder.ui.components.fishing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.model.FishStats
import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.domain.model.Rarity
import com.example.fish_finder.ui.theme.GoldAccent
import com.example.fish_finder.ui.theme.RarityCommon
import com.example.fish_finder.ui.theme.RarityLegendary
import com.example.fish_finder.ui.theme.RarityRare
import com.example.fish_finder.ui.theme.RarityUncommon

fun Rarity.color(): Color = when (this) {
    Rarity.COMUM -> RarityCommon
    Rarity.INCOMUM -> RarityUncommon
    Rarity.RARO -> RarityRare
    Rarity.LENDARIO -> RarityLegendary
}

fun Net.color(): Color = when (this) {
    Net.SIMPLES -> Color(0xFFB0BEC5)
    Net.REFORCADA -> Color(0xFF80DEEA)
    Net.DOURADA -> GoldAccent
}

@Composable
fun RarityChip(rarity: Rarity, modifier: Modifier = Modifier) {
    Text(
        text = rarity.label.uppercase(),
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(rarity.color(), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    )
}

/** Rede de malha circular desenhada no Canvas. */
@Composable
fun NetIcon(color: Color, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val r = this.size.minDimension / 2f - 4f
        val c = center
        val stroke = Stroke(width = 4f, cap = StrokeCap.Round)
        drawCircle(color, radius = r, center = c, style = stroke)
        for (i in -2..2) {
            val d = i * r / 3f
            val half = kotlin.math.sqrt((r * r - d * d).coerceAtLeast(0f))
            drawLine(color.copy(alpha = 0.8f), Offset(c.x - half, c.y + d), Offset(c.x + half, c.y + d), strokeWidth = 2.5f)
            drawLine(color.copy(alpha = 0.8f), Offset(c.x + d, c.y - half), Offset(c.x + d, c.y + half), strokeWidth = 2.5f)
        }
    }
}

@Composable
fun StatBars(stats: FishStats, accent: Color, modifier: Modifier = Modifier, labelColor: Color = Color.White) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        StatRow("Força", stats.power, accent, labelColor)
        StatRow("Velocidade", stats.speed, accent, labelColor)
        StatRow("Resistência", stats.stamina, accent, labelColor)
        StatRow("Tamanho", stats.size, accent, labelColor)
    }
}

@Composable
private fun StatRow(label: String, value: Int, accent: Color, labelColor: Color) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = labelColor, fontSize = 12.sp, modifier = Modifier.width(78.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            color = accent,
            trackColor = labelColor.copy(alpha = 0.2f),
            modifier = Modifier.weight(1f).size(width = 0.dp, height = 7.dp)
        )
        Text("$value", color = labelColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp).padding(start = 6.dp))
    }
}
