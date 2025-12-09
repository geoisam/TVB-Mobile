package com.pjs.tvbox.model

data class MaoYanHot(
    val currHeat: Float,
    val seriesId: Long,
    val name: String,
    val platformDesc: String,
    val releaseInfo: String,
)

data class MaoYanTime(
    val updateTimestamp: Long,
)