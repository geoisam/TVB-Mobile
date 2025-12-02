package com.pjs.tvbox.model

data class Ticket(
    val code: String,
    val name: String,
    val onlineSalesRateDesc: String,
    val releaseDays: Int,
    val releaseDesc: String? = null,
    val salesInWanDesc: String,
    val salesRateDesc: String,
    val seatRateDesc: String,
    val sessionRateDesc: String,
    val splitOnlineSalesRateDesc: String,
    val splitSalesInWanDesc: String,
    val splitSalesRateDesc: String,
    val sumSalesDesc: String,
    val sumSplitSalesDesc: String,
)

data class NationalSales(
    val salesDesc: String? = null,
    val salesUnit: String? = null,
    val splitSalesDesc: String? = null,
    val splitSalesUnit: String? = null,
    val updateTimestamp: String? = null,
)