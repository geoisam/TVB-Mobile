package com.pjs.tvbox.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.ui.view.MarkdownView

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun MarkdownPage(
    pageTitle: Int,
    assetFile: String,
    onBack: () -> Unit = {}
) {
    BackHandler(
        onBack = onBack
    )
    val context =
        LocalContext.current

    val markdownText =
        remember(
            assetFile
        ) {
            runCatching {
                context.assets.open(
                    assetFile
                )
                    .bufferedReader()
                    .use { it.readText() }
            }
                .getOrDefault(
                    "加载失败"
                )
        }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            pageTitle
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_back
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(
                                24.dp
                            ),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
            )
        }
    ) { padding ->
        MarkdownView(
            markdown = markdownText.trimIndent(),
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    padding
                )
                .padding(
                    horizontal = 16.dp
                ),
        )
    }
}