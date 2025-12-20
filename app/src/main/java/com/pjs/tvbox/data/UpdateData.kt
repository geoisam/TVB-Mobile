package com.pjs.tvbox.data

import android.content.Context
import com.pjs.tvbox.util.CryptoUtil
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.String

object UpdateData {
    fun extractContent(
        input: String
    ): String? {
        val body = input.replace(Regex("\\s+"), "")
        val regex = Regex("""<div(.*?)>(.*?)<(.*?)iv>""", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(body)
        return match?.groupValues?.get(2)?.takeIf { it.isNotBlank() }
    }

    suspend fun getUpdate(context: Context): UpdateInfo? =
        runCatching {
            val response =
                PJS.request(
                    PJSRequest(
                        url = NOTE,
                    )
                )

            if (response.status != 200) return@runCatching null

            val rootJson: JsonObject =
                when (val body = response.response) {
                    is JsonElement -> body.jsonObject
                    is String -> JSON.parseToJsonElement(body).jsonObject
                    else -> return@runCatching null
                }

            val contentEscaped = rootJson["content"]?.jsonPrimitive?.content
                ?: return@runCatching null

            val encryptedText = extractContent(contentEscaped)
                ?: return@runCatching null

            runCatching {
                val baseDir = context.getExternalFilesDir(null)
                    ?: return@runCatching

                val targetDir = File(baseDir, "logs")

                if (!targetDir.exists()) {
                    targetDir.mkdirs()
                }

                File(targetDir, "update.txt").writeText(encryptedText, Charsets.UTF_8)
            }

            val decryptedJson = CryptoUtil.decrypt(encryptedText)
                ?: return@runCatching null

            val updateArray = JSON.parseToJsonElement(decryptedJson).jsonArray.takeIf { it.isNotEmpty() }
                ?: return@runCatching null

            val latestJsonObject = updateArray.getOrNull(0)?.jsonObject
                ?: return@runCatching null

            latestJsonObject.toUpdate()

        }.getOrNull()

    private fun JsonObject.toUpdate(): UpdateInfo? =
        runCatching {
            val assetsArray = this["assets"]?.jsonArray
            val firstAsset = assetsArray?.firstOrNull()?.jsonObject

            UpdateInfo(
                versionName = this["tag_name"]?.jsonPrimitive?.content?.removePrefix("v").orEmpty(),
                appSize = firstAsset?.get("size")?.jsonPrimitive?.longOrNull,
                downloadUrl = firstAsset?.get("browser_download_url")?.jsonPrimitive?.content.orEmpty(),
                changeLog = this["body"]?.jsonPrimitive?.content.orEmpty(),
            )
        }.getOrNull()
}
