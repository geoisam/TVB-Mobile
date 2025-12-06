package com.pjs.tvbox.data

import com.pjs.tvbox.model.Update
import com.pjs.tvbox.network.PJS
import com.pjs.tvbox.network.PJSRequest
import com.pjs.tvbox.util.CryptoUtil
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.apache.commons.text.StringEscapeUtils
import kotlin.String

object UpdateData {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    fun extractContent(input: String): String? {
        val html = StringEscapeUtils.unescapeJava(input)
        val pattern = Regex("""<div[^>]*>(.*?)</div>""", RegexOption.DOT_MATCHES_ALL)
        return pattern.find(html)?.groupValues?.get(1)
    }

    private const val NOTE = "https://share.note.youdao.com/yws/api/note/d56e2e56e3434f73519e10dc3b831662?sev=j1&cstk=LnuyBs-w"
    private const val GITHUB = "https://api.github.com/repos/geoisam/TVB-Mobile/releases"

    suspend fun getUpdate(): Update ?= runCatching {
            val response = PJS.request(
                PJSRequest(
                    url = NOTE,
                )
            )

            if (response.status != 200) return@runCatching null

            val rootJson: JsonObject = when (val body = response.response) {
                is JsonElement -> body.jsonObject
                is String -> json.parseToJsonElement(body).jsonObject
                else -> return@runCatching null
            }

            val contentEscaped = rootJson["content"]?.jsonPrimitive?.content ?: return@runCatching null
            val encryptedBase64 = extractContent(contentEscaped) ?: return@runCatching null
            val decryptedJson = CryptoUtil.decrypt(encryptedBase64)
            val updateArray = json.parseToJsonElement(decryptedJson).jsonArray
            val latestJsonObject = updateArray.getOrNull(0)?.jsonObject ?: return@runCatching null

            latestJsonObject.toUpdate()

    }.getOrNull()

    private fun JsonObject.toUpdate(): Update? = runCatching {
        val assetsArray = this["assets"]?.jsonArray
        val firstAsset = assetsArray?.firstOrNull()?.jsonObject

        Update(
            versionName = this["tag_name"]?.jsonPrimitive?.content?.removePrefix("v").orEmpty(),
            versionCode = this["name"]?.jsonPrimitive?.content.orEmpty(),
            appName = firstAsset?.get("name")?.jsonPrimitive?.content.orEmpty(),
            appSize = firstAsset?.get("size")?.jsonPrimitive?.longOrNull ?: 0L,
            downloadUrl = firstAsset?.get("browser_download_url")?.jsonPrimitive?.content.orEmpty(),
            changeLog = this["body"]?.jsonPrimitive?.content.orEmpty(),
        )
    }.getOrNull()
}
