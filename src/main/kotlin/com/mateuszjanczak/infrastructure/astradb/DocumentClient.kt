package com.mateuszjanczak.infrastructure.astradb

import com.datastax.astra.sdk.AstraClient
import kotlinx.serialization.json.JsonObject

class DocumentClient(
    private val astraClient: AstraClient,
    private val namespace: String,
    private val collection: String
) {
    companion object {
        const val BATCH_SIZE = 100
    }

    private val collectionClient by lazy {
        astraClient.apiStargateDocument()
            .namespace(namespace)
            .collection(collection)
    }

    fun store(json: List<JsonObject>) {
        json.chunked(BATCH_SIZE).map { collectionClient.batchInsert(it) }
    }
}


