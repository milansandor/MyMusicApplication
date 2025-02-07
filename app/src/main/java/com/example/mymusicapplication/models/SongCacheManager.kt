package com.example.mymusicapplication.models

import android.content.Context
import android.util.Log

class SongCacheManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("song_cache", Context.MODE_PRIVATE)

    fun cacheSongGenre(songId: String, genre: String) {
        Log.d("SongCacheManager", "cacheSongGenre: songId: $songId, genre: $genre")
        val editor = sharedPreferences.edit()
        editor.putString(songId, genre)
        editor.apply()
    }

   fun getCachedSongGenre(songId: String): String? {
       val genre = sharedPreferences.getString(songId, null)
       Log.d("SongCacheManager", "getCachedSongGenre: $genre")
       return genre
   }

    fun updateCachedSongGenre(songId: String, newGenre: String) {
        Log.d("SongCacheManager", "updateCachedSongGenre: $newGenre")
        val editor = sharedPreferences.edit()
        editor.putString(songId, newGenre)
        editor.apply()
    }
}