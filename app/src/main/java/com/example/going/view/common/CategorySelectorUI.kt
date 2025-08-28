package com.example.going.view.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.going.util.CategoryIcons.CategoryIcons
import com.example.going.view.util.TranslateCategory
import com.example.going.viewmodel.SearchViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelectorUI(
    selectedCategory: String?,
    onClick: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp), // Space between chips on the same line
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        CategoryIcons.keys.forEach { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onClick(category) },
                label = { Text(text = TranslateCategory(category)) },
                leadingIcon = {Text(CategoryIcons[category] ?: "")},
            )
        }
    }
}