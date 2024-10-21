package com.example.mymusicapplication.controllers.songplayercontroller

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    onBackPress: () -> Unit,
    onSongClicked: (Song) -> Unit,
    selectedSong: Song?,
) {
    val painter = if (album.albumArtUri != null) {
        rememberAsyncImagePainter(model = album.albumArtUri)
    } else {
        painterResource(id = R.drawable.test_cover)
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

        var currentSongId by remember {
            mutableStateOf<Long?>(null)
        }
        var currentSongTitle by remember {
            mutableStateOf<String?>(null)
        }

        val sortedSongs = album.songs.sortedBy { it.track.toInt() }

        LazyColumn {
            items(sortedSongs) {song ->
                val isPlaying = currentSongId == song.id
                SongCard(
                    context = context,
                    song = song,
                    isPlaying = isPlaying,
                    isSelected = song == selectedSong,
                    onClick = {
                        if (isPlaying) {
                            stopCurrentSong()
                            currentSongId = null
                            currentSongTitle = null
                        } else {
                            playSong(song)
                            onSongClicked(song)
                            currentSongId = song.id
                            currentSongTitle = song.title
                        }
                    },
                    onTagAdded = { newTag ->
                        println("New tag added: $newTag")
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
    isPlaying: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTagAdded: (String) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    var showInputDialog by remember {
        mutableStateOf(false)
    }
    var newTag by remember {
        mutableStateOf("")
    }

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
                    text = song.track,
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

        // Spacer pushing the icon to the far right
        Spacer(modifier = Modifier.width(16.dp))

        // MoreVert icon
        Icon(
            Icons.Default.MoreVert, contentDescription = "song edit",
            modifier = Modifier
                .clickable {
                    expanded = true
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = "Add Tag")
                },
                onClick = {
                    expanded = false
                    showInputDialog = true
                }
            )
        }

        if (showInputDialog) {
            Log.i("ADD_TAG_TO_SONG", "${song.title}")
            TagInputDialog(
                context = context,
                song = song,
                onDismiss = {
                    showInputDialog = false
                },
                onTagAdded = { tag ->
                    onTagAdded(tag)
                    showInputDialog = false
                    newTag = ""
                }
            )
        }
    }
}
