package com.pjs.tvbox.ui.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pjs.tvbox.R

@Composable
fun TipsDialog(
    isOpen: Boolean,
    onClose: () -> Unit,
    title: String,
    message: String,
    confirmButtonText: String = "确定",
    onConfirm: (() -> Unit)? = null,
    dismissButtonText: String? = "取消",
    onDismiss: (() -> Unit)? = null,
    closeIcon: Boolean,
    onCloseIconClick: (() -> Unit)? = { onClose() }
) {
    if (isOpen) {
        AlertDialog(
            onDismissRequest = onClose,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    if (closeIcon) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onCloseIconClick ?: onClose,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "关闭",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            dismissButton = dismissButtonText?.let {
                {
                    TextButton(
                        onClick = {
                            onDismiss?.invoke()
                            onClose()
                        }
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm?.invoke()
                        onClose()
                    },
                ) {
                    Text(
                        text = confirmButtonText,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier
                .widthIn(max = 666.dp)
                .padding(horizontal = 16.dp)
        )
    }
}