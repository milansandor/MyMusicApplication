package com.example.mymusicapplication.viewmodels

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PermissionViewModel(application: Application): AndroidViewModel(application) {
    private val musicRepository = MusicRepository(application)

    private val requiredPermissions: List<String>
        get() {
            val perms = mutableListOf<String>()
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//                    perms.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    perms.add(Manifest.permission.READ_MEDIA_IMAGES)
                    perms.add(Manifest.permission.READ_MEDIA_AUDIO)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    perms.add(Manifest.permission.READ_MEDIA_IMAGES)
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

    private fun loadMedia() {
        viewModelScope.launch {
            val albumList = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                    // Access media using URIs from the Photo Picker
                    musicRepository.getAllMusic()
                }
                else -> {
                    musicRepository.getAllMusic()
                }
            }
            _albums.value = albumList
        }
    }
}