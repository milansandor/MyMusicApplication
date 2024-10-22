package com.example.mymusicapplication.controllers.songplayercontroller

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.isPlaying
import com.example.mymusicapplication.controllers.pauseSong
import com.example.mymusicapplication.controllers.resumeCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.screens.TagSearchButton


@Composable
fun SongManagerComposable(
    currentSong: Song?,
    currentlyPlayingAlbum: Album?,
    onSongChange: (Song) -> Unit,
    onTagSearchClick: () -> Unit,
) {
    val sortedSongs = currentlyPlayingAlbum?.songs?.sortedBy { it.track.toInt() }
    var songList = listOf<Song>()
    if (currentlyPlayingAlbum != null) {
        if (sortedSongs != null) {
            songList = sortedSongs
        }
    }

    fun getItem(position: Int?): Song {
        return songList[position!!]
    }

    fun findSongIndex(songsFromTheAlbum: List<Song>, song: Song?): Int? {
        for (i in songsFromTheAlbum.indices) {
            if (songsFromTheAlbum[i].title == song?.title) {
                return i
            }
        }
        return null
    }

    fun getPreviousSong(song: Song?): Song? {
        val cSongIndex = findSongIndex(songList, song)
        return if (cSongIndex != null && cSongIndex > 0) {
            getItem(cSongIndex - 1)
        } else {
            null // Return null if no previous song exists.
        }
    }

    fun getNextSong(song: Song?): Song? {
        val cSongIndex = findSongIndex(songList, song)
        return if (cSongIndex != null && cSongIndex < songList.size - 1) {
            getItem(cSongIndex + 1)
        } else {
            null // Return null if no next song exists.
        }
    }


    Log.i("CURRENT_SONG_POS", "${currentSong?.title} position is ${findSongIndex(songList, currentSong)}")

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f),
            horizontalArrangement = Arrangement.Start
        ) {
            OutlinedButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(start = 4.dp)
            ) {
                Text(text = "New Tag", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Filled.Add, contentDescription = "", Modifier.size(14.dp), tint = Color.White)
            }
        }


        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.End
        ) {
            var isSongPlaying = remember { mutableStateOf(false) }
            IconButton(onClick = {
                getPreviousSong(currentSong)?.let { previousSong ->
                    onSongChange(previousSong)
                }
            }) {
                Icon(
                    painterResource(id = R.drawable.previous),
                    contentDescription = "",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }

            IconButton(
                onClick = {
                    if (isPlaying()) {
                        pauseSong()
                        isSongPlaying.value = isPlaying()
                    } else {
                        resumeCurrentSong()
                        isSongPlaying.value = isPlaying()
                    }
                }
            ) {
                if (!isSongPlaying.value) {
                    Icon(
                        painterResource(id = R.drawable.play_button_arrowhead),
                        "",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White,
                    )
                } else {
                    Icon(
                        painterResource(id = R.drawable.pause_white),
                        "",
                        modifier = Modifier.size(14.dp),
                        tint = Color.White,
                    )
                }
            }

            IconButton(onClick = {
                getNextSong(currentSong)?.let { nextSong ->
                    onSongChange(nextSong)
                }
            }) {
                Icon(
                    painterResource(id = R.drawable.next),
                    contentDescription = "",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }

            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    painterResource(id = R.drawable.filter),
                    contentDescription = "",
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }

            TagSearchButton(onClick = onTagSearchClick)
        }
    }
}
