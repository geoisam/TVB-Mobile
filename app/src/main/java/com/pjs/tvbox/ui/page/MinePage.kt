package com.pjs.tvbox.ui.page

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.pjs.tvbox.R
import com.pjs.tvbox.activity.OverlayPage
import com.pjs.tvbox.data.UpdateInfo
import com.pjs.tvbox.ui.dialog.TipsDialog
import com.pjs.tvbox.ui.theme.LogoFont
import com.pjs.tvbox.util.LunarUtil
import com.pjs.tvbox.util.FestivalModel
import com.pjs.tvbox.util.UpdateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinePage(
    onOpenPage: (OverlayPage) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    var showSheet by remember { mutableStateOf(false) }
    val showTipsDialog = remember { mutableStateOf(false) }
    val showUpdateDialog = remember { mutableStateOf(false) }

    val dateState = remember { mutableStateMapOf<String, String>() }
    val nextFestival = remember { mutableStateOf<List<FestivalModel>>(emptyList()) }

    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    val isChecking = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun updateDateState() {
        dateState["YearMonth"] = LunarUtil.getYearMonth()
        dateState["ShiChen"] = LunarUtil.getShiChen()
        dateState["Week"] = LunarUtil.getWeek()
        dateState["Day"] = LunarUtil.getDay()
        dateState["MonthDay"] = LunarUtil.getMonthDay()
        dateState["GanZhi"] = LunarUtil.getGanZhi()
        dateState["JieQi"] = LunarUtil.getJieQi()
        dateState["DayYi"] = LunarUtil.getDayYi()
        dateState["DayJi"] = LunarUtil.getDayJi()
        dateState["DayChong"] = LunarUtil.getDayChong()
        dateState["PengZu"] = LunarUtil.getPengZu()
        dateState["DayXi"] = LunarUtil.getDayXi()
        dateState["DayCai"] = LunarUtil.getDayCai()
        dateState["DayFu"] = LunarUtil.getDayFu()
        nextFestival.value = LunarUtil.getNextFestival()
    }

    LaunchedEffect(Unit) {
        UpdateUtil.init(context)
        updateInfo = UpdateUtil.getUpdateInfo()

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
            runCatching { context.unregisterReceiver(receiver) }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { showTipsDialog.value = true }
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
                                    onOpenPage(
                                        OverlayPage.Settings(
                                            R.string.top_settings
                                        )
                                    )
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
                        .clickable { showSheet = true },
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
                                    text = dateState["YearMonth"] ?: "阳历年 阳历月",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary
                                                .copy(alpha = 0.78f),
                                            MaterialTheme.shapes.medium
                                        )
                                        .padding(
                                            horizontal = 8.dp,
                                            vertical = 4.dp
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dateState["ShiChen"] ?: "时辰",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                if (dateState["JieQi"]?.isNotBlank() == true) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary
                                                    .copy(alpha = 0.69f),
                                                MaterialTheme.shapes.medium
                                            )
                                            .padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dateState["JieQi"] ?: "节气",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                            Text(
                                text = dateState["Week"] ?: "星期",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                        Text(
                            text = dateState["Day"] ?: "阳历日",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )
                        Text(
                            text = dateState["MonthDay"] ?: "阴历月日",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = dateState["GanZhi"] ?: "年干支 月干支 日干支",
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
                        text = stringResource(
                            R.string.mine_like
                        ),
                        onClick = {
                            Toast.makeText(
                                context,
                                "收藏夹",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(
                            1f
                        ),
                    )
                    MineCard(
                        iconRes = R.drawable.ic_history,
                        text = stringResource(
                            R.string.mine_history
                        ),
                        onClick = {
                            Toast.makeText(
                                context,
                                "观看历史",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                    )
                    MineCard(
                        iconRes = R.drawable.ic_cloud_download,
                        text = stringResource(
                            R.string.mine_downloads
                        ),
                        onClick = {
                            Toast.makeText(
                                context,
                                "下载缓存",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(
                            1f
                        ),
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            MaterialTheme.shapes.small
                        ),
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
                            text = stringResource(
                                R.string.mine_subscribe
                            ),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "订阅管理",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_media_link,
                            text = stringResource(
                                R.string.mine_play_link
                            ),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "播放链接",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_videos,
                            text = stringResource(
                                R.string.mine_local_videos
                            ),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "本地视频",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.weight(1f),
                        )
                        MineItem(
                            iconRes = R.drawable.ic_storage,
                            text = stringResource(
                                R.string.mine_backup
                            ),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "备份恢复",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                        .clip(
                            MaterialTheme.shapes.small
                        ),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        R.string.mine_update
                                    ),
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
                                when {
                                    isChecking.value ->
                                        CircularProgressIndicator()

                                    UpdateUtil.hasNewVersion() ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.primary
                                                        .copy(alpha = 0.78f),
                                                    MaterialTheme.shapes.large
                                                )
                                                .padding(horizontal = 9.dp, vertical = 6.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = "发现新版本",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                            )
                                        }

                                    else ->
                                        Text(
                                            text = "已是最新版本",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                }
                            },
                            modifier = Modifier.clickable {
                                if (isChecking.value) return@clickable

                                if (UpdateUtil.hasNewVersion()) {
                                    showUpdateDialog.value = true
                                } else {
                                    isChecking.value = true
                                    scope.launch {
                                        val hasUpdate = runCatching {
                                            UpdateUtil.checkUpdate(context)
                                        }.getOrDefault(false)

                                        withContext(Dispatchers.Main) {
                                            if (hasUpdate) {
                                                updateInfo = UpdateUtil.getUpdateInfo()
                                                showUpdateDialog.value = true
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "已是最新版本",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            isChecking.value = false
                                        }
                                    }
                                }
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
                                    painter = painterResource(
                                        R.drawable.ic_info
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Text(
                                    text = stringResource(
                                        R.string.app_name
                                    ),
                                    fontFamily = LogoFont,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    onOpenPage(
                                        OverlayPage.About(
                                            R.string.mine_about
                                        )
                                    )
                                },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            )
                        )
                    }
                }
                Spacer(
                    modifier = Modifier.height(
                        18.dp
                    )
                )
            }
        }
        if (showSheet) {
            val scrollState = rememberScrollState()
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                dragHandle = null,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            16.dp
                        )
                        .verticalScroll(
                            scrollState
                        ),
                    verticalArrangement = Arrangement.spacedBy(
                        12.dp
                    ),
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                MaterialTheme.shapes.small
                            ),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        ),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                4.dp
                            ),
                        ) {
                            nextFestival.value.forEach { jieri ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { }
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        modifier = Modifier.weight(
                                            1f
                                        )
                                    ) {
                                        Text(
                                            text = jieri.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "${jieri.date} (周${jieri.weekday})",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }

                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(
                                                SpanStyle(
                                                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Bold,
                                                )
                                            ) {
                                                append(
                                                    jieri.daysLeft.toString()
                                                )
                                            }
                                            append(
                                                " "
                                            )
                                            withStyle(
                                                SpanStyle(
                                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                )
                                            ) {
                                                append(
                                                    "天"
                                                )
                                            }
                                        },
                                        modifier = Modifier.align(
                                            Alignment.CenterVertically
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                MaterialTheme.shapes.small
                            )
                            .clickable { },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                6.dp
                            ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFF1772F6
                                            ),
                                            MaterialTheme.shapes.medium,
                                        )
                                        .padding(
                                            horizontal = 7.dp,
                                            vertical = 3.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "宜",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayYi"]
                                        ?: "无",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(

                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFF808080
                                            ),
                                            MaterialTheme.shapes.medium,
                                        )
                                        .padding(
                                            horizontal = 7.dp,
                                            vertical = 3.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "忌",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayJi"]
                                        ?: "无",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                MaterialTheme.shapes.small
                            )
                            .clickable { },
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                6.dp
                            ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFFFF6700
                                            ),
                                            MaterialTheme.shapes.medium,
                                        )
                                        .padding(
                                            horizontal = 7.dp,
                                            vertical = 3.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "冲煞",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayChong"]
                                        ?: "无",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFF8B4513
                                            ),
                                            MaterialTheme.shapes.medium,
                                        )
                                        .padding(
                                            horizontal = 7.dp,
                                            vertical = 3.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "彭祖",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["PengZu"]
                                        ?: "无",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp
                        ),
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(
                                    1f
                                )
                                .clip(
                                    MaterialTheme.shapes.small
                                )
                                .clickable { },
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        12.dp
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFFFF2442
                                            ),
                                            CircleShape
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "喜",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayXi"]
                                        ?: "喜神方位",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(
                                    1f
                                )
                                .clip(
                                    MaterialTheme.shapes.small
                                )
                                .clickable { },
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        12.dp
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFFFFD100
                                            ),
                                            CircleShape
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "财",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayCai"]
                                        ?: "财神方位",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(
                                    1f
                                )
                                .clip(
                                    MaterialTheme.shapes.small
                                )
                                .clickable { },
                            shape = MaterialTheme.shapes.small,
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        12.dp
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(
                                    8.dp
                                ),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(
                                                0xFF8080FF
                                            ),
                                            CircleShape
                                        )
                                        .padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "福",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                                Text(
                                    text = dateState["DayFu"]
                                        ?: "福神方位",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    Spacer(
                        modifier = Modifier.height(
                            18.dp
                        )
                    )
                }
            }
        }
        updateInfo?.let { update ->
            TipsDialog(
                isOpen = showUpdateDialog.value,
                onClose = { showUpdateDialog.value = false },
                title = "发现新版本",
                message =
                    "版本：${update.versionName}\n" +
                            "大小：${
                                Formatter.formatFileSize(
                                    context,
                                    update.appSize ?: 0L
                                )
                            }\n\n更新日志：\n${update.changeLog ?: "修复了一些已知问题"}",
                confirmButtonText = "更新",
                onConfirm = {
                    update.downloadUrl?.let {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, it.toUri())
                        )
                    }
                },
                dismissButtonText = "复制链接",
                onDismiss = {
                    clipboardManager.setPrimaryClip(
                        ClipData.newPlainText("下载链接", update.downloadUrl)
                    )
                    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
                },
                closeIcon = true,
                onCloseIconClick = { showUpdateDialog.value = false }
            )
        }
    }
}

fun calculateTimeToNextHour(): Long {
    val cal =
        Calendar.getInstance()
    cal.add(
        Calendar.HOUR_OF_DAY,
        1
    )
    cal.set(
        Calendar.MINUTE,
        0
    )
    cal.set(
        Calendar.SECOND,
        0
    )
    cal.set(
        Calendar.MILLISECOND,
        0
    )
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
            .clip(
                MaterialTheme.shapes.small
            )
            .clickable { onClick() },
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    12.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                12.dp
            ),
        ) {
            Icon(
                painter = painterResource(
                    iconRes
                ),
                contentDescription = null,
                modifier = Modifier.size(
                    28.dp
                ),
                tint = MaterialTheme.colorScheme.onSurface,
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
            .padding(
                vertical = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                8.dp
            ),
        ) {
            Icon(
                painter = painterResource(
                    iconRes
                ),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onSurface,
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