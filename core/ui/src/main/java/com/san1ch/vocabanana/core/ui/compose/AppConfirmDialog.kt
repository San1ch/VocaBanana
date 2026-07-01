package com.san1ch.vocabanana.core.ui.compose

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.san1ch.vocabanana.core.ui.R

@Composable
fun AppConfirmDialog(
    title: String,
    text: String,
    confirmText: String = "Yes",
    dismissText: String = "No",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.headlineSmall) },
        text = { Text(text = text, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

@Composable
fun <T> DeleteConfirmDialog(
    item: T?,
    title: String = stringResource(R.string.delete),
    text: String = "Are you sure you want to delete this? This action is permanent.",
    onDismiss: () -> Unit,
    onConfirm: (T) -> Unit
) {
    if (item != null) {
        AppConfirmDialog(
            title = title,
            text = text,
            confirmText = stringResource(R.string.delete),
            onConfirm = { onConfirm(item) },
            onDismiss = onDismiss
        )
    }
}