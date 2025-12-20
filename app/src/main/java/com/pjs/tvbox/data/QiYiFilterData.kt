package com.pjs.tvbox.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

object QiYiFilterData {
    suspend fun getDataInfo(order: Int = 1, page: Int = 1): List<MovieInfo> =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = "$IQIYI_API/views_category/3.0/com_sec_rank_list?app_k=205470066c13a1251fc6dda6781bfbe4&app_v=16.11.5&platform_id=1067&dev_os=15&dev_ua=MCE16&net_sts=1&qyid=fbaf262844281811632dc9c0be06a746110f&secure_v=1&secure_p=GPad&layout_v=60.2&page_st=0&tag=0&category_id=${order}&pg_num=${page}&web_pg_size=50",
                        headers = mapOf("Referer" to IQIYI_HOME)
                    )
                )

            if (response.status != 200) return@runCatching emptyList()

            val root =
                when (val body = response.response) {
                    is JsonElement -> body
                    is String -> JSON.parseToJsonElement(body)
                    else -> return@runCatching emptyList()
                }

            val list = root.jsonObject["cardNodeList"]?.jsonArray?.firstOrNull()
                ?: return@runCatching emptyList()

            val data = list.jsonObject["data"]?.jsonObject
                ?: return@runCatching emptyList()

            val items = data["content"]?.jsonArray
                ?: return@runCatching emptyList()

            items.mapNotNull {
                it.jsonObject.toMovie()
            }

        }.getOrElse { emptyList() }

    private fun JsonObject.toMovie(): MovieInfo? =
        runCatching {
            val rank = this["rank"]?.jsonObject ?: return@runCatching null
            MovieInfo(
                id = rank["id"]?.jsonPrimitive?.content.orEmpty(),
                title = rank["title"]?.jsonPrimitive?.content.orEmpty(),
                subtitle = rank["item_tags"]?.jsonPrimitive?.content.orEmpty(),
                thumbnail = rank["img_url"]?.jsonPrimitive?.content.orEmpty(),
                cover = rank["img_url"]?.jsonPrimitive?.content.orEmpty(),
                rating = rank["rec_index"]?.jsonPrimitive?.content.orEmpty(),
                view = rank["main_index"]?.jsonPrimitive?.longOrNull,
            )
        }.getOrNull()
}