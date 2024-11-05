package com.example.mymusicapplication.controllers

import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaScannerConnection
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.mymusicapplication.models.Song
import com.mpatric.mp3agic.ID3v24Tag
import com.mpatric.mp3agic.Mp3File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
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

fun getGenreForSong(context: Context, songId: Long): String {
    val uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", songId.toInt())
    val projection = arrayOf(MediaStore.Audio.Genres.NAME)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)
            return cursor.getString(genreColumn)
        }
    }
    return ""
}

fun readGenreFromFile(filePath: String): String {
    return try {
        val file = File(filePath)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault
        val genre = tag.getFirst(FieldKey.GENRE)
        genre ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

suspend fun getSongFromMediaStore(context: Context, songId: Long): Song? {
    return withContext(Dispatchers.IO) {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media._ID} = ?"
        val selectionArgs = arrayOf(songId.toString())
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val track = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val genre = readGenreFromFile(data)

                Song(id, track, title, artist, album, duration, data, genre)
            } else {
                null
            }
        }
    }
}

// Works!!!
suspend fun updateGenre(context: Context, songId: Long, filePath: String, newTag: String) {
    withContext(Dispatchers.IO) {
        try {
            // Get the Uri of the song
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)

            // Create a temporary file in cache directory
            val tempFile = File.createTempFile("temp", ".mp3", context.cacheDir)

            // Copy the contents of the original file into the temporary file
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Unable to read MP3 file")

            // Disable Unicode normalization (if necessary)
//            TagOptionSingleton.getInstance().isAndroid = true

            // Read and modify the MP3 file's tag using JAudiotagger
            val audioFile: AudioFile = AudioFileIO.read(tempFile)
            val tag = audioFile.tagOrCreateAndSetDefault
            Log.i("PREVIOUS TAG", "!prev. tag: ${tag.getFirst(FieldKey.GENRE)}")

            // Set the genre
            tag.setField(FieldKey.GENRE, newTag)
            Log.i("Updated File TAG:", "!updatedTag. tag: ${tag.getFirst(FieldKey.GENRE)}")

            // Commit changes to the temporary file
            audioFile.commit()

            // Write the updated temporary file back to the original Uri
            context.contentResolver.openOutputStream(uri, "rwt")?.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                    Log.i("UPDATE_GENRE", "genre updated successfully to $output")
                }
            } ?: throw IOException("Unable to write MP3 file")

            // Clean up temporary file
            tempFile.delete()

            // Trigger media scan and suspend until scan is complete
            suspendCoroutine<Unit> { continuation ->
                MediaScannerConnection.scanFile(context, arrayOf(filePath), arrayOf("audio/mpeg")) { path, uri ->
                    Log.d("MediaScanner", "Scanned $path:")
                    continuation.resume(Unit)
                }
            }

            delay(500)

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Genre updated successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error updating genre: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

/*val testChangeTempFile = File.createTempFile("testChangeTemp", ".mp3", context.cacheDir)

            // Copy the contents of the original file into the temporary file
            context.contentResolver.openInputStream(uri)?.use { input ->
                testChangeTempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: throw IOException("Unable to read MP3 file")

            val testChangeAudioFile: AudioFile = AudioFileIO.read(testChangeTempFile)
            val testChangeTag = testChangeAudioFile.tagOrCreateAndSetDefault
            Log.i("TEST CHANGE TAG", "!prev. tag: ${testChangeTag.getFirst(FieldKey.GENRE)}")
            testChangeTempFile.delete()*/

// TODO: -------------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------

suspend fun updateGenreV2(context: Context, songId: Long, newTag: String) {
    withContext(Dispatchers.IO) {
        try {
            // Read the MP3 file from the Uri
            val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
            val inputStream = context.contentResolver.openInputStream(uri)
            Log.i("INPUT_STREAM TO CHANGE GENRE", "!input stream $inputStream from $uri")
            val tempFile = File.createTempFile("temp", ".mp3", context.cacheDir)
            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            // Read and modify the MP3 file's tag
            val audioFile: AudioFile = AudioFileIO.read(tempFile)
            val tag = audioFile.tagOrCreateAndSetDefault
            Log.i("PREVIOUS TAG", "!prev. tag: ${tag.getFirst(FieldKey.GENRE)}")

            // Set the genre
            tag.setField(FieldKey.GENRE, newTag)
            Log.i("UPDATE_GENRE", "!setting the genre: $newTag")

            // Commit changes to the file
            audioFile.commit()

//            ------- FOR LOGGING PURPOSES -------
            val newTag = audioFile.tag
            Log.i("NEW TAG:", "!new. tag: ${newTag.getFirst(FieldKey.GENRE)}")

            // TODO: set audioFile to a File type
            val updatedFile = audioFile.file

//            ------- FOR LOGGING PURPOSES -------
            val updatedAudioFile: AudioFile = AudioFileIO.read(updatedFile)
            val updatedTag = updatedAudioFile.tag
            Log.i("Updated File TAG:", "!updatedTag. tag: ${updatedTag.getFirst(FieldKey.GENRE)}")

            // Write the updated file back to the original Uri
            val outputStream = context.contentResolver.openOutputStream(uri, "rwt")
            Log.i("OUTPUT_STREAM TO CHANGE GENRE", "!The output stream: $outputStream, the $uri.")
            outputStream.use { output ->
                updatedFile.inputStream().use { input ->
                    input.copyTo(output!!)
                }
            }

            // Clean up temporary files
            tempFile.delete()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Genre updated successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Error updating genre: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
