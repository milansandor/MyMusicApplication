package com.example.mymusicapplication.models

data class SongUpdateInfo(
    val songId: Long,
    val data: String,
    val updatedGenre: String,
)
