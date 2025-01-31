package com.mateuszjanczak.infrastructure.astradb

import com.datastax.astra.client.Collection
import com.datastax.astra.client.model.Document
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.util.*

class DocumentClient(
    private val astraClient: Collection<Document>
) {
    companion object {
        const val BATCH_SIZE = 100
    }

    fun store(jsonObjectList: List<Pair<JsonObject, String>>) {
        jsonObjectList.chunked(BATCH_SIZE).map { list ->
            list.map { (item, vectorize) ->
                Document
                    .parse(Json.encodeToString(JsonObject.serializer(), item))
                    .id(UUID.randomUUID().toString())
                    .vectorize(vectorize)
            }
        }.map { astraClient.insertMany(it) }
    }
}