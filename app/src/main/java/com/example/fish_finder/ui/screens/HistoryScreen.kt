package com.example.fish_finder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.presentation.state.InferenceUiState
import com.example.fish_finder.ui.components.AppIcons
import com.example.fish_finder.ui.components.cards.GlobalStatsBanner
import com.example.fish_finder.ui.components.cards.HistorySessionCard
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSurface
import com.example.fish_finder.ui.theme.AppSurfaceVariant
import com.example.fish_finder.ui.theme.StatusSuccess
import com.example.fish_finder.ui.theme.TextMuted
import com.example.fish_finder.ui.theme.TextPrimary
import com.example.fish_finder.ui.theme.TextSecondary

/**
 * Tela 3: Histórico de Inferências passadas com pesquisa e auditoria de sincronização (RF8).
 */
@Composable
fun HistoryScreen(
    state: InferenceUiState,
    onSearchQueryChanged: (String) -> Unit,
    onSelectSession: (String) -> Unit,
    onToggleDarkMode: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cabeçalho com Alternador de Modo Escuro
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Histórico de Inferências",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "• Sincronizado com Armazenamento Local",
                            style = MaterialTheme.typography.bodyMedium,
                            color = StatusSuccess
                        )
                    }
                }

                IconButton(
                    onClick = onToggleDarkMode,
                    modifier = Modifier
                        .size(40.dp)
                        .background(AppSurfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = if (state.isDarkMode) AppIcons.LightMode else AppIcons.DarkMode,
                        contentDescription = if (state.isDarkMode) "Ativar Modo Claro" else "Ativar Modo Escuro",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Banner de Estatísticas Globais
        item {
            GlobalStatsBanner(
                totalInferences = state.totalInferences,
                totalObjects = state.totalObjectsCounted,
                averageTimeMs = state.averageExecutionTimeMs
            )
        }

        // 3. Barra de Busca em Tempo Real
        item {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = "Filtrar por data ou modelo...",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Search,
                        contentDescription = "Buscar",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = AppIcons.Close,
                                contentDescription = "Limpar",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppSurface,
                    unfocusedContainerColor = AppSurface,
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = AppCardBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 4. Cabeçalho da Lista Cronológica
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registros Recentes (Toque para detalhes)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Text(
                    text = "${state.filteredHistory.size} registro(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // 5. Itens do Histórico ou Estado Vazio Limpo
        if (state.filteredHistory.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (state.searchQuery.isNotEmpty()) {
                                "Nenhum resultado para a busca"
                            } else {
                                "Histórico de inferências limpo"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (state.searchQuery.isNotEmpty()) {
                                "Tente buscar por outro termo ou limpe o campo de pesquisa."
                            } else {
                                "As inferências que você realizar na aba Detectar serão salvas e listadas aqui automaticamente."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(
                items = state.filteredHistory,
                key = { it.sessionId }
            ) { session ->
                HistorySessionCard(
                    session = session,
                    onClick = { onSelectSession(session.sessionId) }
                )
            }
        }
    }
}
