package com.example.mymusicapplication.controllers

import android.media.MediaPlayer
import android.util.Log
import com.example.mymusicapplication.models.Song

var mediaPlayer: MediaPlayer? = null

var currentlyPlayingSongId: Long? = null
var currentlyPlayingSongTitle: String? = null

var onSongEnd: (() -> Unit)? = null

fun setOnSongEndListener(listener: () -> Unit) {
    onSongEnd = listener
}

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

            // Set the completion listener
            setOnCompletionListener {
                Log.i("SONG_COMPLETED", "$currentlyPlayingSongTitle has finished playing.")
                // Call the onSongEnd function
                onSongEnd?.invoke() // Invoke the callback here
            }
        } catch (e: Exception) {
            Log.e("PLAY_SONG", "Error playing song: ${e.message}")
            release()
        }
    }
}

fun pauseSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            pause()
        }
    }
}

fun resumeCurrentSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            start()
        }
    }
}

fun isPlaying(): Boolean {
    return mediaPlayer?.isPlaying ?: false
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
