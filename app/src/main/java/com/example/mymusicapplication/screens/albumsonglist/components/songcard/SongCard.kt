package com.example.mymusicapplication.screens.albumsonglist.components.songcard

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.screens.albumsonglist.components.songcard.components.taginputdialog.TagInputDialog

@Composable
fun SongCard(
    context: Context,
    song: Song,
    genre: String,
    tags: List<String>,
    album: Album,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTagAdded: (String) -> Unit,
    onSongUpdated: (Song) -> Unit,
    showMoreVertIcon: Boolean
) {
    var showInputDialog by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        if (isSelected) Color.Green else Color.Transparent, label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                onClick()
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Texts for track and title in one row
        Column(
            modifier = Modifier
                .weight(1f) // Allows the column to take up available space, pushing the icon to the end
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Track text
                Text(
                    text = song.track.toString(),
                    modifier = Modifier
                        .padding(end = 8.dp) // Padding to separate track from title
                )

                // Title text
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f) // Title will take available width and ellipsis if needed
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // MoreVert icon
        if (showMoreVertIcon) {
            Icon(
                Icons.Default.MoreVert, contentDescription = "song edit",
                modifier = Modifier
                    .clickable {
                        showInputDialog = true
                    }
            )
        }

        if (showInputDialog) {
            TagInputDialog(
                context = context,
                song = song,
                genre = genre,
                tags = tags,
                album = album,
                onDismiss = {
                    showInputDialog = false
                },
                onTagAdded = { tag ->
                    onTagAdded(tag)
                },
                onSongUpdated = { updatedSong ->
                    onSongUpdated(updatedSong)
                    showInputDialog = false
                }
            )
        }
    }
}