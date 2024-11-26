package com.example.mymusicapplication.controllers

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.models.SongUpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

var mediaPlayer: MediaPlayer? = null

var currentlyPlayingSongId: Long? = null
var currentlyPlayingSongTitle: String? = null

var onSongEnd: (() -> Unit)? = null

fun setOnSongEndListener(listener: () -> Unit) {
    onSongEnd = listener
}

fun playSong(song: Song) {
    stopCurrentSong()
    currentlyPlayingSongTitle = song.title
    currentlyPlayingSongId = song.id

    mediaPlayer = MediaPlayer().apply {
        try {
            setDataSource(song.data)
            prepare()
            start()
            Log.i("PLAY_SONG", "music started to play: $currentlyPlayingSongTitle")

            // Set the completion listener
            setOnCompletionListener {
                Log.i("SONG_COMPLETED", "$currentlyPlayingSongTitle has finished playing.")
                // Call the onSongEnd function
                onSongEnd?.invoke() // Invoke the callback here
            }
        } catch (e: Exception) {
            Log.e("PLAY_SONG", "Error playing song: ${e.message}")
            release()
        }
    }
}

fun pauseSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            pause()
        }
    }
}

fun resumeCurrentSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            start()
        }
    }
}

fun isPlaying(): Boolean {
    return mediaPlayer?.isPlaying ?: false
}

fun stopCurrentSong() {
    if (currentlyPlayingSongId != null) {
        mediaPlayer?.apply {
            stop()
            release()
        }

        mediaPlayer = null
        Log.i("STOP_CURRENT_SONG", "currently played song is stopped $currentlyPlayingSongTitle")
        currentlyPlayingSongId = null
        currentlyPlayingSongTitle = null
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

suspend fun updateGenre(context: Context, songsToUpdate: List<SongUpdateInfo>) {
    withContext(Dispatchers.IO) {
        try {
            // Loop through each song in the list
            songsToUpdate.forEach { songInfo ->
                // Get the Uri of the song
                val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songInfo.songId)

                // Create a temporary file in cache directory
                val tempFile = File.createTempFile("temp", ".mp3", context.cacheDir)

                // Copy the contents of the original file into the temporary file
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: throw IOException("Unable to read MP3 file")

                // Read and modify the MP3 file's tag using JAudiotagger
                val audioFile: AudioFile = AudioFileIO.read(tempFile)
                val tag = audioFile.tagOrCreateAndSetDefault
                Log.i("PREVIOUS TAG", "!prev. tag: ${tag.getFirst(FieldKey.GENRE)}")

                // Set the genre to the updated genre
                tag.setField(FieldKey.GENRE, songInfo.updatedGenre)
                Log.i("Updated File TAG:", "!updatedTag. tag: ${tag.getFirst(FieldKey.GENRE)}")

                // Commit changes to the temporary file
                audioFile.commit()

                // Write the updated temporary file back to the original Uri
                context.contentResolver.openOutputStream(uri, "rwt")?.use { output ->
                    tempFile.inputStream().use { input ->
                        input.copyTo(output)
                        Log.i("UPDATE_GENRE", "genre updated successfully to ${tempFile.name}")
                    }
                } ?: throw IOException("Unable to write MP3 file")

                // Clean up temporary file
                tempFile.delete()

                // Trigger media scan and suspend until scan is complete
                suspendCoroutine { continuation ->
                    MediaScannerConnection.scanFile(context, arrayOf(songInfo.data), arrayOf("audio/mpeg")) { path, uri ->
                        Log.d("MediaScanner", "Scanned $path:")
                        continuation.resume(Unit)
                    }
                }

                delay(5)
            }

            // Show success toast once all songs are processed
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Genres updated successfully for ${songsToUpdate.size} songs", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error updating genre: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
