package com.example.mymusicapplication.models

data class Album(
    val albumName: String,
    val artistName: String,
    val songs: List<Song>,
    val albumArtUri: String?,
    var genre: String,
    var lastVisited: Long = 0,
)
