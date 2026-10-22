package com.example.fish_finder.ui.screens.fishing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.domain.game.FishingRules
import com.example.fish_finder.domain.model.Net
import com.example.fish_finder.presentation.state.Encounter
import com.example.fish_finder.presentation.state.ScanSummary
import com.example.fish_finder.presentation.state.ThrowResult
import com.example.fish_finder.ui.components.fishing.NetIcon
import com.example.fish_finder.ui.components.fishing.OceanBackground
import com.example.fish_finder.ui.components.fishing.RarityChip
import com.example.fish_finder.ui.components.fishing.StatBars
import com.example.fish_finder.ui.components.fishing.color
import com.example.fish_finder.ui.theme.GoldAccent
import com.example.fish_finder.ui.theme.OceanDeep

/** Tela cheia exibida quando um peixe é detectado: recorte do peixe + redes para capturá-lo. */
@Composable
fun EncounterScreen(
    encounter: Encounter,
    remaining: Int,
    summary: ScanSummary?,
    isThrowing: Boolean,
    result: ThrowResult?,
    onThrow: (Net) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onNext)

    val image = remember(encounter.crop) { encounter.crop.asImageBitmap() }
    val accent = encounter.rarity.color()
    val caught = result as? ThrowResult.Caught

    val fishScale by animateFloatAsState(if (isThrowing || caught != null) 0.6f else 1f, tween(500), label = "fishScale")
    val transition = rememberInfiniteTransition(label = "encounter")
    val wobble by transition.animateFloat(
        initialValue = -14f, targetValue = 14f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "wobble"
    )
    val ringAngle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "ring"
    )

    OceanBackground(modifier) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                when (result) {
                    is ThrowResult.Caught -> "Capturado!"
                    ThrowResult.Fled -> "O peixe escapou!"
                    else -> "Um peixe apareceu!"
                },
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold
            )
            Text(
                if (remaining > 1) "Peixe #${encounter.boxId} · Mais ${remaining - 1} na fila" else "Peixe #${encounter.boxId} · Último da foto",
                color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp
            )
            summary?.let {
                Text(
                    "${it.totalFound} no tanque · confiança média ${it.formattedConfidence}",
                    color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold
                )
            }
            RarityChip(encounter.rarity, Modifier.padding(top = 8.dp))

            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(300.dp)) {
                    drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.45f), Color.Transparent)))
                    rotate(ringAngle) {
                        drawCircle(
                            color = accent,
                            radius = size.minDimension / 2f - 8f,
                            style = Stroke(width = 6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(26f, 18f)))
                        )
                    }
                }
                Image(
                    bitmap = image,
                    contentDescription = "Peixe detectado",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(210.dp)
                        .scale(fishScale)
                        .clip(CircleShape)
                        .background(OceanDeep)
                )
                if (isThrowing || caught != null) {
                    NetIcon(
                        color = GoldAccent, size = 170.dp,
                        modifier = Modifier.rotate(if (isThrowing) wobble else 0f)
                    )
                }
            }

            when {
                caught != null -> {
                    Text(caught.fish.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    StatBars(caught.fish.stats, accent, Modifier.padding(vertical = 12.dp))
                    NextButton("Continuar", onNext)
                }
                result == ThrowResult.Fled -> NextButton("Continuar", onNext)
                else -> {
                    Text(
                        when {
                            isThrowing -> "Recolhendo a rede…"
                            result == ThrowResult.Missed -> "Escapou da rede! Tente de novo."
                            else -> "Escolha uma rede"
                        },
                        color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Net.entries.forEach { net ->
                            NetButton(net, FishingRules.catchChance(encounter.rarity, net, encounter.confidence), !isThrowing, onThrow, Modifier.weight(1f))
                        }
                    }
                    if (!isThrowing) {
                        Text(
                            "Deixar para lá", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp,
                            modifier = Modifier.padding(top = 14.dp).clickable(onClick = onNext)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NextButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep),
        modifier = Modifier.fillMaxWidth()
    ) { Text(label, fontWeight = FontWeight.Bold) }
}

@Composable
private fun NetButton(net: Net, chance: Float, enabled: Boolean, onThrow: (Net) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = if (enabled) 0.18f else 0.08f))
            .clickable(enabled = enabled) { onThrow(net) }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NetIcon(net.color(), 44.dp)
        Text(net.label, color = Color.White, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 4.dp))
        Text("${(chance * 100).toInt()}%", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
