package com.pjs.tvbox.data

import com.pjs.tvbox.R
import kotlinx.serialization.json.Json

// JSON
val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

// 开发者
const val APP_AUTHOR = "GeoiSam"
const val APP_AUTHOR_NAME = "潘钜森"
const val APP_AUTHOR_MAIL = "geoisam@qq.com"
const val APP_AUTHOR_SAY = "愿你一生欢喜，不为世俗所及"

// GitHub
const val GITHUB_HOME = "https://github.com/geoisam"
const val GITHUB_REPO = "https://github.com/geoisam/TVB-Mobile"
const val GITHUB_ISSUE = "https://github.com/geoisam/TVB-Mobile/issues"
const val GITHUB_RELEASE = "https://github.com/geoisam/TVB-Mobile/releases"
const val GITHUB_API = "https://api.github.com"

// 豆瓣
const val DOUBAN_HOME = "https://m.douban.com/movie"
const val DOUBAN_API = "https://m.douban.com"

// 哔哩哔哩
const val BILIBILI_HOME = "https://www.bilibili.com/anime"
const val BILIBILI_API = "https://api.bilibili.com"

// 爱奇艺
const val IQIYI_HOME = "https://m.iqiyi.com/ranklist.html"
const val IQIYI_API = "https://cards.iqiyi.com"

// CMDB
const val CMDB_HOME = "https://zgdypf.zgdypw.cn/movie"
const val CMDB_API = "https://zgdypf.zgdypw.cn"

// 欢网大数据
const val HUANTV_HOME = "https://bigdata.huan.tv/realtime/live"
const val HUANTV_API = "https://tv-zone-api.huan.tv"

// UA
const val UA_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36 Edg/123.0.2420.81"
const val UA_MOBILE = "Mozilla/5.0 (Linux; Android 16; MCE16 Build/BP3A.250905.014; ) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/123.0.0.0 Mobile Safari/537.36 EdgA/123.0.2420.102"

// 主页底部导航
sealed class MainScreen(
    val route: String,
    val title: Int,
    val chIconId: Int,
    val unIconId: Int
) {
    data object Home :
        MainScreen(
            "home",
            R.string.nav_home,
            R.drawable.ic_home_fill,
            R.drawable.ic_home
        )

    data object Discover :
        MainScreen(
            "discover",
            R.string.nav_discover,
            R.drawable.ic_explore_fill,
            R.drawable.ic_explore
        )

    data object Mine :
        MainScreen(
            "mine",
            R.string.nav_mine,
            R.drawable.ic_person_fill,
            R.drawable.ic_person
        )
}

// 更新
const val NOTE = "https://share.note.youdao.com/yws/api/note/d56e2e56e3434f73519e10dc3b831662?sev=j1&cstk=LnuyBs-w"

data class UpdateInfo(
    val versionName: String? = null,
    val appSize: Long? = null,
    val downloadUrl: String? = null,
    val changeLog: String? = null,
)

// 影片
data class MovieInfo(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val thumbnail: String? = null,
    val cover: String? = null,
    val rating: String? = null,
    val view: Long? = null,
)

data class AnimeInfo(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val thumbnail: String? = null,
    val cover: String? = null,
    val rating: String? = null,
    val view: String? = null,
)

data class SearchInfo(
    val id: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val daysDesc: String? = null,
    val currHeat: String? = null,
)

// 番剧时间表
data class TimelineDate(
    val date: String? = null,
    val weekday: Int? = null,
    val isToday: Int? = null,
    val episodes: List<TimelineInfo>?
)

data class TimelineInfo(
    val id: String? = null,
    val title: String? = null,
    val thumbnail: String? = null,
    val coverV: String? = null,
    val coverH: String? = null,
    val time: String? = null,
    val view: String? = null,
)

// 实时票房
data class TicketSales(
    val salesDesc: String? = null,
    val salesUnit: String? = null,
    val splitSalesDesc: String? = null,
    val splitSalesUnit: String? = null,
    val updateTimestamp: String? = null,
)

data class TicketInfo(
    val id: String? = null,
    val name: String? = null,
    val onlineSalesRateDesc: String? = null,
    val releaseDays: Int? = null,
    val releaseDesc: String? = null,
    val salesInWanDesc: String? = null,
    val salesRateDesc: String? = null,
    val seatRateDesc: String? = null,
    val sessionRateDesc: String? = null,
    val splitOnlineSalesRateDesc: String? = null,
    val splitSalesInWanDesc: String? = null,
    val splitSalesRateDesc: String? = null,
    val sumSalesDesc: String? = null,
    val sumSplitSalesDesc: String? = null,
)

// 票房年榜
data class TicketYear(
    val id: String? = null,
    val name: String? = null,
    val premiereDate: String? = null,
    val salesInWan: String? = null,
    val avgPrice: String? = null,
    val avgSalesCount: String? = null,
)

// 电视收视
data class HuanTvInfo(
    val key: String? = null,
    val channelName: String? = null,
    val onlineRate: String? = null,
    val channelLogo: String? = null,
    val programName: String? = null,
    val marketShare: String? = null,
    val channelCode: String? = null,
)
