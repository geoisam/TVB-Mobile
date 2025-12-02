package com.pjs.tvbox.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.data.CMDatabaseData
import com.pjs.tvbox.model.NationalSales
import com.pjs.tvbox.model.Ticket
import com.pjs.tvbox.util.LunarUtil
import com.pjs.tvbox.util.LunarUtil.toDateString
import kotlinx.coroutines.delay

@Composable
fun CMDatabaseView(
    modifier: Modifier = Modifier,
    selectedDate: String,
    isToday: Boolean,
    key: Int? = null,
) {
    val context = LocalContext.current
    var tickets by remember { mutableStateOf<List<Ticket>>(emptyList()) }
    var national by remember { mutableStateOf<NationalSales?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key, selectedDate) {
        isLoading = true
        error = null

        try {
            val (list, nat) = CMDatabaseData.getCMDatabase(selectedDate)
            tickets = list
            national = nat
        } catch (e: Exception) {
            error = e.message ?: "加载失败"
        } finally {
            isLoading = false
        }

        if (isToday) {
            while (true) {
                delay(10_666)
                try {
                    val (list, nat) = CMDatabaseData.getCMDatabase(selectedDate)
                    tickets = list
                    national = nat
                } catch (e: Exception) {
                }
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }

            tickets.isEmpty() -> {
                Text(
                    text = "暂无数据",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            }

            else -> {
                Column(Modifier.fillMaxSize()) {
                    val horizontalScrollState = rememberScrollState()
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                    ) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "今日大盘",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.Bottom,
                                ) {
                                    Text(
                                        text = national?.salesDesc ?: "null",
                                        style = MaterialTheme.typography.displayMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = national?.salesUnit ?: "万",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Text(
                                    text = if (isToday)
                                        "截止 ${national?.updateTimestamp?.toDateString()} (北京时间)"
                                    else
                                        "历史数据 $selectedDate (北京时间)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 12.dp),
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
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
                                        text = "影片名",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .width(160.dp)
                                            .padding(horizontal = 16.dp),
                                        textAlign = TextAlign.Start,
                                    )
                                    Row(
                                        modifier = Modifier.horizontalScroll(horizontalScrollState)
                                    ) {
                                        TableHeader("总票房")
                                        TableHeader("综合票房")
                                        TableHeader("票房占比")
                                        TableHeader("排片占比")
                                        TableHeader("网售占比")
                                        TableHeader("上座率")
                                    }
                                }
                            }
                        }
                        items(tickets) { ticket ->
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    Modifier
                                        .width(160.dp)
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = ticket.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Start,
                                    )
                                    Text(
                                        text = when {
                                            !ticket.releaseDesc.isNullOrBlank() && "点映" !in ticket.releaseDesc -> "${ticket.releaseDesc}上映"
                                            ticket.releaseDays < 0 -> "${-ticket.releaseDays} 天后上映"
                                            ticket.releaseDays == 0 -> "今天上映"
                                            else -> "已上映 ${ticket.releaseDays} 天"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Row(
                                    modifier = Modifier.horizontalScroll(horizontalScrollState)
                                ) {
                                    TableCell(ticket.sumSalesDesc)
                                    TableCell("${ticket.salesInWanDesc}万")
                                    TableCell(ticket.salesRateDesc)
                                    TableCell(ticket.sessionRateDesc)
                                    TableCell(ticket.onlineSalesRateDesc)
                                    TableCell(ticket.seatRateDesc)
                                }
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


@Composable
private fun TableHeader(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .width(120.dp)
            .padding(horizontal = 16.dp),
        textAlign = TextAlign.End,
    )
}

@Composable
private fun TableCell(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .width(120.dp)
            .padding(horizontal = 16.dp),
        textAlign = TextAlign.End,
    )
}