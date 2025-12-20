package com.pjs.tvbox.util

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object AppUtil {
    fun getVersionName(
        context: Context
    ): String {
        val appVersionName = getPackageInfo(context)?.versionName
        return appVersionName.toString()
    }

    fun getVersionCode(
        context: Context
    ): Long? {
        val appVersionCode = getPackageInfo(context)?.longVersionCode
        return appVersionCode
    }

    private fun getPackageInfo(
        context: Context
    ): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
