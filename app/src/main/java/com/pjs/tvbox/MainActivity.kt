package com.pjs.tvbox

import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.pjs.tvbox.model.Update
import com.pjs.tvbox.ui.dialog.TipsDialog
import com.pjs.tvbox.ui.page.AboutPage
import com.pjs.tvbox.ui.page.BottomNav
import com.pjs.tvbox.ui.page.HomePage
import com.pjs.tvbox.ui.page.DiscoverPage
import com.pjs.tvbox.ui.page.MinePage
import com.pjs.tvbox.ui.page.tool.BiliAnimeHot
import com.pjs.tvbox.ui.page.tool.BiliTimeline
import com.pjs.tvbox.ui.page.tool.CMDatabase
import com.pjs.tvbox.ui.page.tool.DouBanTop
import com.pjs.tvbox.ui.page.tool.FuckWatermark
import com.pjs.tvbox.ui.page.tool.HuanTvTop
import com.pjs.tvbox.ui.page.tool.MaoYanHot
import com.pjs.tvbox.ui.page.tool.TodayNews
import com.pjs.tvbox.ui.page.tool.Transcode
import com.pjs.tvbox.ui.page.tool.TvLivePage
import com.pjs.tvbox.ui.screen.MainScreen
import com.pjs.tvbox.util.UpdateUtil
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var overlayPage by remember { mutableStateOf<OverlayPage?>(null) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabs = listOf(MainScreen.Home, MainScreen.Discover, MainScreen.Mine)

    var updateInfo by remember { mutableStateOf<Update?>(null) }

    LaunchedEffect(Unit) {
        UpdateUtil.consumeUpdate()?.let { updateInfo = it }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0.dp),
            bottomBar = {
                BottomNav(
                    currentRoute = tabs[pagerState.currentPage].route,
                    onTabSelected = { screen ->
                        scope.launch {
                            pagerState.animateScrollToPage(tabs.indexOf(screen))
                        }
                    }
                )
            }
        ) { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                beyondViewportPageCount = 2,
            ) { page ->
                when (tabs[page]) {
                    MainScreen.Home -> HomePage()
                    MainScreen.Discover -> DiscoverPage(onOpenPage = { overlayPage = it })
                    MainScreen.Mine -> MinePage(onOpenPage = { overlayPage = it })
                }
            }
        }
        overlayPage?.let { page ->
            val onClose = { overlayPage = null }
            when (page) {
                is OverlayPage.TvLive -> TvLivePage(onClose)
                is OverlayPage.Transcode -> Transcode(onClose)
                is OverlayPage.DouBanTop -> DouBanTop(onClose)
                is OverlayPage.MaoYanHot -> MaoYanHot(onClose)
                is OverlayPage.CMDatabase -> CMDatabase(onClose)
                is OverlayPage.BiliTimeline -> BiliTimeline(onClose)
                is OverlayPage.BiliAnimeHot -> BiliAnimeHot(onClose)
                is OverlayPage.HuanTvTop -> HuanTvTop(onClose)
                is OverlayPage.TodayNews -> TodayNews(onClose)
                is OverlayPage.FuckWatermark -> FuckWatermark(onClose)

                is OverlayPage.About -> AboutPage(onClose)
            }
        }
        updateInfo?.let { update ->
            TipsDialog(
                isOpen = true,
                onClose = { updateInfo = null },
                title = "发现新版本 ${update.versionName}",
                message = "版本：${update.versionCode}\n" +
                        "大小：${Formatter.formatFileSize(LocalContext.current, update.appSize)}\n" +
                        "\n更新日志：\n${update.changeLog.ifBlank { "修复了一些已知问题" }}".trimIndent(),
                confirmButtonText = "更新",
                onConfirm = {
                    val url = when {
                        update.downloadUrl.startsWith("http") -> update.downloadUrl
                        else -> "https://github.com/geoisam/TVB-Mobile/releases"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                    context.startActivity(intent)
                },
                dismissButtonText = "取消",
                onDismiss = { updateInfo = null },
                closeIcon = false
            )
        }
    }
}

sealed class OverlayPage {
    object TvLive : OverlayPage()
    object Transcode : OverlayPage()
    object DouBanTop : OverlayPage()
    object MaoYanHot : OverlayPage()
    object BiliTimeline : OverlayPage()
    object BiliAnimeHot : OverlayPage()
    object CMDatabase : OverlayPage()
    object HuanTvTop : OverlayPage()
    object TodayNews : OverlayPage()
    object FuckWatermark : OverlayPage()
    object About : OverlayPage()
}