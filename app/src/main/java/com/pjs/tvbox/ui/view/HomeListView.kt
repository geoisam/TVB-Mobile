package com.pjs.tvbox.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pjs.tvbox.data.DouBanHotData
import com.pjs.tvbox.data.DouBanRecData
import com.pjs.tvbox.data.DouBanTvData
import com.pjs.tvbox.data.QiYiAnimeData
import com.pjs.tvbox.data.QiYiStoryData
import com.pjs.tvbox.ui.view.card.DouBanHotCard
import com.pjs.tvbox.ui.view.card.DouBanTvCard
import com.pjs.tvbox.ui.view.card.QiYiHotCard
import com.pjs.tvbox.ui.viewmodel.HomeListVMFactory
import com.pjs.tvbox.ui.viewmodel.HomeListViewModel

@Composable
fun <T> HomeListView(
    viewModelKey: String,
    loader: suspend () -> List<T>,
    itemKey: (T) -> Any,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeListViewModel<T> = viewModel(
        key = viewModelKey,
        factory = HomeListVMFactory(loader)
    )

    val items by viewModel.items.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> CircularProgressIndicator()

            error != null -> Text(
                text = "加载失败\n\n$error",
                color = Color.Gray
            )

            items.isEmpty() -> Text(
                text = "暂无数据",
                color = Color.Gray
            )

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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = items,
                        key = itemKey
                    ) { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}


@Composable
fun DouBanTopView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "DouBanTopView",
        loader = { DouBanTvData.getDouBanTv() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            DouBanTvCard(movie,2)
        },
        modifier = modifier
    )
}

@Composable
fun DouBanRecView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "DouBanRecView",
        loader = { DouBanRecData.getDouBanRec() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            DouBanHotCard(movie)
        },
        modifier = modifier
    )
}

@Composable
fun DouBanHotView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "DouBanHotView",
        loader = { DouBanHotData.getDouBanHot() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            DouBanHotCard(movie)
        },
        modifier = modifier
    )
}

@Composable
fun DouBanTvView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "DouBanTvView",
        loader = { DouBanTvData.getDouBanTv() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            DouBanTvCard(movie)
        },
        modifier = modifier
    )
}

@Composable
fun QiYiAnimeView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "QiYiAnimeView",
        loader = { QiYiAnimeData.getQiYiAnime() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            QiYiHotCard(movie)
        },
        modifier = modifier
    )
}

@Composable
fun QiYiStoryView(
    modifier: Modifier = Modifier
) {
    HomeListView(
        viewModelKey = "QiYiStoryView",
        loader = { QiYiStoryData.getQiYiStory() },
        itemKey = { it.id ?: it.hashCode() },
        itemContent = { movie ->
            QiYiHotCard(movie)
        },
        modifier = modifier
    )
}