package com.example.mymusicapplication.controllers.songplayercontroller.songTagController

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SongTagsContainer(
    genres: List<String>,
) {
    Text(
        text = "Existing tags:",
        fontWeight = FontWeight.Bold,
    )

    LazyColumn{
        items(genres) {genre ->
            SongTag(genre = genre)
        }
    }
}

@Composable
fun SongTag(
    genre: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = genre,
                fontSize = 12.sp,
            )
        }
        IconButton(
            onClick = { /* TODO: remove genre tag from the song */ }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
fun AvailableTags(
    tags: List<String>,
) {
    Text(
        text = "Add existing tags:",
        fontWeight = FontWeight.Bold,
    )

    LazyColumn {
        items(tags) {tag ->
            SelectableTag(tag = tag)
        }
    }
}

@Composable
fun SelectableTag(
    tag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Checkbox(
            checked = false,
            onCheckedChange = { /* TODO: add genre tag to the song */ },
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tag,
            fontSize = 12.sp,
        )
    }
}
