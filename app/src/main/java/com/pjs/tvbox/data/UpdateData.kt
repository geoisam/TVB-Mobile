package com.pjs.tvbox.data

import android.content.Context
import android.os.Environment
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
import java.io.File
import kotlin.String

object UpdateData {
    private lateinit var appContext: Context

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    fun initContext(context: Context) {
        appContext = context.applicationContext
    }

    fun extractContent(input: String): String? {
        val body = input.replace(Regex("\\s+"), "")
        val regex = Regex("""<div(.*?)>(.*?)<(.*?)iv>""", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(body)
        return match?.groupValues?.get(2)?.takeIf { it.isNotBlank() }
    }

    private const val NOTE =
        "https://share.note.youdao.com/yws/api/note/d56e2e56e3434f73519e10dc3b831662?sev=j1&cstk=LnuyBs-w"
    private const val GITHUB = "https://api.github.com/repos/geoisam/TVB-Mobile/releases"

    suspend fun getUpdate(): Update? = runCatching {
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
        val encryptedText = extractContent(contentEscaped) ?: return@runCatching null
        val decryptedJson = CryptoUtil.decrypt(encryptedText) ?: return@runCatching null

        runCatching {
            val dir = appContext.getExternalFilesDir(null)
            val file = File(dir, "update.txt")
            if (dir == null) { return@runCatching }
            dir.mkdirs()
            file.writeText( decryptedJson, Charsets.UTF_8 )
        }

        val updateArray =
            json.parseToJsonElement(decryptedJson).jsonArray.takeIf { it.isNotEmpty() }
                ?: return@runCatching null
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
