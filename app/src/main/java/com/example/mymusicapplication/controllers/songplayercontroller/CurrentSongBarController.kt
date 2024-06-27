package com.example.mymusicapplication.controllers.songplayercontroller

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.isPlaying
import com.example.mymusicapplication.controllers.pauseSong
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.resumeCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import kotlin.system.exitProcess


@Composable
fun SongManagerComposable(currentSong: Song?, album: Album?, onSongChange: (Song) -> Unit) {
    val painter = if (album?.albumArtUri != null) {
        rememberAsyncImagePainter(model = album?.albumArtUri)
    } else {
        painterResource(id = R.drawable.test_cover)
    }

    val sortedSongs = album?.songs?.sortedBy { it.track.toInt() }
    var songList = listOf<Song>()
    if (album != null) {
        if (sortedSongs != null) {
            songList = sortedSongs
        }
    }

    fun getItemCount(): Int {
        return songList.size
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

    fun getPreviousSong(song: Song?): Song {
        var cSongIndex = findSongIndex(songList, song)
        if (cSongIndex == 0) {
            exitProcess(404)
        }

        var newPosition = cSongIndex?.minus(1)
        return getItem(newPosition)
    }

    fun getNextSong(song: Song?): Song {
        var cSongIndex = findSongIndex(songList, song)
        if (cSongIndex == songList.size - 1) {
            exitProcess(404)
        }

        var newPosition = cSongIndex?.plus(1)
        return getItem(newPosition)
    }


    Log.i("CURRENT_SONG_POS", "${currentSong?.title} position is ${findSongIndex(songList, currentSong)}")

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(8.dp, 8.dp, 8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painter,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = currentSong?.title ?: "",
            modifier = Modifier.width(150.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {
                    var prevSong = getPreviousSong(currentSong)
                    playSong(prevSong)
                    onSongChange(prevSong)
                }
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowLeft,
                    "",
                    modifier = Modifier.size(30.dp)
                )
            }
            var isSongPlaying = remember { mutableStateOf(false) }
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
                        Icons.Filled.PlayArrow,
                        "",
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        Icons.Filled.Close,
                        "",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    var nextSong = getNextSong(currentSong)
                    playSong(nextSong)
                    onSongChange(nextSong)
                },
            ) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    "",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

    }
}
