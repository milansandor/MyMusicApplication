package com.example.mymusicapplication.controllers

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymusicapplication.models.Song

class MusicPlayerViewModel: ViewModel() {

    var mediaPlayer: MediaPlayer? = null

    private val _currentlyPlayingSongTitle = MutableLiveData("")
    val currentlyPlayingSongTitle: LiveData<String> = _currentlyPlayingSongTitle

    fun playSong(song: Song) {
        // todo: stopCurrentSong()
        _currentlyPlayingSongTitle.value = song.title
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
}