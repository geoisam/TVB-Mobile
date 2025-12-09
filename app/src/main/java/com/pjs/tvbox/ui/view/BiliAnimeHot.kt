package com.pjs.tvbox.ui.view

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.pjs.tvbox.data.BiliAnimeFilterData
import com.pjs.tvbox.data.BiliAnimeHotData
import com.pjs.tvbox.model.AnimeHot
import kotlinx.coroutines.launch

private val tabOrders = listOf(-1, 0, 3, 4, 2, 5)
private val tabSorts = listOf(-1, 0, 0, 0, 0, 1)
private val tabTitles =
    listOf("近期热播", "最近更新", "最多追番", "最高评分", "最多播放", "最早开播")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiliAnimeHotView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabTitles.size }
    )
    val scope = rememberCoroutineScope()

    val pageData = remember { mutableStateOf<Map<Int, List<AnimeHot>>>(emptyMap()) }
    val pageLoading = remember { mutableStateOf<Set<Int>>(emptySet()) }

    fun loadPage(page: Int) {
        if (pageData.value.containsKey(page)) return
        if (pageLoading.value.contains(page)) return

        pageLoading.value += page

        scope.launch {
            val result = if (page == 0) {
                BiliAnimeHotData.getAnimeHot()
            } else {
                val order = tabOrders[page]
                val sort = tabSorts[page]
                BiliAnimeFilterData.getAnimeHot(order = order, page = 1, sort = sort)
            }

            pageData.value += (page to result)
            pageLoading.value -= page
        }
    }

    LaunchedEffect(Unit) {
        loadPage(0)
    }

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp,
            divider = {},
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                        loadPage(index)
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
            beyondViewportPageCount = 2,
        ) { page ->
            LaunchedEffect(page) {
                loadPage(page)
            }

            val isLoading = pageLoading.value.contains(page)
            val data = pageData.value[page].orEmpty()

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

                    data.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("暂无数据")
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                top = 12.dp,
                                end = 16.dp,
                                bottom = 18.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(data.size, key = { it }) { index ->
                                AnimeHotCard(data[index])
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeHotCard(anime: AnimeHot) {
    val context = LocalContext.current
    val thumbnailUrl = remember(anime.cover) {
        if (anime.cover.contains("@")) {
            anime.cover
        } else {
            "${anime.cover}@200w_300h.webp"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = anime.cover
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
            },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
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
                    },
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small),
                )
                if (anime.rating.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                Color(0xFFFB7299).copy(alpha = 0.88f),
                                RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = anime.rating + "分",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
                if (anime.indexShow.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.88f)
                                    )
                                )
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = anime.indexShow,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
        )
    }
}