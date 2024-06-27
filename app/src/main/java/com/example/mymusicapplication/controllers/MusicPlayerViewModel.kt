package com.example.mymusicapplication.controllers

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mymusicapplication.models.Song

class MusicPlayerViewModel: ViewModel() {

//    var mediaPlayer: MediaPlayer? = null
//
//    private var _currentSong = MutableLiveData<Song>()
//    var currentSong: LiveData<Song> = _currentSong
//
////    private var _currentlyPlayingSongTitle = MutableLiveData<String>()
////    var currentlyPlayingSongTitle: LiveData<String> = _currentlyPlayingSongTitle
////
////    private var _currentlyPlayingSongId = MutableLiveData<Long>()
////    var currentlyPlayingSongId: LiveData<Long> = _currentlyPlayingSongId
//
//    fun onPlaySong(song: Song) {
//        // todo: stopCurrentSong()
////        _currentlyPlayingSongTitle.value = song.title
////        _currentlyPlayingSongId.value = song.id
//        _currentSong.value = song
//
//        mediaPlayer = MediaPlayer().apply {
//            try {
//                setDataSource(_currentSong.value!!.data)
//                prepare()
//                start()
//                Log.i("PLAY_SONG", "music started to play: ${_currentSong.value!!.title}")
//            } catch (e: Exception) {
//                Log.e("PLAY_SONG", "Error playing song: ${e.message}")
//                release()
//            }
//        }
//    }
}