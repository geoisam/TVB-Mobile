package com.pjs.tvbox.ui.page

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.OverlayPage
import com.pjs.tvbox.ui.dialog.TipsDialog
import com.pjs.tvbox.ui.theme.LogoFont
import com.pjs.tvbox.util.LunarUtil
import java.util.Calendar
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinePage(
    onOpenPage: (OverlayPage) -> Unit
) {
    val context = LocalContext.current
    val showTipsDialog = remember { mutableStateOf(false) }
    val dateState = remember { mutableStateMapOf<String, String>() }

    fun updateDateState() {
        dateState["yearMonth"] = LunarUtil.getYearMonth()
        dateState["shiChen"] = LunarUtil.getShiChen()
        dateState["week"] = LunarUtil.getWeek()
        dateState["day"] = LunarUtil.getDay()
        dateState["monthDay"] = LunarUtil.getMonthDay()
        dateState["ganZhi"] = LunarUtil.getGanZhi()
    }

    LaunchedEffect(Unit) {
        updateDateState()
        while (true) {
            delay(calculateTimeToNextHour())
            updateDateState()
        }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                updateDateState()
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
            addAction(Intent.ACTION_DATE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            showTipsDialog.value = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_help),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "主题", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_shirt),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    IconButton(
                        onClick = {
                            Toast.makeText(context, "设置", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
            TipsDialog(
                isOpen = showTipsDialog.value,
                onClose = { showTipsDialog.value = false },
                title = "友情提示",
                message = "本软件一直为 开源免费 使用，如果你是从某些地方付费购买的话，那你就是被骗了！",
                confirmButtonText = "知道了",
                onConfirm = { },
                dismissButtonText = null,
                onDismiss = { },
                closeIcon = false,
                onCloseIconClick = { }
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            updateDateState()
                            Toast.makeText(context, "日历时间已更新", Toast.LENGTH_SHORT).show()
                        },
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = dateState["yearMonth"] ?: "2002 年 1 月",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.78f),
                                            MaterialTheme.shapes.medium
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dateState["shiChen"] ?: "null",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                            Text(
                                text = dateState["week"] ?: "星期二",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = dateState["day"] ?: "29",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dateState["monthDay"] ?: "腊月十七",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = dateState["ganZhi"] ?: "辛巳蛇年 辛丑牛月 丁酉鸡日",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MineCard(
                        iconRes = R.drawable.ic_star,
                        text = "收藏夹",
                        onClick = {
                            Toast.makeText(context, "收藏夹", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                    )
                    MineCard(
                        iconRes = R.drawable.ic_history,
                        text = "观看历史",
                        onClick = {
                            Toast.makeText(context, "观看历史", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                    )
                    MineCard(
                        iconRes = R.drawable.ic_cloud_download,
                        text = "下载缓存",
                        onClick = {
                            Toast.makeText(context, "下载缓存", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        MineItem(
                            iconRes = R.drawable.ic_database,
                            text = "订阅管理",
                            onClick = {
                                Toast.makeText(context, "订阅管理", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_media_link,
                            text = "播放链接",
                            onClick = {
                                Toast.makeText(context, "播放链接", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_videos,
                            text = "本地视频",
                            onClick = {
                                Toast.makeText(context, "本地视频", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_storage,
                            text = "备份与恢复",
                            onClick = {
                                Toast.makeText(context, "备份恢复", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.small),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "版本升级",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_rocket),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Text(
                                    text = "已是最新版本",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, "检测更新", Toast.LENGTH_SHORT)
                                        .show()
                                },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = "关于",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_info),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    fontFamily = LogoFont,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    onOpenPage(OverlayPage.About)
                                },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }

}

fun calculateTimeToNextHour(): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.HOUR_OF_DAY, 1)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis - System.currentTimeMillis()
}

@Composable
fun MineCard(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun MineItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}