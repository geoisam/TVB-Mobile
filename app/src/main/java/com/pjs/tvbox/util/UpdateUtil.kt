package com.pjs.tvbox.util

import com.pjs.tvbox.model.Update

object UpdateUtil {
    private var pendingUpdate: Update? = null

    fun setUpdate(update: Update) {
        pendingUpdate = update
    }

    fun consumeUpdate(): Update? {
        val update = pendingUpdate
        pendingUpdate = null
        return update
    }
}