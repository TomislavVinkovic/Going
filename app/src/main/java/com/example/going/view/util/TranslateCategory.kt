package com.example.going.view.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.going.R

@Composable
fun TranslateCategory(category: String): String {
    return when (category.lowercase()) {
        "party" -> stringResource(id = R.string.category_party)
        "concert" -> stringResource(id = R.string.category_concert)
        "sports" -> stringResource(id = R.string.category_sports)
        "rave" -> stringResource(id = R.string.category_rave)
        "food" -> stringResource(id = R.string.category_food)
        "conference" -> stringResource(id = R.string.category_conference)
        else -> stringResource(id = R.string.category_other)
    }
}