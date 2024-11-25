package com.example.mymusicapplication

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymusicapplication.screens.MainApplication
import com.example.mymusicapplication.screens.PermissionDialog
import com.example.mymusicapplication.screens.PermissionViewModel
import com.example.mymusicapplication.screens.ReadExternalStoragePermissionTextProvider
import com.example.mymusicapplication.screens.ReadMediaAudioPermissionTextProvider
import com.example.mymusicapplication.screens.ReadMediaImagesPermissionTextProvider
import com.example.mymusicapplication.screens.WriteExternalStoragePermissionTextProvider
import com.example.mymusicapplication.ui.theme.MyMusicApplicationTheme

class MainActivity : ComponentActivity() {

    private val permissionsToRequest: Array<String>
        get() {
            val perms = mutableListOf<String>()
            when {
                /*Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
//                    perms.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
                    perms.add(Manifest.permission.READ_MEDIA_IMAGES)
                    perms.add(Manifest.permission.READ_MEDIA_AUDIO)
                }*/
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
            return perms.toTypedArray()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMusicApplicationTheme {
                val viewModel = viewModel<PermissionViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue
                val isPermissionsGranted by viewModel.isPermissionGranted
                val albums by viewModel.albums.collectAsState()

                val permissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { perms ->
                        permissionsToRequest.forEach { permission ->
                            viewModel.onPermissionResult(
                                permission = permission,
                                isGranted = perms[permission] == true
                            )
                        }
                    }
                )

                if (isPermissionsGranted) {
                    MainApplication(albums = albums, context = this@MainActivity)
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            // TODO: implement optional access to images
                            permissionResultLauncher.launch(
                                permissionsToRequest
                            )
                        }) {
                            Text(text = "Request required permissions")
                        }
                    }
                }

                dialogQueue
                    .reversed()
                    .forEach { permission -> 
                        PermissionDialog(
                            permissionTextProvider =
                                when (permission) {
                                    /*Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED -> {
                                        ReadMediaVisualUserSelectedPermissionTextProvider()
                                    }*/
                                    Manifest.permission.READ_MEDIA_AUDIO -> {
                                        ReadMediaAudioPermissionTextProvider()
                                    }
                                    Manifest.permission.READ_MEDIA_IMAGES -> {
                                        ReadMediaImagesPermissionTextProvider()
                                    }
                                    Manifest.permission.READ_EXTERNAL_STORAGE -> {
                                        ReadExternalStoragePermissionTextProvider()
                                    }
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                                        WriteExternalStoragePermissionTextProvider()
                                    }
                                    else -> return@forEach
                                },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                permissionResultLauncher.launch(
                                    arrayOf(permission)
                                )
                            },
                            onGoToAppSettings = ::openAppSettings
                        )
                    }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

@Preview(
    showBackground = true,
    name = "My Music App",
    showSystemUi = true,
)
@Composable
fun GreetingPreview() {
    MyMusicApplicationTheme {
    }
}