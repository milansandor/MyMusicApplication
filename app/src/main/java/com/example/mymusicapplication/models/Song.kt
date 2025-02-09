package com.example.mymusicapplication.models

data class Song(
    val id: Long,
    val track: Int,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    var genre: String,
)
