package com.example.going.view.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun MessageDialog(
    onClose: () -> Unit,
    message: String
) {
    Dialog(onDismissRequest = onClose) {
        Text(message)
    }
}