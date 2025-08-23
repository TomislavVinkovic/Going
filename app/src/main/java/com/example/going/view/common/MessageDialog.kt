package com.example.going.view.common

import android.R.attr.text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.example.going.R

@Composable
fun MessageDialog(
    onClose: () -> Unit,
    message: String
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { stringResource(R.string.app_name) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick=onClose,) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}