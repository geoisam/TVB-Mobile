package com.pjs.tvbox.data

import com.pjs.tvbox.model.TimelineAnime
import com.pjs.tvbox.model.TimelineDate
import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.String

object BiliTimelineData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getBiliTimeline(): List<TimelineDate> = runCatching {
        val response = PJS.request(
            PJSRequest(
                url = "https://api.bilibili.com/pgc/web/timeline?types=1",
                headers = mapOf("Referer" to "https://www.bilibili.com/")
            )
        )

        if (response.status != 200) return@runCatching emptyList()

        val rootJson: JsonObject = when (val body = response.response) {
            is JsonElement -> body.jsonObject
            is String -> json.parseToJsonElement(body).jsonObject
            else -> return@runCatching emptyList()
        }

        val items = rootJson["result"]?.jsonArray ?: return@runCatching emptyList()

        items.mapNotNull { it.jsonObject.toTimelineDate() }

    }.getOrElse { emptyList() }

    private fun JsonObject.toTimelineDate(): TimelineDate? = runCatching {
        TimelineDate(
            date = this["date"]?.jsonPrimitive?.content.orEmpty(),
            dayOfWeek = this["day_of_week"]?.jsonPrimitive?.intOrNull ?: 0,
            isToday = this["is_today"]?.jsonPrimitive?.intOrNull ?: 0,
            episodes = this["episodes"]?.jsonArray
                ?.mapNotNull { it.jsonObject.toTimelineAnime() }
                .orEmpty()
        )
    }.getOrNull()


    private fun JsonObject.toTimelineAnime(): TimelineAnime? = runCatching {
        TimelineAnime(
            seasonId = this["season_id"]?.jsonPrimitive?.longOrNull ?: 0L,
            episodeId = this["episode_id"]?.jsonPrimitive?.longOrNull ?: 0L,
            cover = this["cover"]?.jsonPrimitive?.content.orEmpty(),
            epCover = this["ep_cover"]?.jsonPrimitive?.content.orEmpty(),
            squareCover = this["square_cover"]?.jsonPrimitive?.content.orEmpty(),
            pubIndex = this["pub_index"]?.jsonPrimitive?.content.orEmpty(),
            pubTime = this["pub_time"]?.jsonPrimitive?.content.orEmpty(),
            published = this["published"]?.jsonPrimitive?.intOrNull ?: 0,
            title = this["title"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}