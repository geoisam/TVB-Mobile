package com.pjs.tvbox.data

import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object BiLiTimelineData {
    private val json =
        Json {
            ignoreUnknownKeys =
                true
            coerceInputValues =
                true
            isLenient =
                true
        }

    suspend fun getBiliTimeline(): List<TimelineDate> =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$BILIBILI_API/pgc/web/timeline?types=1&before=&after=",
                        headers = mapOf(
                            "Referer" to BILIBILI_HOME
                        )
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val rootJson: JsonObject =
                when (val body =
                    response.response) {
                    is JsonElement -> body.jsonObject
                    is String -> json.parseToJsonElement(
                        body
                    ).jsonObject

                    else -> return@runCatching emptyList()
                }

            val items =
                rootJson["result"]?.jsonArray
                    ?: return@runCatching emptyList()

            items.mapNotNull { it.jsonObject.toTimelineDate() }

        }.getOrElse { emptyList() }

    private fun JsonObject.toTimelineDate(): TimelineDate? =
        runCatching {
            TimelineDate(
                date = this["date"]?.jsonPrimitive?.content.orEmpty(),
                weekday = this["day_of_week"]?.jsonPrimitive?.intOrNull
                    ?: 0,
                isToday = this["is_today"]?.jsonPrimitive?.intOrNull
                    ?: 0,
                episodes = this["episodes"]?.jsonArray
                    ?.mapNotNull { it.jsonObject.toTimelineAnime() }
                    .orEmpty()
            )
        }.getOrNull()


    private fun JsonObject.toTimelineAnime(): TimelineInfo? =
        runCatching {
            val thumbnailCover =
                this["ep_cover"]?.jsonPrimitive?.content.orEmpty()
            val thumbnailUrl =
                if (thumbnailCover.contains(
                        "@"
                    )
                ) {
                    thumbnailCover
                } else {
                    "${thumbnailCover}@300w_200h.webp"
                }

            TimelineInfo(
                id = this["season_id"]?.jsonPrimitive?.content.orEmpty(),
                title = this["title"]?.jsonPrimitive?.content.orEmpty(),
                thumbnail = thumbnailUrl,
                coverV = this["cover"]?.jsonPrimitive?.content.orEmpty(),
                coverH = this["ep_cover"]?.jsonPrimitive?.content.orEmpty(),
                time = this["pub_time"]?.jsonPrimitive?.content.orEmpty(),
                view = this["pub_index"]?.jsonPrimitive?.content.orEmpty(),

                )
        }.getOrNull()
}