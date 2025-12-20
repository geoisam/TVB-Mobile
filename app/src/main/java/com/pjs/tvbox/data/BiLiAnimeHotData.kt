package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object BiLiAnimeHotData {
    suspend fun getDataInfo(): List<AnimeInfo> {
        return runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$BILIBILI_API/pgc/web/rank/list?day=3&season_type=1",
                        headers = mapOf("Referer" to BILIBILI_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root: JsonObject =
                when (val body = response.response) {
                    is JsonElement -> body.jsonObject
                    is String -> JSON.parseToJsonElement(body).jsonObject
                    else -> return@runCatching emptyList()
                }

            val items = root["result"]?.jsonObject?.get("list")?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toAnimeHot()
            }

        }.getOrElse { emptyList() }
    }

    private fun JsonObject.toAnimeHot(): AnimeInfo? =
        runCatching {
            val thumbnailCover = this["cover"]?.jsonPrimitive?.content.orEmpty()
            val thumbnailUrl =
                if (thumbnailCover.contains("@")) {
                    thumbnailCover
                } else {
                    "${thumbnailCover}@200w_300h.webp"
                }

            AnimeInfo(
                id = this["season_id"]?.jsonPrimitive?.content.orEmpty(),
                title = this["title"]?.jsonPrimitive?.content.orEmpty(),
                subtitle = this["icon_font"]?.jsonObject?.get("text")?.jsonPrimitive?.content.orEmpty(),
                thumbnail = thumbnailUrl,
                cover = this["cover"]?.jsonPrimitive?.content.orEmpty(),
                rating = this["rating"]?.jsonPrimitive?.content?.replace("分", "").orEmpty(),
                view = this["new_ep"]?.jsonObject?.get("index_show")?.jsonPrimitive?.content.orEmpty(),
            )
        }.getOrNull()
}
