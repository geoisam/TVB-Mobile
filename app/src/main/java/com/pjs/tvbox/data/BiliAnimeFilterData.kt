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

object BiliAnimeFilterData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val cache = mutableMapOf<Pair<Int, Int>, List<AnimeHot>>()

    suspend fun getAnimeHot(order: Int = 0, page: Int = 1, sort: Int = 0): List<AnimeHot> {
        val cacheKey = order to page
        cache[cacheKey]?.let { return it }

        return runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "https://api.bilibili.com/pgc/season/index/result?st=1&order=${order}&season_version=-1&spoken_language_type=-1&area=-1&is_finish=-1&copyright=-1&season_status=-1&season_month=-1&year=-1&style_id=-1&sort=${sort}&page=${page}&season_type=1&pagesize=50&type=1",
                    headers = mapOf("Referer" to "https://www.bilibili.com/")
                )
            )

            if (response.status != 200) return@runCatching emptyList()

            val rootJson: JsonObject = when (val body = response.response) {
                is JsonElement -> body.jsonObject
                is String -> json.parseToJsonElement(body).jsonObject
                else -> return@runCatching emptyList()
            }

            val items = rootJson["data"]?.jsonObject?.get("list")?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull { it.jsonObject.toAnimeHot() }

        }.getOrElse { emptyList() }
    }

    private fun JsonObject.toAnimeHot(): AnimeHot? = runCatching {
        AnimeHot(
            cover = this["cover"]?.jsonPrimitive?.content.orEmpty(),
            epCover = this["first_ep"]?.jsonObject?.get("cover")?.jsonPrimitive?.content.orEmpty(),
            indexShow = this["index_show"]?.jsonPrimitive?.content.orEmpty(),
            link = this["link"]?.jsonPrimitive?.content.orEmpty(),
            rating = this["score"]?.jsonPrimitive?.content.orEmpty(),
            title = this["title"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}
