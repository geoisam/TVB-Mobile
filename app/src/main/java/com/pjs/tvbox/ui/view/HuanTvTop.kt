package com.pjs.tvbox.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.pjs.tvbox.data.HuanTv
import com.pjs.tvbox.data.HuanTvTopData
import kotlinx.coroutines.delay

@Composable
fun HuanTvTopView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var tvRate by remember { mutableStateOf<List<HuanTv>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        error = null

        try {
            tvRate = HuanTvTopData.getHuanTvHot()
        } catch (e: Exception) {
            error = e.message ?: "加载失败"
        } finally {
            isLoading = false
        }

        while (true) {
            delay(32_123)
            try {
                tvRate = HuanTvTopData.getHuanTvHot()
            } catch (e: Exception) {
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(strokeWidth = 4.dp)
            }

            error != null -> {
                Text(
                    text = "加载失败\n\n$error",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            tvRate.isEmpty() -> {
                Text(
                    text = "暂无数据",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            else -> {
                Column(Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "剧集",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .width(160.dp)
                                            .padding(horizontal = 16.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = "收视率",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                            .padding(horizontal = 16.dp),
                                        textAlign = TextAlign.End,
                                    )
                                    Text(
                                        text = "市场份额",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                            .padding(horizontal = 16.dp),
                                        textAlign = TextAlign.End,
                                    )
                                }
                            }
                        }
                        items(tvRate) { huan ->
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                            )
                            Row(
                                Modifier.clickable {},
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    Modifier
                                        .width(160.dp)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Card(
                                            modifier = Modifier
                                                .width(18.dp)
                                                .height(18.dp)
                                                .clip(CircleShape),
                                            shape = CircleShape,
                                        ) {
                                            SubcomposeAsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(huan.channelLogo)
                                                    .crossfade(true)
                                                    .httpHeaders(
                                                        NetworkHeaders.Builder()
                                                            .set(
                                                                "Referer",
                                                                "https://bigdata.huan.tv/"
                                                            )
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
                                                            .clip(CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        CircularProgressIndicator()
                                                    }
                                                },
                                                error = {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .clip(CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(
                                                            "图",
                                                            color = Color.Gray,
                                                            style = MaterialTheme.typography.labelMedium,
                                                        )
                                                    }
                                                },
                                                contentDescription = huan.channelName,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape),
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = huan.channelName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                )
                                            ) {
                                                append("直播:")
                                            }
                                            append(" ")
                                            withStyle(
                                                SpanStyle(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            ) {
                                                append(huan.programName)
                                            }
                                        },
                                        style = MaterialTheme.typography.bodySmall,

                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                Text(
                                    text = huan.onlineRate,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    textAlign = TextAlign.End,
                                )
                                Text(
                                    text = huan.marketShare,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp),
                                    textAlign = TextAlign.End,
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

}
