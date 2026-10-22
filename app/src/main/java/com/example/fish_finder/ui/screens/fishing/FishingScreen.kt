package com.example.fish_finder.ui.screens.fishing

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.fish_finder.presentation.state.FishingUiState
import com.example.fish_finder.ui.components.fishing.NetIcon
import com.example.fish_finder.ui.components.fishing.OceanBackground
import com.example.fish_finder.ui.theme.GoldAccent
import com.example.fish_finder.ui.theme.OceanDeep
import java.io.File

/** Tela inicial: o jogador fotografa (ou escolhe) uma imagem para procurar peixes. */
@Composable
fun FishingScreen(
    state: FishingUiState,
    onImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { taken ->
        if (taken) cameraUri?.let(onImage)
    }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(onImage)
    }

    OceanBackground(modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NetIcon(color = GoldAccent, size = 120.dp)
            Spacer(Modifier.height(16.dp))
            Text("Hora de pescar!", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Fotografe um tanque ou escolha uma imagem. Cada peixe encontrado vira um desafio de captura.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            if (state.isScanning) {
                CircularProgressIndicator(color = GoldAccent, modifier = Modifier.size(48.dp))
                Text("Procurando peixes…", color = Color.White, modifier = Modifier.padding(top = 12.dp))
            } else {
                Button(
                    onClick = {
                        val file = File(File(context.cacheDir, "photos").apply { mkdirs() }, "capture.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
                        cameraUri = uri
                        camera.launch(uri)
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent, contentColor = OceanDeep),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) { Text("Usar a câmera", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                OutlinedButton(
                    onClick = {
                        gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.fillMaxWidth().height(54.dp).padding(top = 10.dp)
                ) { Text("Escolher da galeria", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }

            state.message?.let {
                Text(it, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 20.dp))
            }
            Text(
                "Peixes no aquário: ${state.collection.size}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 28.dp)
            )
        }
    }
}
