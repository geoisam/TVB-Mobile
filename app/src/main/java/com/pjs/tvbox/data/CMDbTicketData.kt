package com.pjs.tvbox.data

import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import com.pjs.tvbox.util.LunarUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.String

object CMDbTicketData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }


    suspend fun getCMDbTicket(date: String = LunarUtil.getYearMonthDay()): Pair<List<TicketInfo>, TicketSales> =
        runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "$CMDB_API/getDayData?date=$date",
                    headers = mapOf("Referer" to CMDB_HOME)
                )
            )

            if (response.status != 200) return@runCatching emptyList<TicketInfo>() to TicketSales()

            val root = when (val body = response.response) {
                is JsonElement -> body
                is String -> json.parseToJsonElement(body)
                else -> return@runCatching emptyList<TicketInfo>() to TicketSales()
            }

            val items = root.jsonObject["list"]?.jsonArray.orEmpty()
                .mapNotNull { it.jsonObject.toMovie() }

            val sales = root.jsonObject["nationalSales"]?.jsonObject.orEmpty()
            val info = TicketSales(
                salesDesc = sales["salesDesc"]?.jsonObject?.get("value")?.jsonPrimitive?.content
                    ?: "null",
                salesUnit = sales["salesDesc"]?.jsonObject?.get("unit")?.jsonPrimitive?.content
                    ?: "万",
                splitSalesDesc = sales["splitSalesDesc"]?.jsonObject?.get("value")?.jsonPrimitive?.content
                    ?: "null",
                splitSalesUnit = sales["splitSalesDesc"]?.jsonObject?.get("unit")?.jsonPrimitive?.content
                    ?: "万",
                updateTimestamp = sales["updateTimestamp"]?.jsonPrimitive?.content
                    ?: "1012281292000",
            )

            items to info
        }.getOrElse { emptyList<TicketInfo>() to TicketSales() }

    private fun JsonObject.toMovie(): TicketInfo? = runCatching {
        TicketInfo(
            id = this["code"]?.jsonPrimitive?.content.orEmpty(),
            name = this["name"]?.jsonPrimitive?.content.orEmpty(),
            onlineSalesRateDesc = this["onlineSalesRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            releaseDays = this["releaseDays"]?.jsonPrimitive?.intOrNull ?: 0,
            releaseDesc = this["releaseDesc"]?.jsonPrimitive?.content.orEmpty(),
            salesInWanDesc = this["salesInWanDesc"]?.jsonPrimitive?.content.orEmpty(),
            salesRateDesc = this["salesRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            seatRateDesc = this["seatRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            sessionRateDesc = this["sessionRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            splitOnlineSalesRateDesc = this["splitOnlineSalesRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            splitSalesInWanDesc = this["splitSalesInWanDesc"]?.jsonPrimitive?.content.orEmpty(),
            splitSalesRateDesc = this["splitSalesRateDesc"]?.jsonPrimitive?.content.orEmpty(),
            sumSalesDesc = this["sumSalesDesc"]?.jsonPrimitive?.content.orEmpty(),
            sumSplitSalesDesc = this["sumSplitSalesDesc"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}