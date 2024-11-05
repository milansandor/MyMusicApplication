package com.example.mymusicapplication.models

data class Song(
    val id: Long,
    val track: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val data: String,
    val genre: String,
)
