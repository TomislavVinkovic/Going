package com.example.going.view.MyEventsScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.going.R
import com.example.going.view.AppMainScreenPlaceholder

@Composable
fun MyEventsScreen(navController: NavController) {
    AppMainScreenPlaceholder(stringResource(R.string.screen_title_my_events))
}