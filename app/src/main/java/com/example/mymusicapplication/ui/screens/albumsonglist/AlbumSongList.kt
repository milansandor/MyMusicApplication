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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.ui.screens.albumsonglist.components.albumheader.AlbumHeader
import com.example.mymusicapplication.ui.screens.albumsonglist.components.songcard.SongCard
import com.example.mymusicapplication.viewmodels.MusicViewModel

@Composable
fun AlbumSongList(
    context: Context,
    album: Album,
    tags: SnapshotStateList<String>,
    onBackPress: () -> Unit,
    onSongClicked: (Song) -> Unit,
    selectedSong: Song?,
    isSongCurrentlyPlaying: Boolean,
    onIsSongCurrentlyPlayingChange: (Boolean) -> Unit,
    onTagAdded: (String) -> Unit,
    musicViewModel: MusicViewModel
) {
    val painter = if (album.albumArtUri != "null") {
        rememberAsyncImagePainter(model = album.albumArtUri)
    } else {
        painterResource(id = R.drawable.album_cover_palceholer_v2)
    }

    Column (modifier = Modifier
        .fillMaxSize()
    ) {
        IconButton(onClick = onBackPress) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                    onTagAdded = onTagAdded,
                    showMoreVertIcon = index == 0,
                    musicViewModel = musicViewModel
                )
            }
        }
    }
}
