package com.mateuszjanczak.infrastructure.justjoinit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

class ApiClient(private val httpClient: HttpClient, private val baseUrl: String) {

    companion object {
        const val CURSOR_SIZE = 100
        const val RELATIVE_URL = "/v2/user-panel/offers?page=%CURSOR%&perPage=%CURSOR_SIZE%"
        val REQUEST_HEADERS = mapOf("version" to "2")
        val LOG = KtorSimpleLogger(this::class.qualifiedName!!)
    }

    fun getData(): List<JsonObject> {
        return try {
            generateSequence(1) { it + 1 }
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
        } catch (e: Exception) {
            LOG.error(e.message)
            emptyList()
        }
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
