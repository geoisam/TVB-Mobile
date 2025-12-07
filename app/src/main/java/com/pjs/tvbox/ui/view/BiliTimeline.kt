package com.pjs.tvbox.ui.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pjs.tvbox.data.BiliTimelineData
import com.pjs.tvbox.model.TimelineAnime
import com.pjs.tvbox.model.TimelineDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiliTimelineView(modifier: Modifier = Modifier) {
    var timelineData by remember { mutableStateOf<List<TimelineDate>>(emptyList()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    val tabs = timelineData.map { it.date }
    val currentEpisodes = timelineData.getOrNull(selectedTabIndex)?.episodes.orEmpty()

    LaunchedEffect(Unit) {
        isLoading = true
        timelineData = BiliTimelineData.getBiliTimeline()
        isLoading = false
    }

    LaunchedEffect(timelineData) {
        if (timelineData.isEmpty()) {
            selectedTabIndex = 0
            return@LaunchedEffect
        }

        val todayIndex = timelineData.indexOfFirst { it.isToday == 1 }
        selectedTabIndex =
            if (todayIndex != -1) todayIndex
            else selectedTabIndex.coerceIn(0, timelineData.lastIndex)
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        if (tabs.isNotEmpty()) {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 0.dp,
                divider = {},
            ) {
                tabs.forEachIndexed { index, date ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = date,
                                color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            )
                        }
                    )
                }
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            timelineData.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无数据")
                }
            }

            currentEpisodes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("当前日期暂无更新")
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = 12.dp,
                        end = 16.dp,
                        bottom = 18.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(currentEpisodes.size, key = { it }) { index ->
                        AnimeCard(currentEpisodes[index])
                    }
                }
            }
        }
    }
}


@Composable
private fun AnimeCard(anime: TimelineAnime) {
    val context = LocalContext.current
    val thumbnailUrl = remember(anime.epCover) {
        if (anime.epCover.contains("@")) {
            anime.epCover
        } else {
            "${anime.epCover}@300w_200h.webp"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = anime.epCover
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 2f)
                .clip(MaterialTheme.shapes.small),
            shape = MaterialTheme.shapes.small,
        ) {
            Box {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .httpHeaders(
                            NetworkHeaders.Builder()
                                .set("Referer", "https://www.bilibili.com/")
                                .set(
                                    "User-Agent",
                                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
                                )
                                .build()
                        )
                        .build(),
                    contentDescription = anime.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(MaterialTheme.shapes.small),
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
                if (anime.pubTime.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.78f),
                                RoundedCornerShape(topStart = 8.dp, bottomEnd = 8.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = anime.pubTime + " 更新",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
                if (anime.pubIndex.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.78f)
                                    )
                                )
                            )
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = anime.pubIndex,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        Text(
            text = anime.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
        )
    }
}

private val TimelineDate.dayOfWeekText: String
    get() = when (dayOfWeek) {
        1 -> "周一"
        2 -> "周二"
        3 -> "周三"
        4 -> "周四"
        5 -> "周五"
        6 -> "周六"
        7 -> "周日"
        else -> "周〇"
    }