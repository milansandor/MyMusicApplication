package com.example.mymusicapplication.ui.screens.albumsonglist

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.ui.screens.albumsonglist.components.albumheader.AlbumHeader
import com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.SongCard

@Composable
fun AlbumSongList(
    context: Context,
    album: Album,
    tags: List<String>,
    onBackPress: () -> Unit,
    onSongClicked: (Song) -> Unit,
    selectedSong: Song?,
    isSongCurrentlyPlaying: Boolean,
    onIsSongCurrentlyPlayingChange: (Boolean) -> Unit,
    onTagAdded: (String) -> Unit
) {
    val songsState = remember { mutableStateListOf<Song>().apply { addAll(album.songs) } }

    val painter = if (album.albumArtUri != "null") {
        rememberAsyncImagePainter(model = album.albumArtUri)
    } else {
        painterResource(id = R.drawable.album_cover_palceholer_v2)
    }

    Column (modifier = Modifier
        .fillMaxSize()
    ) {
        IconButton(onClick = onBackPress) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        AlbumHeader(albumCover = painter, albumName = album.albumName, artistName = album.artistName)

        val sortedSongs = album.songs.sortedBy { it.track }

        LazyColumn {
            itemsIndexed(sortedSongs) {index, song ->
                val isPlaying = song == selectedSong && isSongCurrentlyPlaying
                SongCard(
                    context = context,
                    song = song,
                    isSelected = song == selectedSong,
                    genre = song.genre.value,
                    tags = tags,
                    album = album,
                    onClick = {
                        if (isPlaying) {
                            stopCurrentSong()
                            onIsSongCurrentlyPlayingChange(false)
                        } else {
                            playSong(song)
                            onSongClicked(song)
                            onIsSongCurrentlyPlayingChange(true)
                        }
                    },
                    onTagAdded = { newTag ->
                        onTagAdded(newTag)
                    },
                    onSongUpdated = { updatedSong ->
                        val index = songsState.indexOfFirst { it.id == updatedSong.id }
                        if (index != -1) {
                            songsState[index] = updatedSong
                        }
                    },
                    showMoreVertIcon = index == 0
                )
            }
        }
    }
}
