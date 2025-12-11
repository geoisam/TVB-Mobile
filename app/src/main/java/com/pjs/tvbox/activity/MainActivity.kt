package com.pjs.tvbox.activity

import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.ui.page.AboutPage
import com.pjs.tvbox.ui.page.BottomNav
import com.pjs.tvbox.ui.page.HomePage
import com.pjs.tvbox.ui.page.DiscoverPage
import com.pjs.tvbox.ui.page.MinePage
import com.pjs.tvbox.ui.page.tool.*
import com.pjs.tvbox.data.MainScreen
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
                is OverlayPage.TvLive -> TvLivePage(onClose, page.title)
                is OverlayPage.Transcode -> Transcode(onClose, page.title)
                is OverlayPage.DouBanTop -> DouBanTop(onClose, page.title)
                is OverlayPage.CMDbYear -> CMDbYear(onClose, page.title)
                is OverlayPage.CMDbTicket -> CMDbTicket(onClose, page.title)
                is OverlayPage.BiLiTimeline -> BiLiTimeline(onClose, page.title)
                is OverlayPage.BiLiAnimeFilter -> BiLiAnimeFilter(onClose, page.title)
                is OverlayPage.HuanTvTop -> HuanTvTop(onClose, page.title)
                is OverlayPage.TodayNews -> TodayNews(onClose, page.title)
                is OverlayPage.FuckWatermark -> FuckWatermark(onClose, page.title)
                is OverlayPage.About -> AboutPage(onClose, page.title)
            }
        }
    }
}


sealed class OverlayPage(val title: Int) {
    class TvLive(title: Int) : OverlayPage(title)
    class Transcode(title: Int) : OverlayPage(title)
    class DouBanTop(title: Int) : OverlayPage(title)
    class CMDbYear(title: Int) : OverlayPage(title)
    class BiLiTimeline(title: Int) : OverlayPage(title)
    class BiLiAnimeFilter(title: Int) : OverlayPage(title)
    class CMDbTicket(title: Int) : OverlayPage(title)
    class HuanTvTop(title: Int) : OverlayPage(title)
    class TodayNews(title: Int) : OverlayPage(title)
    class FuckWatermark(title: Int) : OverlayPage(title)
    class About(title: Int) : OverlayPage(title)
}
