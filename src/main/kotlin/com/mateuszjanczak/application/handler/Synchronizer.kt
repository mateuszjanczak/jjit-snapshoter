package com.mateuszjanczak.application.handler

import com.mateuszjanczak.infrastructure.astradb.DocumentClient
import com.mateuszjanczak.infrastructure.justjoinit.ApiClient
import com.mateuszjanczak.infrastructure.justjoinit.JobPosting
import io.ktor.util.logging.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Synchronizer(
    private val apiClient: ApiClient,
    private val documentClient: DocumentClient
) {

    companion object {
        val LOG = KtorSimpleLogger(this::class.qualifiedName!!)
    }

    fun execute() {
        val currentTime = currentTime()
        val data = apiClient.getData()
            .map { it.withSyncTime(currentTime) }
            .also { LOG.info("Fetched ${it.size} items") }

        val jsonWithVectorize = data.map { Pair(it.toJsonObject(), it.toVectorize()) }
            .onEach { LOG.info(it.second) }

        documentClient.store(jsonWithVectorize)
            .also { LOG.info("Stored ${data.size} items") }
    }

    private fun currentTime() = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        .withZone(ZoneOffset.UTC)
        .format(Instant.now())

    private fun JobPosting.withSyncTime(syncTime: String) = this.copy(syncTime = syncTime)

    private fun JobPosting.toVectorize() =
        listOf(
            this.title,
            this.requiredSkills.reduce { a, b -> "$a $b" },
            this.workplaceType,
            this.workingTime,
            this.experienceLevel,
            this.employmentTypes.joinToString(" ") { "${it.from} ${it.to} ${it.type} ${it.currency}" },
            this.city,
            this.companyName,
            this.publishedAt
        ).reduce { a, b -> "$a $b" }

    private fun JobPosting.toJsonObject(): JsonObject =
        Json.decodeFromString(JsonObject.serializer(), Json.encodeToString(this))
}