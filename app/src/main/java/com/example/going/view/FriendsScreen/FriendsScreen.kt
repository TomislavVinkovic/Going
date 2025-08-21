package com.example.going.view.FriendsScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.going.R
import com.example.going.view.AppMainScreenPlaceholder

@Composable
fun FriendsScreen(navController: NavController) {
    AppMainScreenPlaceholder(stringResource(R.string.placeholder_friends))
}