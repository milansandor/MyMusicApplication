package com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.components.taginputdialog

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.models.SongCacheManager
import com.example.mymusicapplication.controllers.updateGenre
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.models.SongUpdateInfo
import com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.components.taginputdialog.components.availabletags.AvailableTags
import com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.components.taginputdialog.components.songtagscontainer.SongTagsContainer
import com.example.mymusicapplication.viewmodels.MusicViewModel
import kotlinx.coroutines.launch

@Composable
fun TagInputDialog(
    context: Context,
    song: Song,
    tags: SnapshotStateList<String>,
    album: Album,
    onDismiss: () -> Unit,
    onTagAdded: (String) -> Unit,
    musicViewModel: MusicViewModel
) {
    val songTags = remember { mutableStateListOf<String>().apply { addAll(song.genre.split(";")) } }
    val selectedTags = remember { mutableStateListOf<String>() }
    val newTag = remember { mutableStateOf("") }
    val brandNewTags = remember { mutableStateListOf<String>() }
    val songCacheManager = SongCacheManager(context)

    val recoverablePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                musicViewModel.retryPendingOperation()
            } else {
                Log.e("MainActivity", "User declined permission request")
            }
        }
    )

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
                        if (brandNewTags.contains(tag)) {
                            brandNewTags.remove(tag)
                        }
                    }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        value = newTag.value,
                        onValueChange = { newTag.value = it },
                        placeholder = { Text(text = "Add new tag") },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (newTag.value.isNotBlank() && !songTags.contains(newTag.value)) {
                                songTags.add(newTag.value)
                                brandNewTags.add(newTag.value)
                                newTag.value = "" // Clear the input field after adding
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

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
                        val newTags = if (songTags.isNotEmpty()) {
                            (songTags + selectedTags).distinct()
                        } else {
                            selectedTags.distinct()
                        }

                        val newTagString = newTags.joinToString(";")
                        val updatedAlbumSongsInfo = mutableListOf<SongUpdateInfo>()
                        album.songs.forEach { song ->
                            updatedAlbumSongsInfo.add(SongUpdateInfo(song.id, song.data, newTagString))
                            songCacheManager.updateCachedSongGenre(song.id.toString(), newTagString)
                            song.genre = newTagString
                        }

                        updateGenre(context, updatedAlbumSongsInfo, recoverablePermissionLauncher)

                        val checkGenreExistList = album.genre.split(";").toMutableList()
                        newTags.forEach {
                            if (!checkGenreExistList.contains(it)) {
                                checkGenreExistList.add(it)
                            }
                        }

                        brandNewTags.forEach { tag ->
                            onTagAdded(tag)
                        }

                        val songGenresInAlbum = album.songs.flatMap { song -> song.genre.split(";").map { it.trim() } }.toSet()

                        checkGenreExistList.removeAll { genre -> genre !in songGenresInAlbum }

                        album.genre = checkGenreExistList.joinToString(";")

                        Log.d("UPDATED_SONG", song.toString())
                        Log.d("UPDATED_AlBUM_GENRE", album.genre)
                        Log.d("UPDATED_SONG_GENRE_ITEMS", newTags.joinToString(";"))

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
