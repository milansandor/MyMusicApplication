package com.example.mymusicapplication.screens

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PermissionViewModel(application: Application): AndroidViewModel(application) {
    private val musicRepository = MusicRepository(application)

    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    val isPermissionGranted = mutableStateOf(false)

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted) {
            visiblePermissionDialogQueue.add(0, permission)
        }
        isPermissionGranted.value = isGranted
        if (isGranted) {
            loadMusic()
        }
    }

    fun checkPermission(context: Context, permission: String) {
        val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        isPermissionGranted.value == isGranted
    }

    private fun loadMusic() {
        viewModelScope.launch {
            val albumList = musicRepository.getAllMusic()
            _albums.value = albumList
        }
    }
}