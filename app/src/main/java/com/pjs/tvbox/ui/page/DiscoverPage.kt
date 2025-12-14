package com.pjs.tvbox.ui.page

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.activity.OverlayPage
import com.pjs.tvbox.ui.dialog.TipsDialog

data class ToolItem(
    val icon: Int,
    val title: Int,
    val subtitle: Int,
    val onClick: () -> Unit,
)

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun DiscoverPage(
    onOpenPage: (OverlayPage) -> Unit
) {
    val context =
        LocalContext.current
    val showTipsDialog =
        remember {
            mutableStateOf(
                false
            )
        }

    val toolsList =
        listOf(
            ToolItem(
                icon = R.drawable.ic_tv,
                title = R.string.tvlive_title,
                subtitle = R.string.tvlive_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.TvLive(
                            R.string.tvlive_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_change_circle,
                title = R.string.transcode_title,
                subtitle = R.string.transcode_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.Transcode(
                            R.string.transcode_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_movie,
                title = R.string.douban_rating_title,
                subtitle = R.string.douban_rating_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.DouBanTop(
                            R.string.douban_rating_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_hive,
                title = R.string.cmdb_year_title,
                subtitle = R.string.cmdb_year_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.CMDbYear(
                            R.string.cmdb_year_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_event_note,
                title = R.string.bilibili_timeline_title,
                subtitle = R.string.bilibili_timeline_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.BiLiTimeline(
                            R.string.bilibili_timeline_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_whatshot,
                title = R.string.bilibili_filter_title,
                subtitle = R.string.bilibili_filter_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.BiLiAnimeFilter(
                            R.string.bilibili_filter_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_local_atm,
                title = R.string.cmdb_ticket_title,
                subtitle = R.string.cmdb_ticket_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.CMDbTicket(
                            R.string.cmdb_ticket_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_kanban,
                title = R.string.huan_tv_title,
                subtitle = R.string.huan_tv_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.HuanTvTop(
                            R.string.huan_tv_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_news,
                title = R.string.today_news_title,
                subtitle = R.string.today_news_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.TodayNews(
                            R.string.today_news_title
                        )
                    )
                }
            ),
            ToolItem(
                icon = R.drawable.ic_psychiatry,
                title = R.string.fuck_watermark_title,
                subtitle = R.string.fuck_watermark_desc,
                onClick = {
                    onOpenPage(
                        OverlayPage.FuckWatermark(
                            R.string.fuck_watermark_title
                        )
                    )
                }
            ),
        )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 4.dp
                            )
                            .clip(
                                MaterialTheme.shapes.extraLarge
                            )
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "搜索",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            },
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 8.dp
                                )
                                .padding(
                                    start = 18.dp,
                                    end = 16.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "搜索",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_search
                                ),
                                contentDescription = stringResource(
                                    R.string.top_search
                                ),
                                modifier = Modifier.size(
                                    20.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            showTipsDialog.value =
                                true
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_help
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(
                                24.dp
                            ),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "更多",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_add_circle
                            ),
                            contentDescription = stringResource(
                                R.string.top_more
                            ),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(
                                24.dp
                            ),
                        )
                    }
                }
            )
            TipsDialog(
                isOpen = showTipsDialog.value,
                onClose = {
                    showTipsDialog.value =
                        false
                },
                title = "友情提示",
                message = "本软件仅供个人学习交流和研究参考，严禁用于商业用途，安装后请于 24 小时内删除！",
                confirmButtonText = "知道了",
                onConfirm = { },
                dismissButtonText = null,
                onDismiss = { },
                closeIcon = false,
                onCloseIconClick = { }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    padding
                )
                .padding(
                    horizontal = 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(
                12.dp
            ),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    2
                ),
                contentPadding = PaddingValues(
                    bottom = 18.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    12.dp
                ),
            ) {
                items(
                    toolsList.size
                ) { index ->
                    val tool =
                        toolsList[index]
                    ToolsCard(
                        logo = tool.icon,
                        title = tool.title,
                        subtitle = tool.subtitle,
                        onClick = tool.onClick,
                        modifier = Modifier.weight(
                            1f
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ToolsCard(
    logo: Int,
    title: Int,
    subtitle: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(
                MaterialTheme.shapes.medium
            )
            .clickable(
                onClick = onClick
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),
        ) {
            Icon(
                painter = painterResource(
                    logo
                ),
                contentDescription = null,
                modifier = Modifier.size(
                    41.dp
                ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(
                modifier = Modifier.height(
                    8.dp
                )
            )
            Text(
                text = stringResource(
                    title
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(
                    subtitle
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}