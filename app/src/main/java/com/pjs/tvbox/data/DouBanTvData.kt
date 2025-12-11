package com.pjs.tvbox.data

import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object DouBanTvData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getDouBanTv(): List<AnimeInfo> = runCatching {
        val response = PJS.request(
            PJSRequest(
                url = "$DOUBAN_API/rexxar/api/v2/subject/recent_hot/tv?start=0&limit=50",
                headers = mapOf("Referer" to DOUBAN_HOME)
            )
        )

        if (response.status != 200) return@runCatching emptyList()

        val root = when (val body = response.response) {
            is JsonElement -> body
            is String -> json.parseToJsonElement(body)
            else -> return@runCatching emptyList()
        }

        val items = root.jsonObject["items"]?.jsonArray ?: return@runCatching emptyList()

        items.mapNotNull {
            it.jsonObject.toMovie()
        }
    }.getOrElse { emptyList() }

    private fun JsonObject.toMovie(): AnimeInfo? = runCatching {
        AnimeInfo(
            id = this["id"]?.jsonPrimitive?.content.orEmpty(),
            title = this["title"]?.jsonPrimitive?.content.orEmpty(),
            subtitle = this["card_subtitle"]?.jsonPrimitive?.content.orEmpty(),
            thumbnail = this["pic"]?.jsonObject?.get("normal")?.jsonPrimitive?.content.orEmpty(),
            cover = this["pic"]?.jsonObject?.get("large")?.jsonPrimitive?.content.orEmpty(),
            rating = this["rating"]?.jsonObject?.get("value")?.jsonPrimitive?.content.orEmpty(),
            view = this["episodes_info"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}