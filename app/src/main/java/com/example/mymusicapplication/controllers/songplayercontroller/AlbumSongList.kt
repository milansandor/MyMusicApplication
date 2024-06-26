package com.example.mymusicapplication.controllers.songplayercontroller

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.MusicPlayerViewModel
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.setCurrentSongTitle
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song

@Composable
fun AlbumSongList(album: Album, onBackPress: () -> Unit) {
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
//                var isPlaying = false

                SongCard(
                    song = song,
                    isPlaying = isPlaying,
                    onClick = {
                        //SmusicPlayerViewModel.playSong(song)
                        if (isPlaying) {
                            stopCurrentSong()
                            currentSongId = null
                            currentSongTitle = null
                        } else {
                            playSong(song)
                            setCurrentSongTitle(song.title)
                            currentSongId = song.id
                            currentSongTitle = song.title
                        }
//                        !isPlaying
                    }
                )
            }
        }
    }
}

@Composable
fun SongCard(song: Song, isPlaying: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        if (isPlaying) Color.Green else Color.LightGray
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
        Column {
            Row {
                Column(
                    modifier = Modifier
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = song.track,
                    )
                }
                Column {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.MoreVert, contentDescription = "song edit")
    }
}