package com.pjs.tvbox.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pjs.tvbox.data.TicketYear
import com.pjs.tvbox.ui.viewmodel.CMDbYearViewModel

@Composable
fun CMDbYearView(
    modifier: Modifier = Modifier,
    selectedYear: Int,
    viewModel: CMDbYearViewModel = viewModel(),
) {
    val tickets by viewModel.tickets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(selectedYear) {
        viewModel.loadByYear(selectedYear)
    }

    val horizontalScroll = rememberScrollState()
    val pullState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.refresh() },
        state = pullState,
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null -> {
                    Text(
                        text = "加载失败\n\n$error",
                        modifier = Modifier
                            .align(Alignment.Center),
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                tickets.isEmpty() -> {
                    Text(
                        text = "暂无数据",
                        modifier = Modifier
                            .align(Alignment.Center),
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                else -> {
                    CMDbYearList(tickets, horizontalScroll)
                }
            }
        }
    }
}

@Composable
private fun CMDbYearList(
    tickets: List<TicketYear>,
    horizontalScrollState: androidx.compose.foundation.ScrollState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        stickyHeader {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.width(160.dp)
                ) {
                    Text(
                        text = "影片",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(
                            horizontal = 16.dp
                        ),
                        textAlign = TextAlign.Start
                    )
                }
                Row(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                ) {
                    TableHeader("总票房", 140)
                    TableHeader("平均票价")
                    TableHeader("场均人次")
                }
            }
        }

        itemsIndexed(
            tickets
        ) { index, ticket ->
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
                    .copy(alpha = 0.5f),
                thickness = 0.5.dp,
            )
            Row(
                Modifier.clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(160.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                    ) {
                        ticket.name?.let {
                            Text(
                                text = "${index + 1}.${it}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = "${ticket.premiereDate} 上映",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                ) {
                    TableCell("${ticket.salesInWan}万", 140)
                    TableCell(ticket.avgPrice)
                    TableCell(ticket.avgSalesCount)
                }
            }
        }
        item {
            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }
    }
}

