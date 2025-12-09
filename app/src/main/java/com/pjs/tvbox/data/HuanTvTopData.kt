package com.pjs.tvbox.data

import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class HuanTv(
    val channelName: String,
    val onlineRate: String,
    val channelLogo: String,
    val programName: String,
    val marketShare: String,
    val channelCode: String,
    val key: String,
)

object HuanTvTopData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getHuanTvHot(): List<HuanTv> =
        runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "https://tv-zone-api.huan.tv/tv-zone-user-system-prod/common/pc/real-time-performance-trend/all/json?sample_name=defult&duration=1&channel_type=all&city_group=all",
                    headers = mapOf("Referer" to "https://bigdata.huan.tv/")
                )
            )

            if (response.status != 200) return@runCatching emptyList()

            val root = when (val body = response.response) {
                is JsonElement -> body
                is String -> json.parseToJsonElement(body)
                else -> return@runCatching emptyList()
            }

            val items = root.jsonObject["data"]?.jsonArray ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toHuanTv()
            }
        }.getOrElse { emptyList() }

    private fun JsonObject.toHuanTv(): HuanTv? = runCatching {
        HuanTv(
            channelName = this["entity"]?.jsonObject?.get("channel_name")?.jsonPrimitive?.content.orEmpty(),
            onlineRate = this["entity"]?.jsonObject?.get("online_rate")?.jsonPrimitive?.content.orEmpty(),
            channelLogo = this["entity"]?.jsonObject?.get("channel_logo_url")?.jsonPrimitive?.content.orEmpty(),
            programName = this["entity"]?.jsonObject?.get("program_name")?.jsonPrimitive?.content.orEmpty(),
            marketShare = this["entity"]?.jsonObject?.get("market_share")?.jsonPrimitive?.content.orEmpty(),
            channelCode = this["entity"]?.jsonObject?.get("channel_code")?.jsonPrimitive?.content.orEmpty(),
            key = this["entity"]?.jsonObject?.get("key")?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}