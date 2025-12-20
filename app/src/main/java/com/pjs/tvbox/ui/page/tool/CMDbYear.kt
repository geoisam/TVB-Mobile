package com.pjs.tvbox.ui.page.tool

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.ui.view.CMDbYearView
import java.util.Calendar

sealed class CMDbYearScreen {
    object Main : CMDbYearScreen()
}

@Composable
fun CMDbYear(
    onBack: () -> Unit,
    title: Int,
) {
    var current by remember {
        mutableStateOf<CMDbYearScreen>(
            CMDbYearScreen.Main
        )
    }

    var selectedYear by remember {
        mutableIntStateOf(1)
    }

    BackHandler(enabled = true) {
        if (current == CMDbYearScreen.Main) {
            onBack()
        } else {
            current = CMDbYearScreen.Main
        }
    }

    when (current) {
        CMDbYearScreen.Main -> CMDbYearMain(
            onBack = onBack,
            title = title,
            selectedYear = selectedYear,
            onYearSelected = { selectedYear = it }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CMDbYearMain(
    onBack: () -> Unit,
    title: Int,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit,
) {
    var showYearDialog by remember { mutableStateOf(false) }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = listOf(1) + (2017..currentYear).toList().reversed()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showYearDialog = true }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CMDbYearView(
                modifier = Modifier.weight(1f),
                selectedYear = selectedYear,
            )
        }
    }

    if (showYearDialog) {
        YearSelectDialog(
            years = years,
            selectedYear = selectedYear,
            onSelect = onYearSelected,
            onDismiss = { showYearDialog = false }
        )
    }
}

@Composable
private fun YearSelectDialog(
    years: List<Int>,
    selectedYear: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "选择年份",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                items(years.size) { index ->
                    val year = years[index]

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (year == 1) "全部" else year.toString(),
                                fontWeight =
                                    if (year == selectedYear)
                                        FontWeight.Bold
                                    else
                                        FontWeight.Normal
                            )
                        },
                        onClick = {
                            onSelect(year)
                            onDismiss()
                        }
                    )
                }
            }
        },
        confirmButton = {}
    )
}
