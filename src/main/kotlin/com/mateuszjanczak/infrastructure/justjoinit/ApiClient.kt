package com.mateuszjanczak.infrastructure.justjoinit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

class ApiClient(private val httpClient: HttpClient, private val baseUrl: String) {

    companion object {
        const val CURSOR_SIZE = 100
        const val RELATIVE_URL = "/v2/user-panel/offers/by-cursor?page=%CURSOR%&perPage=%CURSOR_SIZE%"
        val REQUEST_HEADERS = mapOf("version" to "2")
    }

    fun getData(): List<JsonObject> {
        return generateSequence(0) { it + CURSOR_SIZE }
            .map { cursor -> buildUrl(cursor) }
            .map { url ->
                runBlocking<ApiResponse> {
                    httpClient.get(url) {
                        REQUEST_HEADERS.map { (key, value) ->
                            header(
                                key,
                                value
                            )
                        }
                    }.body()
                }
            }
            .takeWhile { it.data.isNotEmpty() }
            .flatMap { it.data }
            .toList()
    }

    private fun buildUrl(cursor: Int): String {
        return baseUrl + RELATIVE_URL.replace("%CURSOR%", "$cursor").replace("%CURSOR_SIZE%", "$CURSOR_SIZE")
    }
}

@Serializable
data class ApiResponse(
    val data: List<JsonObject>,
    val meta: JsonObject
)
