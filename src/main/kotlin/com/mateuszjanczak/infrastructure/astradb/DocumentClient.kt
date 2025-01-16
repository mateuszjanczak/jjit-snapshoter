package com.mateuszjanczak.infrastructure.astradb

import com.datastax.astra.client.Collection
import com.datastax.astra.client.model.Document
import kotlinx.serialization.json.JsonObject
import java.util.*

class DocumentClient(
    private val astraClient: Collection<Document>
) {
    companion object {
        const val BATCH_SIZE = 100
    }

    fun store(json: List<JsonObject>) {
        json.chunked(BATCH_SIZE).map { list ->
            list.map { item -> Document(item).id(UUID.randomUUID().toString()) }
        }.map { astraClient.insertMany(it) }
    }
}


