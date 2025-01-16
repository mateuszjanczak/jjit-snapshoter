package com.mateuszjanczak

import com.datastax.astra.client.DataAPIClient
import com.mateuszjanczak.application.handler.Synchronizer
import com.mateuszjanczak.infrastructure.astradb.DocumentClient
import com.mateuszjanczak.infrastructure.justjoinit.ApiClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*

fun Application.bootstrap() {
    Bootstrap
}

object Bootstrap {
    private val astraClient = DataAPIClient(System.getenv("ASTRA_TOKEN") ?: error("ASTRA_TOKEN not set"))
        .getDatabase(System.getenv("ASTRA_DATABASE") ?: error("ASTRA_DATABASE not set"))
        .getCollection(System.getenv("ASTRA_COLLECTION") ?: error("ASTRA_COLLECTION not set"))

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val documentClient = DocumentClient(
        astraClient
    )

    private val apiClient = ApiClient(httpClient, System.getenv("API_BASE_URL") ?: error("API_BASE_URL not set"))

    val synchronizer = Synchronizer(apiClient, documentClient)
}