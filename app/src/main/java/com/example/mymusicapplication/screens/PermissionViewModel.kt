package com.example.mymusicapplication.screens

import android.Manifest
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

    private val requiredPermissions = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val visiblePermissionDialogQueue = mutableStateListOf<String>()
    val isPermissionGranted  = mutableStateOf(false)

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums

    init {
        checkAllPermissionsGranted()
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
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

    // Checks if all required permissions are granted.
    private fun checkAllPermissionsGranted() {
        val context = getApplication<Application>().applicationContext
        isPermissionGranted.value = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        // Load music if all permissions are granted.
        if (isPermissionGranted.value) {
            loadMusic()
        }
    }

    private fun loadMusic() {
        viewModelScope.launch {
            val albumList = musicRepository.getAllMusic()
            _albums.value = albumList
        }
    }
}