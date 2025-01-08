package com.mateuszjanczak.application.handler

import com.mateuszjanczak.infrastructure.astradb.DocumentClient
import com.mateuszjanczak.infrastructure.justjoinit.ApiClient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class Synchronizer(
    private val apiClient: ApiClient,
    private val documentClient: DocumentClient
) {

    fun execute() {
        val data = apiClient.getData().map { addTimestampToItem(it) }
        documentClient.store(data)
    }

    private fun addTimestampToItem(item: JsonObject): JsonObject {
        return JsonObject(
            item.toMap() + ("syncTime" to JsonPrimitive(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withZone(ZoneOffset.UTC)
                    .format(Instant.now())
            ))
        )
    }
}