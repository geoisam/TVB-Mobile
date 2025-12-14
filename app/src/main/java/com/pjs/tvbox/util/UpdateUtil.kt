package com.pjs.tvbox.util

import android.content.Context
import androidx.core.content.edit
import com.pjs.tvbox.data.GITHUB_RELEASE
import com.pjs.tvbox.data.UpdateData
import com.pjs.tvbox.data.UpdateInfo

object UpdateUtil {

    private const val PREF_NAME = "update_cache"
    private const val KEY_VERSION = "version_name"
    private const val KEY_URL = "download_url"
    private const val KEY_LOG = "change_log"
    private const val KEY_SIZE = "app_size"

    @Volatile
    private var cachedUpdate: UpdateInfo? = null

    fun init(context: Context) {
        val cached = readCache(context)
        cachedUpdate =
            if (cached != null && isRemoteNewer(context, cached)) cached
            else null
    }

    suspend fun checkUpdate(context: Context): Boolean {
        val remote = runCatching {
            UpdateData.getUpdate(context)
        }.getOrNull()

        return if (remote != null && isRemoteNewer(context, remote)) {
            cachedUpdate = remote
            saveCache(context, remote)
            true
        } else {
            clear(context)
            false
        }
    }

    fun hasNewVersion(): Boolean = cachedUpdate != null

    fun getUpdateInfo(): UpdateInfo? = cachedUpdate

    fun clear(context: Context) {
        cachedUpdate = null
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit { clear() }
    }

    private fun isRemoteNewer(
        context: Context,
        update: UpdateInfo
    ): Boolean {
        val localVersion = AppUtil.getVersionName(context)
        val remoteVersion = update.versionName ?: return false
        return isVersionNewer(remoteVersion, localVersion)
    }

    private fun isVersionNewer(
        remote: String,
        local: String
    ): Boolean {
        val r = remote.split(".").map { it.toIntOrNull() ?: 0 }
        val l = local.split(".").map { it.toIntOrNull() ?: 0 }
        val max = maxOf(r.size, l.size)

        for (i in 0 until max) {
            val rv = r.getOrElse(i) { 0 }
            val lv = l.getOrElse(i) { 0 }
            if (rv != lv) return rv > lv
        }
        return false
    }

    private fun saveCache(
        context: Context,
        update: UpdateInfo
    ) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_VERSION, update.versionName)
                putString(KEY_URL, update.downloadUrl)
                putString(KEY_LOG, update.changeLog)
                putLong(KEY_SIZE, update.appSize ?: 0L)
            }
    }

    private fun readCache(context: Context): UpdateInfo? {
        val sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val version = sp.getString(KEY_VERSION, null) ?: return null

        return UpdateInfo(
            versionName = version,
            downloadUrl = sp.getString(KEY_URL, GITHUB_RELEASE),
            changeLog = sp.getString(KEY_LOG, "修复了一些已知问题"),
            appSize = sp.getLong(KEY_SIZE, 0L)
        )
    }
}
