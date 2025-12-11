package com.pjs.tvbox.util

import com.pjs.tvbox.data.UpdateInfo

object UpdateUtil {
    private var updateInfo: UpdateInfo? = null
    private var hasReadUpdate = false

    fun currentUpdate(): UpdateInfo? = updateInfo

    fun setUpdate(update: UpdateInfo?) {
        updateInfo = update
        hasReadUpdate = false
    }

    fun clearUpdate() {
        updateInfo = null
        hasReadUpdate = false
    }
}