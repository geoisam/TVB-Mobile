package com.pjs.tvbox.data

import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object BiLiAnimeNewData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getBiLiAnimeNew(): List<AnimeInfo> {
        return runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "$BILIBILI_API/pgc/season/index/result?st=1&order=0&season_version=-1&spoken_language_type=-1&area=-1&is_finish=-1&copyright=-1&season_status=-1&season_month=-1&year=-1&style_id=-1&sort=0&page=1&season_type=1&pagesize=50&type=1",
                    headers = mapOf("Referer" to BILIBILI_HOME)
                )
            )

            if (response.status != 200) return@runCatching emptyList()

            val root: JsonObject = when (val body = response.response) {
                is JsonElement -> body.jsonObject
                is String -> json.parseToJsonElement(body).jsonObject
                else -> return@runCatching emptyList()
            }

            val items = root["data"]?.jsonObject?.get("list")?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull { it.jsonObject.toAnimeHot() }

        }.getOrElse { emptyList() }
    }

    private fun JsonObject.toAnimeHot(): AnimeInfo? = runCatching {
        val thumbnailCover = this["cover"]?.jsonPrimitive?.content.orEmpty()
        val thumbnailUrl = if (thumbnailCover.contains("@")) {
            thumbnailCover
        } else {
            "${thumbnailCover}@200w_300h.webp"
        }

        AnimeInfo(
            id = this["season_id"]?.jsonPrimitive?.content.orEmpty(),
            title = this["title"]?.jsonPrimitive?.content.orEmpty(),
            subtitle = this["subTitle"]?.jsonPrimitive?.content.orEmpty(),
            thumbnail = thumbnailUrl,
            cover = this["cover"]?.jsonPrimitive?.content.orEmpty(),
            rating = this["score"]?.jsonPrimitive?.content.orEmpty(),
            view = this["index_show"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}
