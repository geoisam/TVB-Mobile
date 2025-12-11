package com.pjs.tvbox.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private val tabs = listOf("近期热播", "最近更新", "最多追番", "最高评分", "最多播放", "最早开播")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiLiAnimeFilterView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
            divider = {},
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = title,
                            color = if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (pagerState.currentPage == index)
                                FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> BiLiAnimeHotView(modifier = Modifier.fillMaxSize())
                1 -> BiLiAnimeNewView(modifier = Modifier.fillMaxSize())
                2 -> BiLiAnimeMostView(modifier = Modifier.fillMaxSize())
                3 -> BiLiAnimeTopView(modifier = Modifier.fillMaxSize())
                4 -> BiLiAnimeMoreView(modifier = Modifier.fillMaxSize())
                5 -> BiLiAnimeOldView(modifier = Modifier.fillMaxSize())
            }
        }
    }
}


