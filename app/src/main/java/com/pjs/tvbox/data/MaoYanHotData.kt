package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object MaoYanHotData {
    suspend fun getDataInfo(): List<SearchInfo> =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "https://piaofang.maoyan.com/dashboard/webHeatData?showDate=",
                        headers = mapOf("Referer" to DOUBAN_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root =
                when (val body = response.response) {
                    is JsonElement -> body
                    is String -> JSON.parseToJsonElement(body)
                    else -> return@runCatching emptyList()
                }

            val list = root.jsonObject["dataList"]?.jsonObject
                ?: return@runCatching emptyList()

            val items = list["list"]?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toMovie()
            }

        }.getOrElse { emptyList() }

    private fun JsonObject.toMovie(): SearchInfo? =
        runCatching {
            val info = this["seriesInfo"]?.jsonObject ?: return@runCatching null
            SearchInfo(
                id = info["seriesId"]?.jsonPrimitive?.content.orEmpty(),
                title = info["name"]?.jsonPrimitive?.content.orEmpty(),
                subtitle = info["platformDesc"]?.jsonPrimitive?.content.orEmpty(),
                daysDesc = info["releaseInfo"]?.jsonPrimitive?.content.orEmpty(),
                currHeat = this["currHeatDesc"]?.jsonPrimitive?.content.orEmpty(),
            )
        }.getOrNull()
}