package com.example.mymusicapplication.viewmodels

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymusicapplication.models.SongCacheManager
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.MusicRepository
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.models.SongUpdateInfo
import com.example.mymusicapplication.controllers.updateGenre
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicViewModel(application: Application): AndroidViewModel(application) {
    private val musicRepository = MusicRepository(application)
    private val songCacheManager = SongCacheManager(application)
    private var pendingOperation: (() -> Unit)? = null

    private val requiredPermissions: List<String>
        get() {
            val perms = mutableListOf<String>()
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    perms.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    perms.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    perms.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return perms
        }

    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    val isPermissionGranted  = mutableStateOf(false)

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    var selectedAlbum by mutableStateOf<Album?>(null)
    var isTagSearchModalOpen by mutableStateOf(false)
    var isSongPlaying by mutableStateOf(false)
    var selectedSong by mutableStateOf<Song?>(null)
    var currentlyPlayingAlbum by mutableStateOf<Album?>(null)

    // tags and checked tags state
    val tags = mutableStateListOf<String>()
    val checkedTags = mutableStateMapOf<String, Boolean>()

    init {
        checkAllPermissionsGranted()
    }

    // Checks if all required permissions are granted.
    private fun checkAllPermissionsGranted() {
        val context = getApplication<Application>().applicationContext
        isPermissionGranted.value = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (isPermissionGranted.value) {
            loadMusic()
        }
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(0)
        checkAllPermissionsGranted()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }

        checkAllPermissionsGranted()
    }

    private fun loadMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            val albumList = musicRepository.getAllMusic()
            _albums.value = albumList

            updateTagsFromAlbums()
        }
    }

    private fun updateTagsFromAlbums() {
        val individualGenreTags = _albums.value
            .flatMap{ album -> album.genre.split(";") }
            .filter { tag -> tag.isNotBlank() }
            .distinct()

        tags.addAll(individualGenreTags)
        tags.forEach { tag ->
            if (checkedTags[tag] == null) {
                checkedTags[tag] = false
            }
        }
    }

    fun retryPendingOperation() {
        pendingOperation?.invoke()
        pendingOperation = null
    }

    // modification operations
    fun setPendingOperation(operation: () -> Unit) {
        pendingOperation = operation
    }

    // tag management
    fun onAddGenreTag(newTag: String) {
        if (newTag.isNotBlank() && !tags.contains(newTag)) {
            tags.add(newTag)
            checkedTags[newTag] = false
        }
    }

    fun onModifyTagName(oldTag: String, newTag: String, context: Context, scope: CoroutineScope) {
        val songsToUpdate = mutableListOf<SongUpdateInfo>()

        // Loop through each album and each song to check which songs have the old tag
        albums.value.forEach { album ->
            album.songs.forEach { song ->
                val songGenres = song.genre.split(";").map { it.trim() }
                if (songGenres.contains(oldTag)) {
                    val updatedGenre = songGenres
                        .map { if (it == oldTag) newTag else it }  // Replace oldTag with newTag
                        .joinToString(";")

                    // Add this song to the update list
                    songsToUpdate.add(SongUpdateInfo(song.id, song.data, updatedGenre))
                    // Update the genre in cache
                    songCacheManager.updateCachedSongGenre(song.id.toString(), updatedGenre)
                }
            }
        }

        val oldTagIndex = tags.indexOf(oldTag)
        if (oldTagIndex != -1) {
            tags[oldTagIndex] = newTag
        }

        val oldCheckedValue = checkedTags[oldTag]
        if (oldCheckedValue != null) {
            checkedTags.remove(oldTag)
            checkedTags[newTag] = oldCheckedValue
        }

        if (songsToUpdate.isNotEmpty()) {
            scope.launch {
                updateGenre(context = context, songsToUpdate = songsToUpdate)
            }
        }

        // Update the genre in each album
        albums.value.forEach { album ->
            val updatedGenres = album.genre
                .split(";")
                .map { it.trim() }
                .map { if (it == oldTag) newTag else it }  // Replace oldTag with newTag
                .joinToString(";")
            album.genre = updatedGenres
        }

        // Update the genre in each song of each album
        albums.value.forEach { album ->
            album.songs.forEach { song ->
                val updatedSongGenres = song.genre
                    .split(";")
                    .map { it.trim() }
                    .map { if (it == oldTag) newTag else it }
                    .joinToString(";")
                song.genre = updatedSongGenres
            }
        }

        // Optional: log the changes for debugging
        Log.i("TAG_MODIFICATION", "Modified tag: $oldTag to $newTag")

    }
    fun onRemoveTag(tag: String, context: Context, scope: CoroutineScope) {
        // Create a list to store the relevant song information
        val songsToUpdate = mutableListOf<SongUpdateInfo>()

        // Loop through each album and each song to check which songs have the tag
        albums.value.forEach { album ->
            album.songs.forEach { song ->
                val songGenres = song.genre.split(";").map { it.trim() }
                if (songGenres.contains(tag)) {
                    val updatedGenre = song.genre
                        .split(";")
                        .filter { it.trim() != tag } // Remove the tag from the genre
                        .joinToString(";")

                    songsToUpdate.add(SongUpdateInfo(song.id, song.data.toString(), updatedGenre))
                    // Update the genre in cache
                    songCacheManager.updateCachedSongGenre(song.id.toString(), updatedGenre)
                }
            }
        }

        // Now remove the tag from the tags and checkedTags lists
        tags.remove(tag)
        checkedTags.remove(tag)

        // Update the genre in the album
        albums.value.forEach { album ->
            val updatedGenres = album.genre
                .split(";")
                .filter { it.trim() != tag }
                .joinToString(";")
            album.genre = updatedGenres
        }

        // Update the genre in each song of the album
        albums.value.forEach { album ->
            album.songs.forEach { song ->
                val updatedSongGenres = song.genre
                    .split(";")
                    .filter { it.trim() != tag }
                    .joinToString(";")
                song.genre = updatedSongGenres
            }
        }

        // Call updateGenre for the list of songs to update
        if (songsToUpdate.isNotEmpty()) {
            scope.launch {
                updateGenre(context = context, songsToUpdate = songsToUpdate)
            }
        }

        // Optional: log the changes for debugging
        Log.i("TAG_REMOVAL", "Removed tag: $tag")
    }

    fun onSongEnd() {
        val songList = currentlyPlayingAlbum?.songs?.sortedBy { it.track } ?: emptyList()
        val currentIndex = songList.indexOfFirst { it.title == selectedSong?.title }
        val nextIndex = currentIndex + 1
        if (nextIndex < songList.size) {
            val nextSong = songList[nextIndex]
            selectedSong = nextSong
            playSong(nextSong)
        } else {
            stopCurrentSong()
            selectedSong = null
        }
    }
}