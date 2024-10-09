package com.example.mymusicapplication.models

data class Album(
    val albumName: String,
    val artistName: String,
    val songs: List<Song>,
    val albumArtUri: String?,
    val genre: String,
    var lastVisited: Long,
)
