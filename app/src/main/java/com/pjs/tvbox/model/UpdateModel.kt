package com.pjs.tvbox.model

data class Update(
    val versionName: String,
    val versionCode: String,
    val appName: String,
    val appSize: Long,
    val downloadUrl: String,
    val changeLog: String,
)