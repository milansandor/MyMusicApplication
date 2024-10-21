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

    /*fun onPermissionResult(
        isGranted: Boolean
    ) {
        if (!isGranted) {
            visiblePermissionDialogQueue.add(0, requiredPermissions[currentPermissionIndex])
        }
        // Move to the next permission
        currentPermissionIndex++

        // Check if we have requested all permissions
        if (currentPermissionIndex < requiredPermissions.size) {
            // Still more permissions to check
            checkNextPermission()
        } else {
            // All permissions checked, update the permission status
            isPermissionGranted.value = checkAllPermissionsGranted()
            if (isPermissionGranted.value) {
                loadMusic()
            }
        }
    }

     fun checkNextPermission() {
        if (currentPermissionIndex < requiredPermissions.size) {
            val permission = requiredPermissions[currentPermissionIndex]
            val isGranted = ContextCompat.checkSelfPermission(
                getApplication(),
                permission
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                // Trigger a request for the next permission
                visiblePermissionDialogQueue.add(0, permission)
            } else {
                // If granted, move to the next permission
                currentPermissionIndex++
                checkNextPermission()
            }
        } else {
            // All permissions have been checked
            isPermissionGranted.value = true
        }
    }

    private fun checkAllPermissionsGranted(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                getApplication(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }*/
}