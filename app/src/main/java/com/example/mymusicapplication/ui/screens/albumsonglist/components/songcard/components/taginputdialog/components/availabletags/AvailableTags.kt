package com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.components.taginputdialog.components.availabletags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AvailableTags(
    tags: List<String>,
    selectedTags: MutableList<String>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Add existing tags:",
            fontWeight = FontWeight.Bold,
        )
    }

    LazyColumn {
        items(tags.sortedBy { it.lowercase() }) {tag ->
            val isChecked = selectedTags.contains(tag)
            SelectableTag(
                tag = tag,
                checked = isChecked,
                onCheckedChange = { checked ->
                    if (checked) {
                        selectedTags.add(tag)
                    } else {
                        selectedTags.remove(tag)
                    }
                }
            )
        }
    }
}