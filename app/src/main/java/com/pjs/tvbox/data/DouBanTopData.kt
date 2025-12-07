package com.pjs.tvbox.data

import com.pjs.tvbox.model.Movie
import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object DouBanTopData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getTopMovies(): List<Movie> = runCatching {
        val response = PJS.request(
            PJSRequest(
                url = "https://m.douban.com/rexxar/api/v2/subject_collection/movie_top250/items?start=0&count=30&items_only=1&type_tag=&for_mobile=1",
                headers = mapOf("Referer" to "https://movie.douban.com/")
            )
        )

        if (response.status != 200) return@runCatching emptyList()

        val root = when (val body = response.response) {
            is JsonElement -> body
            is String -> json.parseToJsonElement(body)
            else -> return@runCatching emptyList()
        }

        val items =
            root.jsonObject["subject_collection_items"]?.jsonArray ?: return@runCatching emptyList()

        items.mapNotNull {
            it.jsonObject.toMovie()
        }
    }.getOrElse { emptyList() }

    private fun JsonObject.toMovie(): Movie? = runCatching {
        Movie(
            id = this["id"]!!.jsonPrimitive.content,
            title = this["title"]!!.jsonPrimitive.content,
            subtitle = this["card_subtitle"]?.jsonPrimitive?.content.orEmpty(),
            cover = this["pic"]?.jsonObject?.get("normal")?.jsonPrimitive?.content.orEmpty(),
            coverLarge = this["pic"]?.jsonObject?.get("large")?.jsonPrimitive?.content.orEmpty(),
            rating = this["rating"]?.jsonObject?.get("value")?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}