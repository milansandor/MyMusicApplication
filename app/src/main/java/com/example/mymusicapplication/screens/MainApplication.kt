package com.example.mymusicapplication.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.screens.albumslist.AlbumListContainer
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.setOnSongEndListener
import com.example.mymusicapplication.screens.albumsonglist.AlbumSongList
import com.example.mymusicapplication.screens.songmanager.SongManagerComposable
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.controllers.updateGenre
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.models.SongUpdateInfo
import kotlinx.coroutines.launch

@Composable
fun MainApplication(albums: List<Album>, context: Context) {
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var currentlyPlayingAlbum by remember { mutableStateOf<Album?>(null) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var isModalOpen by remember { mutableStateOf(false) }
    var isSongPlaying by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // tags and checked tags
    val genreTags = mutableSetOf<String>()
    albums.forEach { album ->
        genreTags.addAll(album.genre.value.split(';'))
    }

    val tags = remember {
        mutableStateListOf(*genreTags.toTypedArray())
    }
    Log.i("album tags", "${tags.toList()}")

    val checkedTags = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(tags) {
        tags.forEach { tag ->
            if (checkedTags[tag] == null) {
                checkedTags[tag] = false
            }
        }
    }

    val onAddTag: (String) -> Unit = { newTag ->
        if (newTag.isNotBlank() && !tags.contains(newTag)) {
            tags.add(newTag)
            checkedTags[newTag] = false
        }
    }

    val onRemoveTag: (String) -> Unit = { tag ->
        // Create a list to store the relevant song information
        val songsToUpdate = mutableListOf<SongUpdateInfo>()

        // Loop through each album and each song to check which songs have the tag
        albums.forEach { album ->
            album.songs.forEach { song ->
                val songGenres = song.genre.value.split(";").map { it.trim() }
                if (songGenres.contains(tag)) {
                    // If the song's genre contains the tag, prepare updated genre and add to the list
                    val updatedGenre = song.genre.value
                        .split(";")
                        .filter { it.trim() != tag } // Remove the tag from the genre
                        .joinToString(";")

                    // Add song id, data, and updated genre to the list
                    songsToUpdate.add(SongUpdateInfo(song.id, song.data, updatedGenre))
                }
            }
        }

        // Now remove the tag from the tags and checkedTags lists
        tags.remove(tag)
        checkedTags.remove(tag)

        // Call updateGenre for the list of songs to update
        if (songsToUpdate.isNotEmpty()) {
            // Pass the list of songs to the updateGenre function
            scope.launch {
                updateGenre(context = context, songsToUpdate = songsToUpdate)
            }
        }

        // Update the genre in the album
        albums.forEach { album ->
            // Split the album's genre and filter out the removed tag
            val updatedGenres = album.genre.value
                .split(";")
                .filter { it.trim() != tag }
                .joinToString(";")

            // Update the album's genre value
            album.genre.value = updatedGenres
        }

        // Update the genre in each song of the album
        albums.forEach { album ->
            album.songs.forEach { song ->
                // Filter out the removed tag from the song's genre
                val updatedSongGenres = song.genre.value
                    .split(";")
                    .filter { it.trim() != tag }
                    .joinToString(";")

                // Update the song's genre value
                song.genre.value = updatedSongGenres
            }
        }

        // Optional: you might want to log the changes for debugging
        Log.i("TAG_REMOVAL", "Removed tag: $tag")
    }

    val onSongEnd: () -> Unit = {
        val songList = currentlyPlayingAlbum?.songs?.sortedBy { it.track } ?: emptyList()
        val currentIndex = songList.indexOfFirst { it.title == selectedSong?.title }
        val nextIndex = currentIndex + 1
        if (nextIndex < songList.size) {
            val nextSong = songList[nextIndex]
            selectedSong = nextSong
            playSong(nextSong)
        } else {
            // No more songs in the album
            stopCurrentSong()
            selectedSong = null
        }
    }

    setOnSongEndListener(onSongEnd)

    val activeTags = checkedTags.filterValues { it }.keys

    val filteredAlbums = if (activeTags.isEmpty()) {
        albums
    } else {
        albums.filter { album ->
            activeTags.all { tag -> album.genre.value.contains(tag) }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(100.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black,
                contentPadding = PaddingValues(0.dp),
            ) {
                SongManagerComposable(
                    currentSong = selectedSong,
                    currentlyPlayingAlbum = currentlyPlayingAlbum,
                    onSongChange = { newSong ->
                        stopCurrentSong()
                        selectedSong = newSong
                        playSong(selectedSong!!)
                        Log.i("NEW_SONG", "$newSong")
                    },
                    onTagSearchClick = { isModalOpen = !isModalOpen },
                    isSongCurrentlyPlaying = isSongPlaying,
                    onIsSongCurrentlyPlayingChange = { newValue ->
                        isSongPlaying = newValue
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Color.White),
        ) {
            if (selectedAlbum == null) {
                TagSearchModal(
                    tags = tags,
                    checkedTags = checkedTags,
                    onCheckedTagChange = { tag, isChecked ->
                        checkedTags[tag] = isChecked
                        Log.i("checked:", checkedTags.toString())
                    },
                    isModalOpen = isModalOpen,
                    onDismiss = { isModalOpen = false },
                    onAddTag = onAddTag,
                    onRemoveTag = onRemoveTag
                )
            }

            Row {
                if (selectedAlbum == null) {
                    AlbumListContainer(albums = filteredAlbums, selectedSong = selectedSong) { album ->
                        selectedAlbum = album
                    }
                } else {
                    AlbumSongList(
                        context = context,
                        album = selectedAlbum!!,
                        tags = tags,
                        onBackPress = {
                            selectedAlbum = null
                        },
                        onSongClicked = { song ->
                            selectedSong = song
                            currentlyPlayingAlbum = selectedAlbum
                        },
                        selectedSong = selectedSong,
                        isSongCurrentlyPlaying = isSongPlaying,
                        onIsSongCurrentlyPlayingChange = { newValue ->
                            isSongPlaying = newValue
                        },
                        onTagAdded = onAddTag,
                    )
                }
            }
        }
    }
}
