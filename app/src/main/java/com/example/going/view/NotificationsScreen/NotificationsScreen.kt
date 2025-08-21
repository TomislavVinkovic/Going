package com.example.going.view.NotificationsScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.going.R
import com.example.going.view.AppMainScreenPlaceholder

@Composable
fun NotificationsScreen(navController: NavController) {
    AppMainScreenPlaceholder(stringResource(R.string.placeholder_notifications))
}