package com.example.mymusicapplication.controllers.songplayercontroller

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.screens.TagInputDialog

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
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(125.dp)
                    .height(125.dp),
            )
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = album.albumName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = album.artistName)
            }
        }

        val sortedSongs = album.songs.sortedBy { it.track.toInt() }

        LazyColumn {
            items(sortedSongs) {song ->
                val isPlaying = song == selectedSong && isSongCurrentlyPlaying
                SongCard(
                    context = context,
                    song = song,
                    isPlaying = isPlaying,
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
                    }
                )
            }
        }
    }
}

@Composable
fun SongCard(
    context: Context,
    song: Song,
    genre: String,
    tags: List<String>,
    album: Album,
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTagAdded: (String) -> Unit,
    onSongUpdated: (Song) -> Unit
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
        Icon(
            Icons.Default.MoreVert, contentDescription = "song edit",
            modifier = Modifier
                .clickable {
                    showInputDialog = true
                }
        )

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
