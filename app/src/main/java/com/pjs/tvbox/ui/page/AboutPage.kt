package com.pjs.tvbox.ui.page

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.util.AppUtil
import androidx.core.net.toUri
import com.pjs.tvbox.data.APP_AUTHOR
import com.pjs.tvbox.data.APP_AUTHOR_SAY
import com.pjs.tvbox.data.GITHUB_REPO
import com.pjs.tvbox.ui.theme.LogoFont
import java.time.LocalDate
import java.time.format.DateTimeFormatter

sealed class AboutScreen {
    object Main :
        AboutScreen()

    data class Markdown(
        val file: String,
        val title: Int
    ) : AboutScreen()
}

@Composable
fun AboutPage(
    onBack: () -> Unit,
    title: Int,
) {
    var current by remember {
        mutableStateOf<AboutScreen>(
            AboutScreen.Main
        )
    }

    BackHandler(
        enabled = true
    ) {
        if (current == AboutScreen.Main) {
            onBack()
        } else {
            current =
                AboutScreen.Main
        }
    }

    when (current) {
        AboutScreen.Main -> AboutMain(
            title = title,
            onBack = onBack,
            onOpenMarkdown = { file, title ->
                current =
                    AboutScreen.Markdown(
                        file,
                        title
                    )
            }
        )

        is AboutScreen.Markdown -> {
            val m =
                current as AboutScreen.Markdown
            MarkdownPage(
                pageTitle = m.title,
                assetFile = m.file,
                onBack = {
                    current =
                        AboutScreen.Main
                }
            )
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
private fun AboutMain(
    title: Int,
    onBack: () -> Unit,
    onOpenMarkdown: (file: String, title: Int) -> Unit,
) {
    val context =
        LocalContext.current
    val appVersionName =
        AppUtil.getVersionName(
            context
        )
    val appVersionCode =
        AppUtil.getVersionCode(
            context
        )

    val appName =
        stringResource(
            R.string.app_name
        )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            title
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
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    GITHUB_REPO.toUri()
                                )
                            )
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_github
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(
                                24.dp
                            ),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        },
    ) { padding ->
        LazyColumn(
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
            )
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(
                        modifier = Modifier.height(
                            12.dp
                        )
                    )
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_logo_fill
                        ),
                        contentDescription = null,
                        tint = Color(
                            0xFFF5F5F5
                        ),
                        modifier = Modifier
                            .size(
                                78.dp
                            )
                            .background(
                                Color(
                                    0xFF1B1F23
                                ),
                                MaterialTheme.shapes.medium
                            )
                    )
                    Spacer(
                        modifier = Modifier.height(
                            12.dp
                        )
                    )
                    Text(
                        text = appName,
                        fontFamily = LogoFont,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "v$appVersionName.$appVersionCode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(
                    modifier = Modifier.height(
                        12.dp
                    )
                )
            }
            item {
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
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_compare
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(
                                    28.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    R.string.about_changelog
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
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
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_archive
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(
                                    28.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    R.string.about_releases
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
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
                            Icon(
                                painter = painterResource(
                                    R.drawable.ic_debug
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(
                                    28.dp
                                ),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(
                                    R.string.about_debug
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
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
                                        R.string.about_sponsor
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_arrow_right
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(
                                        24.dp
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier.clickable {
                                onOpenMarkdown(
                                    "md/Sponsor.md",
                                    R.string.about_sponsor
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        R.string.about_thanks
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_arrow_right
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(
                                        24.dp
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier.clickable {
                                onOpenMarkdown(
                                    "md/ThanksList.md",
                                    R.string.about_thanks
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        R.string.about_disclaimer
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_arrow_right
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(
                                        24.dp
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier.clickable {
                                onOpenMarkdown(
                                    "md/Disclaimer.md",
                                    R.string.about_disclaimer
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        R.string.about_open_source
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_arrow_right
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(
                                        24.dp
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier.clickable {
                                onOpenMarkdown(
                                    "md/OpenSource.md",
                                    R.string.about_open_source
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        )
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(
                                        R.string.about_permission
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            trailingContent = {
                                Icon(
                                    painter = painterResource(
                                        R.drawable.ic_arrow_right
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(
                                        24.dp
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            modifier = Modifier.clickable {
                                onOpenMarkdown(
                                    "md/Permission.md",
                                    R.string.about_permission
                                )
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        )
                    }
                }
                Spacer(
                    modifier = Modifier.height(
                        18.dp
                    )
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp
                    ),
                ) {
                    Text(
                        text = APP_AUTHOR_SAY,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = APP_AUTHOR,
                        fontFamily = LogoFont,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Copyright ©${getYearNumber()} YOU. All Rights Reserved",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(
                    modifier = Modifier.height(
                        32.dp
                    )
                )
            }
        }
    }
}

fun getYearNumber(): Int {
    val currentDate =
        LocalDate.now()
    val formatter =
        DateTimeFormatter.ofPattern(
            "yyyy"
        )
    return currentDate.format(
        formatter
    )
        .toInt()
}