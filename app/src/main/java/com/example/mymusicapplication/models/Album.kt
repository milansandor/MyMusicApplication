package com.example.mymusicapplication.models

import androidx.compose.runtime.MutableState

data class Album(
    val albumName: String,
    val artistName: String,
    var songs: List<Song>,
    val albumArtUri: String?,
    var genre: MutableState<String>,
    var lastVisited: Long,
    var isSelectedAlbum: Boolean = false
)
