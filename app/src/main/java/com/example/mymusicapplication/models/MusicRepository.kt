package com.example.mymusicapplication.models

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.FileNotFoundException

class MusicRepository(private val context: Context) {

    fun getAllMusic(): List<Album> {
        val albums = mutableListOf<Album>()
        val contentResolver: ContentResolver = context.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("audio/mpeg")
        val sortOrder = "${MediaStore.Audio.Media.ALBUM_ARTIST} ASC"

        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use { c ->
            val albumMap = mutableMapOf<String, Pair<String, MutableList<Song>>>()
            val genreMap = mutableMapOf<String, MutableSet<String>>()
            val retriever = MediaMetadataRetriever()

            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val trackNumber = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                val convertedTrackNumber = trackNumber?.toIntOrNull() ?: 0
                val finalTrackNumber = if (convertedTrackNumber > 999) {
                    convertedTrackNumber - 1000
                } else {
                    convertedTrackNumber
                }
                val title = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val data = c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                retriever.setDataSource(data)
                val genre = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE) ?: ""

                val song = Song(id, finalTrackNumber, title, artist, album, duration, data, genre = genre)

                if (albumMap.containsKey(album)) {
                    albumMap[album]?.second?.add(song)
                    genreMap[album]?.addAll(genre.split(";").map { it.trim() })
                } else {
                    albumMap[album] = Pair(artist, mutableListOf(song))
                    genreMap[album] = mutableSetOf(*genre.split(";").map { it.trim() }.toTypedArray())
                }
            }
            retriever.release()

            for ((albumName, pair) in albumMap) {
                val (artist, songs) = pair
                val albumArtUri = getAlbumArtUri(albumName)
                val albumArtPath = if (albumArtUri != null && fileExists(albumArtUri)) {
                    albumArtUri.toString()
                } else {
                    null
                }
                val genre = genreMap[albumName]?.joinToString(";") ?: "Unknown"
                val album = Album(albumName, artist, songs, albumArtPath.toString(), genre = genre)
                albums.add(album)
            }
        }

        return albums
    }

    private fun fileExists(uri: Uri): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun readGenreFromFile(filePath: String): String {
        return try {
            val file = File(filePath)
            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault
            val genre = tag.getFirst(FieldKey.GENRE)
            genre ?: "Unknown"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
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

                return try {
                    context.contentResolver.openInputStream(fullAlbumArtUri)?.use {
                        // Successfully opened stream, album art exists
                        return fullAlbumArtUri
                    }
                } catch (e: FileNotFoundException) {
                    // Album art doesn't exist
                    null
                }
            }
        }
        return null
    }
}
