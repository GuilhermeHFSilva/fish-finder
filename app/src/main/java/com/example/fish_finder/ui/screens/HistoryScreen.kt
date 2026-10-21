package com.example.fish_finder.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fish_finder.presentation.state.InferenceUiState
import com.example.fish_finder.ui.components.cards.GlobalStatsBanner
import com.example.fish_finder.ui.components.cards.HistorySessionCard
import com.example.fish_finder.ui.theme.AppCardBorder
import com.example.fish_finder.ui.theme.AppPrimary
import com.example.fish_finder.ui.theme.AppSurface
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
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cabeçalho
        item {
            Column {
                Text(
                    text = "Histórico de Inferências",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "• Sincronizado com o Servidor Backend",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusSuccess
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
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = AppPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
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

        // 5. Itens do Histórico
        if (state.filteredHistory.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nenhuma inferência encontrada para o filtro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
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
