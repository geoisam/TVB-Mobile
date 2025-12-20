package com.pjs.tvbox.ui.view.card

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.pjs.tvbox.data.AnimeInfo
import com.pjs.tvbox.data.BILIBILI_HOME
import com.pjs.tvbox.data.UA_DESKTOP

@Composable
fun BiLiAnimeCard(
    anime: AnimeInfo,
    playNum: Boolean = false
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val url = anime.cover
                val intent =
                    Intent(
                        Intent.ACTION_VIEW,
                        url?.toUri()
                    )
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
                        .data(anime.thumbnail)
                        .crossfade(true)
                        .httpHeaders(
                            NetworkHeaders.Builder()
                                .set("Referer", BILIBILI_HOME)
                                .set("User-Agent", UA_DESKTOP)
                                .build()
                        ).build(),
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
                    },
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            MaterialTheme.shapes.small
                        ),
                )
                anime.rating?.let {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(
                                MaterialTheme.colorScheme.primary
                                    .copy(alpha = 0.88f),
                                RoundedCornerShape(
                                    bottomStart = 8.dp,
                                    topEnd = 8.dp
                                )
                            )
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (it.isNotBlank()) "${it}分" else "暂无评分",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
                anime.view?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                            .copy(alpha = 0.88f)
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
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        anime.subtitle?.let {
            Text(
                text = if (playNum) "播放量$it" else it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}