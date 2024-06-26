package com.example.mymusicapplication.controllers

import android.media.MediaPlayer
import android.util.Log
import com.example.mymusicapplication.models.Song

var mediaPlayer: MediaPlayer? = null

var currentlyPlayingSongId: Long? = null
var currentlyPlayingSongTitle: String? = null
var currentSongTitle: String? = null

fun playSong(song: Song) {
    stopCurrentSong()
    currentlyPlayingSongTitle = song.title
    currentlyPlayingSongId = song.id

    mediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(song.data)
            prepare()
            start()
            Log.i("PLAY_SONG", "music started to play: $currentlyPlayingSongTitle")
        } catch (e: Exception) {
            Log.e("PLAY_SONG", "Error playing song: ${e.message}")
            release()
        }
    }
}

fun stopCurrentSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            stop()
            release()
        }

        mediaPlayer = null
        Log.i("STOP_CURRENT_SONG", "currently played song is stopped $currentlyPlayingSongTitle")
        currentlyPlayingSongId = null
        currentlyPlayingSongTitle = null
    }
}

fun setCurrentSongTitle(songTitle: String): String {
    currentSongTitle = songTitle;
    return currentSongTitle as String;
}
