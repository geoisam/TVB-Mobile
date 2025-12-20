package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object CMDbYearData {
    suspend fun getDataInfo(year: Int = 1): List<TicketYear> {
        return runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$CMDB_API/getYearData?year=$year",
                        headers = mapOf("Referer" to CMDB_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root: JsonObject =
                when (val body = response.response) {
                    is JsonElement -> body.jsonObject
                    is String -> JSON.parseToJsonElement(body).jsonObject
                    else -> return@runCatching emptyList()
                }

            val items = root["rankList"]?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toTicket()
            }

        }.getOrElse { emptyList() }
    }

    private fun JsonObject.toTicket(): TicketYear? =
        runCatching {
            TicketYear(
                id = this["movieCode"]?.jsonPrimitive?.content.orEmpty(),
                name = this["name"]?.jsonPrimitive?.content.orEmpty(),
                premiereDate = this["premiereDate"]?.jsonPrimitive?.content.orEmpty(),
                salesInWan = this["salesInWan"]?.jsonPrimitive?.content.orEmpty(),
                avgPrice = this["avgPrice"]?.jsonPrimitive?.content.orEmpty(),
                avgSalesCount = this["avgSalesCount"]?.jsonPrimitive?.content.orEmpty(),
            )
        }.getOrNull()
}