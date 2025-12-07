package com.pjs.tvbox.data

import com.pjs.tvbox.model.NationalSales
import com.pjs.tvbox.model.Ticket
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

object CMDatabaseData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    suspend fun getCMDatabase(date: String = LunarUtil.getYearMonthDay()): Pair<List<Ticket>, NationalSales> =
        runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = "https://zgdypf.zgdypw.cn/getDayData?date=$date",
                    headers = mapOf("Referer" to "https://zgdypf.zgdypw.cn/")
                )
            )

            if (response.status != 200) return@runCatching emptyList<Ticket>() to NationalSales()

            val root = when (val body = response.response) {
                is JsonElement -> body
                is String -> json.parseToJsonElement(body)
                else -> return@runCatching emptyList<Ticket>() to NationalSales()
            }

            val tickets = root.jsonObject["list"]?.jsonArray.orEmpty()
                .mapNotNull { it.jsonObject.toMovie() }

            val natSale = root.jsonObject["nationalSales"]?.jsonObject
            val nationalSales = NationalSales(
                salesDesc = natSale?.get("salesDesc")?.jsonObject?.get("value")?.jsonPrimitive?.content.orEmpty(),
                salesUnit = natSale?.get("salesDesc")?.jsonObject?.get("unit")?.jsonPrimitive?.content.orEmpty(),
                splitSalesDesc = natSale?.get("splitSalesDesc")?.jsonObject?.get("value")?.jsonPrimitive?.content.orEmpty(),
                splitSalesUnit = natSale?.get("splitSalesDesc")?.jsonObject?.get("unit")?.jsonPrimitive?.content.orEmpty(),
                updateTimestamp = natSale?.get("updateTimestamp")?.jsonPrimitive?.content.orEmpty(),
            )

            tickets to nationalSales
        }.getOrElse { emptyList<Ticket>() to NationalSales() }

    private fun JsonObject.toMovie(): Ticket? = runCatching {
        Ticket(
            code = this["code"]!!.jsonPrimitive.content,
            name = this["name"]!!.jsonPrimitive.content,
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