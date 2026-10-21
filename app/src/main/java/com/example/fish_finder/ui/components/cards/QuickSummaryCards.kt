package com.example.fish_finder.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSecondary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Cards métricos de resumo rápido exibindo quantidade de peixes e confiança média (Tela 2).
 */
@Composable
fun QuickSummaryCards(
    totalObjects: Int,
    averageConfidence: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bloco 1: Total de objetos detectados (Fundo Azul de Destaque)
        Card(
            modifier = Modifier
                .weight(1f)
                .height(105.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "OBJETOS DETECTADOS",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = totalObjects.toString(),
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Tilápias classificadas",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }

        // Bloco 2: Grau de Confiança Médio
        Card(
            modifier = Modifier
                .weight(1f)
                .height(105.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppSurface),
            border = BorderStroke(1.dp, AppCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CONFIANÇA MÉDIA",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${(averageConfidence * 100).toInt()}%",
                    color = AppSecondary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Precisão ponderada",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
