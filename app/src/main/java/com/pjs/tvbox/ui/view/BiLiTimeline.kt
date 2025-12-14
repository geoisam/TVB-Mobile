package com.pjs.tvbox.ui.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.SubcomposeAsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.pjs.tvbox.data.BILIBILI_HOME
import com.pjs.tvbox.data.BiLiTimelineData
import com.pjs.tvbox.data.TimelineDate
import com.pjs.tvbox.data.TimelineInfo
import com.pjs.tvbox.data.UA_DESKTOP
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun BiLiTimelineView(
    modifier: Modifier = Modifier
) {
    var timelineData by remember {
        mutableStateOf<List<TimelineDate>>(
            emptyList()
        )
    }
    var isLoading by remember {
        mutableStateOf(
            true
        )
    }

    val scope =
        rememberCoroutineScope()

    LaunchedEffect(
        Unit
    ) {
        isLoading =
            true
        timelineData =
            BiLiTimelineData.getBiliTimeline()
        isLoading =
            false
    }

    val tabs =
        timelineData

    val pagerState =
        rememberPagerState(
            initialPage = 0,
            pageCount = { tabs.size }
        )

    LaunchedEffect(
        timelineData
    ) {
        if (timelineData.isNotEmpty()) {
            val todayIndex =
                timelineData.indexOfFirst { it.isToday == 1 }
            if (todayIndex != -1) {
                pagerState.scrollToPage(
                    todayIndex
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (tabs.isNotEmpty()) {

            PrimaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                divider = {},
            ) {
                tabs.forEachIndexed { index, timeline ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(
                                    index
                                )
                            }
                        },
                        text = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    4.dp
                                ),
                            ) {
                                timeline.date?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }
                                if (pagerState.currentPage == index) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.78f
                                                ),
                                                CircleShape
                                            )
                                            .padding(
                                                horizontal = 7.dp,
                                                vertical = 3.dp
                                            ),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = timeline.weekdayText,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                } else {
                                    Text(
                                        text = timeline.weekdayText,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 2,
        ) { page ->

            val safeTimeline =
                timelineData
            val safePageData =
                safeTimeline.getOrNull(
                    page
                )
            val currentEpisodes =
                safePageData?.episodes.orEmpty()

            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                    ),
            ) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    safeTimeline.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "暂无数据"
                            )
                        }
                    }

                    safePageData == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "加载中…"
                            )
                        }
                    }

                    currentEpisodes.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "当前日期暂无更新"
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(
                                2
                            ),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 12.dp,
                                end = 16.dp,
                                bottom = 18.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                8.dp
                            ),
                        ) {
                            items(
                                count = currentEpisodes.size,
                                key = { it }
                            ) { index ->
                                AnimeCard(
                                    currentEpisodes[index]
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeCard(
    anime: TimelineInfo
) {
    val context =
        LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url =
                    anime.coverV
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        url?.toUri()
                    )
                context.startActivity(
                    intent
                )
            },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    3f / 2f
                )
                .clip(
                    MaterialTheme.shapes.small
                ),
            shape = MaterialTheme.shapes.small,
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(
                        context
                    )
                        .data(
                            anime.thumbnail
                        )
                        .crossfade(
                            true
                        )
                        .httpHeaders(
                            NetworkHeaders.Builder()
                                .set(
                                    "Referer",
                                    BILIBILI_HOME
                                )
                                .set(
                                    "User-Agent",
                                    UA_DESKTOP
                                )
                                .build()
                        )
                        .build(),
                    contentDescription = anime.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            MaterialTheme.shapes.small
                        ),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(
                                    MaterialTheme.shapes.small
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "图片加载失败",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                )
                anime.time?.let {
                    Box(
                        modifier = Modifier
                            .align(
                                Alignment.TopStart
                            )
                            .background(
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.88f
                                ),
                                RoundedCornerShape(
                                    topStart = 8.dp,
                                    bottomEnd = 8.dp
                                )
                            )
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$it 更新",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
                anime.view?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(
                                Alignment.BottomEnd
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(
                                            alpha = 0.88f
                                        )
                                    )
                                )
                            )
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            ),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        anime.title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp
                    )
                    .align(
                        Alignment.CenterHorizontally
                    ),
            )
        }
    }
}

private val TimelineDate.weekdayText: String
    get() = when (weekday) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        7 -> "周日"
        else -> "周〇"
    }