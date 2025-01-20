package com.mateuszjanczak.application.handler

import com.mateuszjanczak.infrastructure.astradb.DocumentClient
import com.mateuszjanczak.infrastructure.justjoinit.ApiClient
import io.ktor.util.logging.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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
        val data = apiClient.getData().map { addTimestampToItem(it) }.also { LOG.info("Fetched ${it.size} items") }
        documentClient.store(data).also { LOG.info("Stored ${data.size} items") }
    }

    private fun addTimestampToItem(item: JsonObject): JsonObject {
        val syncTime = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        return JsonObject(item.toMap() + ("syncTime" to JsonPrimitive(syncTime)))
    }
}