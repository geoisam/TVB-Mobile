package com.pjs.tvbox.ui.dialog

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId

@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit,
    minDate: LocalDate,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis(),
            yearRange = 2017..LocalDate.now().year
        )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val date = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        if (date >= minDate) {
                            onDateSelected(
                                date.year,
                                date.monthValue - 1,
                                date.dayOfMonth
                            )
                        }
                    }
                }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("取消")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}