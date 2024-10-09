package com.example.mymusicapplication.models

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.FileNotFoundException

class MusicRepository(private val context: Context) {

    fun getAllMusic(): List<Album> {
        val albums = mutableListOf<Album>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.ALBUM_ARTIST} ASC"

        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        cursor?.use { c ->
            val albumMap = mutableMapOf<String, Pair<String, MutableList<Song>>>()
            val genreMap = mutableMapOf<Long, String>()

            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val trackNumber = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                val finalTrackNumber = if (trackNumber.length > 3) {
                    trackNumber.takeLast(2)
                } else {
                    trackNumber
                }
                val title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val data = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val genre = getGenreForSong(id)

                val song = Song(id, finalTrackNumber, title, artist, album, duration, data)

                if (albumMap.containsKey(album)) {
                    albumMap[album]?.second?.add(song)
                } else {
                    albumMap[album] = Pair(artist, mutableListOf(song))
                    genreMap[id] = genre
                }
            }

            for ((albumName, pair) in albumMap) {
                val (artist, songs) = pair
                val albumArtUri = getAlbumArtUri(albumName)
                val genre = genreMap[songs.first().id] ?: "Unknown"
                val album = Album(albumName, artist, songs, albumArtUri.toString(), genre, 0)
                albums.add(album)
            }
        }

        return albums
    }

    @SuppressLint("Range")
    private fun getAlbumArtUri(albumName: String): Uri? {
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Albums._ID)
        val selection = "${MediaStore.Audio.Albums.ALBUM} = ?"
        val selectionArgs = arrayOf(albumName)
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val albumId = it.getLong(it.getColumnIndex(MediaStore.Audio.Albums._ID))
                val albumArtUri = Uri.parse("content://media/external/audio/albumart")
                val fullAlbumArtUri = ContentUris.withAppendedId(albumArtUri, albumId)
                Log.i("ALBUM ART URI", fullAlbumArtUri.toString())

                return try {
                    context.contentResolver.openInputStream(fullAlbumArtUri)?.use {
                        // Successfully opened stream, album art exists
                        fullAlbumArtUri
                    }
                } catch (e: FileNotFoundException) {
                    // Album art doesn't exist
                    null
                }
            }
        }
        return null
    }

    @SuppressLint("Range")
    private fun getGenreForSong(songId: Long): String {
        val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songId.toInt())
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        var genre: String = "Unknown"

        cursor?.use {
            if (it.moveToFirst()) {
                genre = it.getString(it.getColumnIndex(MediaStore.Audio.Genres.NAME))
            }
        }

        return genre
    }
}
