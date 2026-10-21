package com.example.fish_finder.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.ui.theme.AppPrimary

/**
 * Banner analítico com métricas agregadas da base de inferências (Tela 3).
 */
@Composable
fun GlobalStatsBanner(
    totalInferences: Int,
    totalObjects: Int,
    averageTimeMs: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatColumnItem(
                value = "$totalInferences",
                label = "INFERÊNCIAS",
                modifier = Modifier.weight(1f)
            )

            // Divisor vertical sutil
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            StatColumnItem(
                value = "$totalObjects",
                label = "OBJETOS CONTADOS",
                modifier = Modifier.weight(1.2f)
            )

            // Divisor vertical sutil
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(38.dp)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            StatColumnItem(
                value = "$averageTimeMs ms",
                label = "MÉDIA TEMPO",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatColumnItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
    }
}
