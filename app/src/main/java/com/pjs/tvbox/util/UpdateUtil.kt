package com.pjs.tvbox.util

import com.pjs.tvbox.model.Update

object UpdateUtil {
    private var updateInfo: Update? = null
    private var hasReadUpdate = false

    fun currentUpdate(): Update? = updateInfo

    fun setUpdate(update: Update?) {
        updateInfo = update
        hasReadUpdate = false
    }

    fun clearUpdate() {
        updateInfo = null
        hasReadUpdate = false
    }
}