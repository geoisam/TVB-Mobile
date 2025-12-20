package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

object DouBanRecData {
    suspend fun getDataInfo(start: Int = 0): List<MovieInfo> =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$DOUBAN_API/rexxar/api/v2/movie/suggestion?start=${start}&count=50&new_struct=1&with_review=1&for_mobile=1",
                        headers = mapOf("Referer" to DOUBAN_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root =
                when (val body = response.response) {
                    is JsonElement -> body
                    is String -> JSON.parseToJsonElement(body)
                    else -> return@runCatching emptyList()
                }

            val items = root.jsonObject["items"]?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toMovie()
            }

        }.getOrElse { emptyList() }

    private fun JsonObject.toMovie(): MovieInfo? =
        runCatching {
            MovieInfo(
                id = this["id"]?.jsonPrimitive?.content.orEmpty(),
                title = this["title"]?.jsonPrimitive?.content.orEmpty(),
                subtitle = this["card_subtitle"]?.jsonPrimitive?.content.orEmpty(),
                thumbnail = this["pic"]?.jsonObject?.get("normal")?.jsonPrimitive?.content.orEmpty(),
                cover = this["pic"]?.jsonObject?.get("large")?.jsonPrimitive?.content.orEmpty(),
                rating = this["rating"]?.jsonObject?.get("value")?.jsonPrimitive?.content.orEmpty(),
                view = this["rating"]?.jsonObject?.get("count")?.jsonPrimitive?.longOrNull,
            )
        }.getOrNull()
}