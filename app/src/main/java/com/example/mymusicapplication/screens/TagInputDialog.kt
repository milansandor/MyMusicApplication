package com.example.mymusicapplication.screens

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.controllers.songplayercontroller.songTagController.AvailableTags
import com.example.mymusicapplication.controllers.songplayercontroller.songTagController.SongTag
import com.example.mymusicapplication.controllers.songplayercontroller.songTagController.SongTagsContainer
import com.example.mymusicapplication.models.MusicRepository
import com.example.mymusicapplication.models.Song

@Composable
fun TagInputDialog(
    context: Context,
    song: Song,
    genre: String,
    tags: List<String>,
    onDismiss: () -> Unit,
    onTagAdded: (String) -> Unit
) {
    var tagInput by remember { mutableStateOf("") } // State to hold the input value
    val musicRepository = MusicRepository(context = context)
    var listOfGenre = genre.split(";")
    var availableTags = tags.filter { !listOfGenre.contains(it) }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Manage Song's Tags") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                SongTagsContainer(genres = listOfGenre)
                AvailableTags(tags = availableTags)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tagInput.isNotBlank()) {
                        onTagAdded(";$tagInput") // Call the callback to add the tag
                        musicRepository.setNewGenreForSong(song.id, tagInput)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
