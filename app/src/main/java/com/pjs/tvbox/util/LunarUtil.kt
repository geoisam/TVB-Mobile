package com.pjs.tvbox.util

import com.nlf.calendar.Solar
import com.nlf.calendar.Lunar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

data class FestivalModel(
    val name: String,
    val date: LocalDate,
    val weekday: String,
    val daysLeft: Long,
)

object LunarUtil {
    private val formatter =
        SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.CHINA
        )

    fun String.toDateString(): String {
        return try {
            val timestamp =
                this.toLong()
            formatter.format(
                Date(
                    timestamp
                )
            )
        } catch (e: Exception) {
            getDateTime()
        }
    }

    fun getYearMonthDay(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val yearSolar =
            timeNow.year.toString()
                .padStart(
                    2,
                    '0'
                )
        val monthSolar =
            timeNow.monthValue.toString()
                .padStart(
                    2,
                    '0'
                )
        val daySolar =
            timeNow.dayOfMonth.toString()
                .padStart(
                    2,
                    '0'
                )
        return "${yearSolar}-${monthSolar}-${daySolar}"
    }

    fun getDateTime(): String {
        val yearMonthDay =
            getYearMonthDay()
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val hourSolar =
            timeNow.hour.toString()
                .padStart(
                    2,
                    '0'
                )
        val minuteSolar =
            timeNow.minute.toString()
                .padStart(
                    2,
                    '0'
                )
        val secondSolar =
            timeNow.second.toString()
                .padStart(
                    2,
                    '0'
                )
        return "$yearMonthDay ${hourSolar}:${minuteSolar}:${secondSolar}"
    }

    fun getYearMonth(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val yearSolar =
            timeNow.year
        val monthSolar =
            timeNow.monthValue
        return "$yearSolar 年 $monthSolar 月"
    }

    fun getDay(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        return timeNow.dayOfMonth.toString()
    }

    fun getWeek(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val solar =
            Solar.fromYmd(
                timeNow.year,
                timeNow.monthValue,
                timeNow.dayOfMonth,
            )
        return "星期${solar.weekInChinese}"
    }

    fun getMonthDay(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.monthInChinese}月${lunar.dayInChinese}"
    }

    fun getGanZhi(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.yearInGanZhi}${lunar.yearShengXiao}年 ${lunar.monthInGanZhi}${lunar.monthShengXiao}月 ${lunar.dayInGanZhi}${lunar.dayShengXiao}日"
    }

    fun getJieQi(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return lunar.jieQi
    }

    fun getDayYi(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        val yiString =
            lunar.dayYi?.joinToString(
                " "
            )
                ?: "无"
        return yiString
    }

    fun getDayJi(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        val jiString =
            lunar.dayJi?.joinToString(
                " "
            )
                ?: "无"
        return jiString
    }

    fun getDayChong(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.dayShengXiao}日冲${lunar.dayChongShengXiao}(${lunar.dayChongGan}${lunar.dayChong}) 煞${lunar.daySha}"
    }

    fun getPengZu(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.pengZuGan}\n${lunar.pengZuZhi}"
    }

    fun getDayXi(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.dayPositionXiDesc}(${lunar.dayPositionXi})"
    }

    fun getDayCai(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.dayPositionCaiDesc}(${lunar.dayPositionCai})"
    }

    fun getDayFu(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromDate(
                Date.from(
                    timeNow.toInstant()
                )
            )
        return "${lunar.dayPositionFuDesc}(${lunar.dayPositionFu})"
    }

    fun getShiChen(): String {
        val timeNow =
            ZonedDateTime.now(
                ZoneId.systemDefault()
            )
        val lunar =
            Lunar.fromYmdHms(
                timeNow.year,
                timeNow.monthValue,
                timeNow.dayOfMonth,
                timeNow.hour,
                timeNow.minute,
                timeNow.second
            )
        return "${lunar.timeZhi}时"
    }

    fun getNextFestival(
        maxDays: Int = 99
    ): List<FestivalModel> {
        val today =
            LocalDate.now()
        val zone =
            ZoneId.systemDefault()

        var nearestJieQi: FestivalModel? =
            null
        var nearestFestivalDay: Long? =
            null
        var nearestFestivals =
            mutableListOf<String>()

        for (i in 0 until maxDays) {
            val date =
                today.plusDays(
                    i.toLong()
                )
            val javaDate =
                Date.from(
                    date.atStartOfDay(
                        zone
                    )
                        .toInstant()
                )
            val solar =
                Solar.fromDate(
                    javaDate
                )
            val lunar =
                Lunar.fromDate(
                    javaDate
                )

            if (nearestJieQi == null) {
                val jieQi =
                    lunar.jieQi
                if (!jieQi.isNullOrBlank()) {
                    nearestJieQi =
                        FestivalModel(
                            name = jieQi,
                            date = date,
                            weekday = solar.weekInChinese,
                            daysLeft = i.toLong()
                        )
                }
            }

            val solarFestivals =
                solar.festivals
            if (solarFestivals.isNotEmpty()) {
                if (nearestFestivalDay == null) {
                    nearestFestivalDay =
                        i.toLong()
                    nearestFestivals =
                        solarFestivals.toMutableList()
                } else if (nearestFestivalDay == i.toLong()) {
                    nearestFestivals.addAll(
                        solarFestivals
                    )
                }
            }

            if (nearestJieQi != null && nearestFestivalDay != null) break
        }

        val result =
            mutableListOf<FestivalModel>()

        nearestJieQi?.let {
            result.add(
                it
            )
        }

        nearestFestivalDay?.let { days ->
            val date =
                today.plusDays(
                    days
                )
            val javaDate =
                Date.from(
                    date.atStartOfDay(
                        zone
                    )
                        .toInstant()
                )
            val solar =
                Solar.fromDate(
                    javaDate
                )

            nearestFestivals.forEach { name ->
                result.add(
                    FestivalModel(
                        name = name,
                        date = date,
                        weekday = solar.weekInChinese,
                        daysLeft = days
                    )
                )
            }
        }

        return result.sortedBy { it.daysLeft }
    }


}