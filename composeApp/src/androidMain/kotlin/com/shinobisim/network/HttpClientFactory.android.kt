package com.shinobisim.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    engine {
        config {
            retryOnConnectionFailure(true)
        }
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
}
