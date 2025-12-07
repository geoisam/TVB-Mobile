package com.pjs.tvbox.data

import com.pjs.tvbox.model.AnimeHot
import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object BiliAnimeHotData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getAnimeHot(): List<AnimeHot> {
        return runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "https://api.bilibili.com/pgc/web/rank/list?day=3&season_type=1",
                    headers = mapOf("Referer" to "https://www.bilibili.com/")
                )
            )

            if (response.status != 200) return@runCatching emptyList()

            val rootJson: JsonObject = when (val body = response.response) {
                is JsonElement -> body.jsonObject
                is String -> json.parseToJsonElement(body).jsonObject
                else -> return@runCatching emptyList()
            }

            val items = rootJson["result"]?.jsonObject?.get("list")?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull { it.jsonObject.toAnimeHot() }

        }.getOrElse { emptyList() }
    }

    private fun JsonObject.toAnimeHot(): AnimeHot? = runCatching {
        AnimeHot(
            cover = this["cover"]?.jsonPrimitive?.content.orEmpty(),
            indexShow = this["new_ep"]?.jsonObject?.get("index_show")?.jsonPrimitive?.content.orEmpty(),
            rating = this["rating"]?.jsonPrimitive?.content?.replace("分", "").orEmpty(),
            epCover = this["ss_horizontal_cover"]?.jsonPrimitive?.content.orEmpty(),
            title = this["title"]?.jsonPrimitive?.content.orEmpty(),
            link = this["url"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}
