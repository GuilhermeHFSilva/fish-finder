package com.example.fish_finder.ui.components.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppPrimaryLight
import com.example.fish_finder.ui.theme.StatusPending
import com.example.fish_finder.ui.theme.StatusPendingBg
import com.example.fish_finder.ui.theme.StatusSuccess
import com.example.fish_finder.ui.theme.StatusSuccessBg

/**
 * Badge para exibição de status operacional (ativo, sucesso, pendência).
 */
@Composable
fun StatusBadge(
    text: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSuccess) StatusSuccessBg else StatusPendingBg
    val contentColor = if (isSuccess) StatusSuccess else StatusPending

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(contentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default
            )
        }
    }
}

/**
 * Badge indicando o hardware local responsável pela inferência on-device (RF4).
 */
@Composable
fun ChipDeviceBadge(
    label: String = "Local NPU",
    modifier: Modifier = Modifier
) {
    Surface(
        color = AppPrimaryLight,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = AppIcons.Chip,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = AppPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
