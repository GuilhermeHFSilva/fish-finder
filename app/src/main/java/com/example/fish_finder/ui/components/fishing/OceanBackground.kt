package com.example.fish_finder.ui.components.fishing

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.fish_finder.ui.theme.OceanDeep
import com.example.fish_finder.ui.theme.OceanLight
import com.example.fish_finder.ui.theme.OceanMid
import kotlin.random.Random

private class Bubble(val x: Float, val radius: Float, val speed: Int, val offset: Float)

/** Fundo degradê de águas profundas com bolhas subindo. */
@Composable
fun OceanBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val bubbles = remember {
        val rng = Random(7)
        List(16) { Bubble(rng.nextFloat(), 6f + rng.nextFloat() * 22f, 1 + rng.nextInt(3), rng.nextFloat()) }
    }
    val progress by rememberInfiniteTransition(label = "bubbles").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Restart),
        label = "bubbles"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(OceanLight, OceanMid, OceanDeep)))
    ) {
        Canvas(Modifier.fillMaxSize()) {
            bubbles.forEach { b ->
                // speed é inteiro para o ciclo fechar sem salto quando progress volta a 0
                val y = 1f - (progress * b.speed + b.offset) % 1f
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = b.radius,
                    center = Offset(b.x * size.width, y * size.height),
                    style = Stroke(width = 2.5f)
                )
            }
        }
        content()
    }
}
