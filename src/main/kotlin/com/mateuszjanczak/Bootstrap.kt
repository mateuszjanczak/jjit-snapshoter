package com.mateuszjanczak

import com.datastax.astra.sdk.AstraClient
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
    private val astraClient = AstraClient.builder()
        .withClientId(System.getenv("ASTRA_CLIENT_ID") ?: error("ASTRA_CLIENT_ID not set"))
        .withClientSecret(System.getenv("ASTRA_CLIENT_SECRET") ?: error("ASTRA_CLIENT_SECRET not set"))
        .withToken(System.getenv("ASTRA_TOKEN") ?: error("ASTRA_TOKEN not set"))
        .withDatabaseId(System.getenv("ASTRA_DATABASE_ID") ?: error("ASTRA_DATABASE_ID not set"))
        .withDatabaseRegion(System.getenv("ASTRA_DATABASE_REGION") ?: error("ASTRA_DATABASE_REGION not set"))
        .withCqlKeyspace(System.getenv("ASTRA_CQL_KEYSPACE") ?: error("ASTRA_CQL_KEYSPACE not set"))
        .enableCql()
        .build()

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    private val documentClient = DocumentClient(
        astraClient,
        System.getenv("ASTRA_NAMESPACE") ?: error("ASTRA_NAMESPACE not set"),
        System.getenv("ASTRA_COLLECTION") ?: error("ASTRA_COLLECTION not set")
    )

    private val apiClient = ApiClient(httpClient, System.getenv("API_BASE_URL") ?: error("API_BASE_URL not set"))

    val synchronizer = Synchronizer(apiClient, documentClient)
}