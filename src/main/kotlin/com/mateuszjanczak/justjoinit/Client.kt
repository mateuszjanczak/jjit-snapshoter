package com.mateuszjanczak.justjoinit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

class JjitApiClient(private val baseUrl: String) {

    companion object {
        const val CURSOR_SIZE = 100
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }

    }

    fun fetchData(): List<JsonObject> {
        return generateSequence(0) { it + CURSOR_SIZE }
            .map { cursor -> buildUrl(cursor) }
            .map { url -> runBlocking<ApiResponse> { client.get(url).body() } }
            .takeWhile { it.data.isNotEmpty() }
            .flatMap { it.data }
            .toList()
    }

    private fun buildUrl(cursor: Int): String {
        return "$baseUrl?from=$cursor&itemsCount=$CURSOR_SIZE"
    }
}

@Serializable
data class ApiResponse(
    val data: List<JsonObject>,
    val meta: JsonObject
)
