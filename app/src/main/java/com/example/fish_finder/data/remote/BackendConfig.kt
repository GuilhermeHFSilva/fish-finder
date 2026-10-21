package com.example.fish_finder.data.remote

/**
 * Configurações de conexão de rede com a API REST do Backend (RF6 / RF7).
 */
object BackendConfig {
    /**
     * IP padrão para emulador Android comunicando com localhost do host.
     * Caso teste em dispositivo físico via Wi-Fi, substitua pelo IP da máquina (ex: "http://192.168.0.14:3000/api").
     */
    const val DEFAULT_EMULATOR_BASE_URL = "http://10.0.2.2:3000/api"
    const val DEFAULT_LOCAL_DEVICE_BASE_URL = "http://192.168.0.14:3000/api"

    @Volatile
    var baseUrl: String = DEFAULT_EMULATOR_BASE_URL

    const val TIMEOUT_CONNECT_MS = 5000
    const val TIMEOUT_READ_MS = 5000

    val inferencesEndpoint: String
        get() = "$baseUrl/inferencias"
}
