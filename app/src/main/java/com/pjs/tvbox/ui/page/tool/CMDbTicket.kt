package com.pjs.tvbox.ui.page.tool

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R
import com.pjs.tvbox.ui.dialog.DatePickerDialog
import com.pjs.tvbox.ui.view.CMDbTicketView
import com.pjs.tvbox.util.LunarUtil
import java.time.LocalDate

sealed class CMDbTicketScreen {
    object Main : CMDbTicketScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CMDbTicket(
    onBack: () -> Unit,
    title: Int,
) {
    var current by remember { mutableStateOf<CMDbTicketScreen>(CMDbTicketScreen.Main) }

    BackHandler(enabled = true) {
        if (current == CMDbTicketScreen.Main) {
            onBack()
        } else {
            current = CMDbTicketScreen.Main
        }
    }

    when (current) {
        CMDbTicketScreen.Main -> CMDbTicketMain(
            onBack = onBack,
            title = title,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CMDbTicketMain(
    onBack: () -> Unit,
    title: Int,
) {
    var selectedDate by remember { mutableStateOf(LunarUtil.getYearMonthDay()) }
    var showDatePicker by remember { mutableStateOf(false) }

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
                    IconButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_calendar),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            CMDbTicketView(
                modifier = Modifier.weight(1f),
                selectedDate = selectedDate,
                isToday = (selectedDate == LunarUtil.getYearMonthDay()),
            )
        }
        if (showDatePicker) {
            DatePickerDialog(
                onDismiss = { showDatePicker = false },
                onDateSelected = { year, month, day ->
                    selectedDate = "%04d-%02d-%02d".format(year, month + 1, day)
                    showDatePicker = false
                },
                minDate = LocalDate.of(2017, 1, 1),
            )
        }
    }
}