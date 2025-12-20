package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object HuanTvTopData {
    suspend fun getDataInfo(): List<HuanTvInfo> =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$HUANTV_API/tv-zone-user-system-prod/common/pc/real-time-performance-trend/all/json?sample_name=defult&duration=1&channel_type=all&city_group=all",
                        headers = mapOf("Referer" to HUANTV_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root =
                when (val body = response.response) {
                    is JsonElement -> body
                    is String -> JSON.parseToJsonElement(body)
                    else -> return@runCatching emptyList()
                }

            val items = root.jsonObject["data"]?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toHuanTv()
            }

        }.getOrElse { emptyList() }

    private fun JsonObject.toHuanTv(): HuanTvInfo? =
        runCatching {
            val entity = this["entity"]?.jsonObject ?: return@runCatching null
            HuanTvInfo(
                key = entity["key"]?.jsonPrimitive?.content.orEmpty(),
                channelName = entity["channel_name"]?.jsonPrimitive?.content.orEmpty(),
                onlineRate = entity["online_rate"]?.jsonPrimitive?.content.orEmpty(),
                channelLogo = entity["channel_logo_url"]?.jsonPrimitive?.content.orEmpty(),
                programName = entity["program_name"]?.jsonPrimitive?.content.orEmpty(),
                marketShare = entity["market_share"]?.jsonPrimitive?.content.orEmpty(),
                channelCode = entity["channel_code"]?.jsonPrimitive?.content.orEmpty(),
            )
        }.getOrNull()
}