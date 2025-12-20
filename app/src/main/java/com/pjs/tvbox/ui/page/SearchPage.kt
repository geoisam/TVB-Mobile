package com.pjs.tvbox.ui.page

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R

sealed class SearchScreen {
    object Main : SearchScreen()
}

@Composable
fun SearchPage(
    onBack: () -> Unit,
    title: Int,
) {
    var current by remember {
        mutableStateOf<SearchScreen>(
            SearchScreen.Main
        )
    }

    BackHandler(enabled = true) {
        if (current == SearchScreen.Main) {
            onBack()
        } else {
            current = SearchScreen.Main
        }
    }

    when (current) {
        SearchScreen.Main -> SearchMain(
            title = title,
            onBack = onBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMain(
    onBack: () -> Unit,
    title: Int,
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val searchHistory = listOf("电影1", "电影2", "电影3")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            textStyle = TextStyle(
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default
                                .copy(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    Toast.makeText(context, "搜索：$searchQuery", Toast.LENGTH_SHORT).show()
                                }
                            ),
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onSurface
                                        .copy(alpha = 0.5f),
                                    MaterialTheme.shapes.medium
                                )
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "返回",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            Toast.makeText(context, "搜索", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(
                            text = "搜索",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // 搜索历史
                Text(
                    text = "搜索历史",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                LazyColumn {
                    itemsIndexed(searchHistory) { index, item ->
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    searchQuery = item
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("热搜影片", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}