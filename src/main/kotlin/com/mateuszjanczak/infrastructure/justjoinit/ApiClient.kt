package com.mateuszjanczak.infrastructure.justjoinit

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class ApiClient(
    private val httpClient: HttpClient,
    private val jsonParser: Json,
    private val baseUrl: String
) {

    companion object {
        const val CURSOR_SIZE = 100
        const val RELATIVE_URL = "/v2/user-panel/offers?page=%CURSOR%&perPage=%CURSOR_SIZE%"
        val REQUEST_HEADERS = mapOf("version" to "2")
        val LOG = KtorSimpleLogger(this::class.qualifiedName!!)
    }

    fun getData(): List<JobPosting> = try {
        generateSequence(1) { it + 1 }
            .map { cursor -> buildUrl(cursor) }
            .map { url ->
                runBlocking<ApiResponse> {
                    val response = httpClient.get(url) {
                        REQUEST_HEADERS.forEach { (key, value) ->
                            header(key, value)
                        }
                    }
                    jsonParser.decodeFromString(response.bodyAsText())
                }
            }
            .takeWhile { it.data.isNotEmpty() }
            .flatMap { it.data }
            .toList()
    } catch (e: Exception) {
        LOG.error(e.message)
        emptyList()
    }

    private fun buildUrl(cursor: Int): String =
        baseUrl + RELATIVE_URL.replace("%CURSOR%", "$cursor").replace("%CURSOR_SIZE%", "$CURSOR_SIZE")
}

@Serializable
data class ApiResponse(
    val data: List<JobPosting>,
    val meta: JsonObject
)

@Serializable
data class JobPosting(
    val slug: String,
    val title: String,
    val requiredSkills: List<String>,
    val workplaceType: String,
    val workingTime: String,
    val experienceLevel: String,
    val employmentTypes: List<EmploymentType>,
    val city: String,
    val street: String,
    val latitude: String,
    val longitude: String,
    val remoteInterview: Boolean,
    val companyName: String,
    val publishedAt: String,
    val syncTime: String? = null
)

@Serializable
data class EmploymentType(
    val to: Double?,
    val from: Double?,
    val type: String,
    val currency: String
)