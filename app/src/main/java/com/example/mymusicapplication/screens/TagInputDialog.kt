package com.example.mymusicapplication.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.mymusicapplication.controllers.getGenre
import com.example.mymusicapplication.controllers.getSongFromMediaStore
import com.example.mymusicapplication.controllers.songplayercontroller.songTagController.AvailableTags
import com.example.mymusicapplication.controllers.songplayercontroller.songTagController.SongTagsContainer
import com.example.mymusicapplication.controllers.updateGenre
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import kotlinx.coroutines.launch

@Composable
fun TagInputDialog(
    context: Context,
    song: Song,
    genre: String,
    tags: List<String>,
    album: Album,
    onDismiss: () -> Unit,
    onTagAdded: (String) -> Unit,
    onSongUpdated: (Song) -> Unit
) {
    val songTags = remember { mutableStateListOf<String>().apply { addAll(genre.split(";")) } }
    val selectedTags = remember { mutableStateListOf<String>() }

    val scope = rememberCoroutineScope()

    val availableTags by remember(songTags, tags) {
        derivedStateOf {
            tags.filter { it.isNotBlank() && !songTags.contains(it) }
        }
    }


    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Manage Song's Tags") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                SongTagsContainer(
                    songTags = songTags,
                    onTagRemoved = { tag ->
                        songTags.remove(tag)
                    }
                )
                AvailableTags(
                    tags = availableTags,
                    selectedTags = selectedTags,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val newTags = (songTags + selectedTags).distinct()
                        val newTagString = newTags.joinToString(";")
                        updateGenre(context, song.id, song.data, newTagString)
                        val updatedGenre = getGenre(context, song.id)

                        song.genre.value = updatedGenre
                        val checkGenreExistList = album.genre.value.split(";").toMutableList()
                        newTags.forEach {
                            if (!checkGenreExistList.contains(it)) {
                                checkGenreExistList.add(it)
                            }
                        }

                        val songGenresInAlbum = album.songs.flatMap { it.genre.value.split(";").map { it.trim() } }.toSet()

                        checkGenreExistList.removeAll { genre -> genre !in songGenresInAlbum }

                        album.genre.value = checkGenreExistList.joinToString(";")

                        Log.d("UPDATED_SONG", song.toString())
                        Log.d("UPDATED_AlBUM_GENRE", album.genre.value)
                        Log.d("UPDATED_SONG_GENRE_ITEMS", selectedTags.joinToString(";"))

                        onDismiss()
                    }
                },
                enabled = true
            ) {
                Text("Apply tags")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
