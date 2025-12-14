package com.pjs.tvbox.network

import com.pjs.tvbox.data.UA_MOBILE
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

object PJS {
    private val json =
        Json {
            ignoreUnknownKeys =
                true
            coerceInputValues =
                true
            encodeDefaults =
                true
        }

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(
                15,
                TimeUnit.SECONDS
            )
            .readTimeout(
                30,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )
            .build()

    suspend fun request(
        details: PJSRequest
    ): PJSResponse =
        suspendCancellableCoroutine { cont ->
            val requestBody =
                details.data.toRequestBody(
                    details.headers
                )

            val headers =
                buildMap {
                    putAll(
                        details.headers.orEmpty()
                    )
                    put(
                        "User-Agent",
                        details.userAgent
                            ?: UA_MOBILE
                    )
                    details.cookie?.let {
                        put(
                            "Cookie",
                            it
                        )
                    }
                }

            val request =
                Request.Builder()
                    .url(
                        details.url
                    )
                    .headers(
                        headers.toHeaders()
                    )
                    .method(
                        details.method.uppercase(),
                        requestBody?.takeIf {
                            details.method.uppercase() !in setOf(
                                "GET",
                                "HEAD"
                            )
                        })
                    .build()

            val call =
                details.timeout?.let {
                    client.newBuilder()
                        .callTimeout(
                            it.toLong(),
                            TimeUnit.MILLISECONDS
                        )
                        .build()
                        .newCall(
                            request
                        )
                }
                    ?: client.newCall(
                        request
                    )

            cont.invokeOnCancellation { call.cancel() }

            call.enqueue(
                object :
                    Callback {
                    override fun onFailure(
                        call: Call,
                        e: IOException
                    ) {
                        cont.resume(
                            PJSResponse(
                                0,
                                e.message
                                    ?: "Network error",
                                responseText = "",
                                response = null
                            )
                        )
                    }

                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {
                        response.use { resp ->
                            val bodyBytes =
                                resp.body.byteString()
                            val bodyString =
                                bodyBytes.utf8()

                            val parsed =
                                try {
                                    when (details.responseType?.lowercase()) {
                                        "", "text", null -> bodyString
                                        "json" -> if (bodyString.isBlank()) null else json.decodeFromString<Any>(
                                            bodyString
                                        )

                                        "arraybuffer", "blob" -> bodyBytes.toByteArray()
                                        else -> bodyString
                                    }
                                } catch (_: Exception) {
                                    null
                                }

                            cont.resume(
                                PJSResponse(
                                    status = resp.code,
                                    statusText = resp.message.ifEmpty { "OK" },
                                    responseHeaders = resp.headers.toMultimap(),
                                    responseText = bodyString,
                                    response = parsed
                                )
                            )
                        }
                    }
                })
        }

    private fun Any?.toRequestBody(
        extraHeaders: Map<String, String>?
    ): RequestBody? =
        when (this) {
            null -> "".toRequestBody(
                "application/json".toMediaType()
            )

            is String -> toRequestBody(
                "text/plain".toMediaType()
            )

            is ByteArray -> toRequestBody(
                "application/octet-stream".toMediaType()
            )

            is File -> asRequestBody(
                "application/octet-stream".toMediaType()
            )

            is RequestBody -> this
            is Map<*, *> -> FormBody.Builder()
                .apply {
                    this@toRequestBody.forEach { (k, v) ->
                        add(
                            k.toString(),
                            v.toString()
                        )
                    }
                }
                .build()

            is Collection<*> -> MultipartBody.Builder()
                .setType(
                    MultipartBody.FORM
                )
                .apply {
                    this@toRequestBody.forEachIndexed { i, item ->
                        when (item) {
                            is File -> addFormDataPart(
                                "file$i",
                                item.name,
                                item.asRequestBody()
                            )

                            is ByteArray -> addFormDataPart(
                                "file$i",
                                "file$i.bin",
                                item.toRequestBody(
                                    "application/octet-stream".toMediaType()
                                )
                            )

                            else -> addFormDataPart(
                                "field$i",
                                item.toString()
                            )
                        }
                    }
                }
                .build()

            is Array<*> -> this.toList()
                .toRequestBody(
                    extraHeaders
                )

            else -> json.encodeToString(
                this
            )
                .toRequestBody(
                    "application/json".toMediaType()
                )
        }
}

data class PJSRequest(
    val method: String = "GET",
    val url: String,
    val headers: Map<String, String>? = null,
    val data: Any? = null,
    val cookie: String? = null,
    val userAgent: String? = null,
    val responseType: String? = null,
    val timeout: Int? = null
)

data class PJSResponse(
    val status: Int,
    val statusText: String,
    val responseHeaders: Map<String, List<String>> = emptyMap(),
    val responseText: String,
    val response: Any?
)