package com.example.mymusicapplication.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mymusicapplication.models.MusicRepository
import com.example.mymusicapplication.models.Song

@Composable
fun TagInputDialog(
    context: Context,
    song: Song,
    onDismiss: () -> Unit,
    onTagAdded: (String) -> Unit
) {
    var tagInput by remember { mutableStateOf("") } // State to hold the input value
    val musicRepository = MusicRepository(context = context)


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add Tag") },
        text = {
            Column {
                TextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
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
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
