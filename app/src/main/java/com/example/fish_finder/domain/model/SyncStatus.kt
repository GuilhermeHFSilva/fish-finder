package com.example.fish_finder.domain.model

/**
 * Representa o estado de sincronização e persistência de uma sessão de inferência
 * com o servidor backend (RF6 / RF8).
 */
enum class SyncStatus(val description: String) {
    SYNCED("Sincronizado com o Servidor"),
    PENDING("Pendente de Envio"),
    ERROR("Erro na Transmissão");

    val isSuccess: Boolean
        get() = this == SYNCED
}
