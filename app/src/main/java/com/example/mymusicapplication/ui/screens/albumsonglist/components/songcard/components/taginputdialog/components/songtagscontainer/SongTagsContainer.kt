package com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.components.taginputdialog.components.songtagscontainer

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SongTagsContainer(
    songTags: List<String>,
    onTagRemoved: (String) -> Unit,
) {
    Text(
        text = "Songs' tags:",
        fontWeight = FontWeight.Bold,
    )

    LazyColumn(
        modifier = Modifier.heightIn(max = 150.dp),
    ) {
        items(songTags) {tag ->
            SongTag(
                tag = tag,
                onTagRemoved = onTagRemoved
            )
        }
    }
}
