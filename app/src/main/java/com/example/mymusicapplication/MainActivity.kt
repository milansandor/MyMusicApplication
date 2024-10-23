package com.example.mymusicapplication

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymusicapplication.controllers.albumcontroller.AlbumListContainer
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.setOnSongEndListener
import com.example.mymusicapplication.controllers.songplayercontroller.AlbumSongList
import com.example.mymusicapplication.controllers.songplayercontroller.SongManagerComposable
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.screens.PermissionDialog
import com.example.mymusicapplication.screens.PermissionViewModel
import com.example.mymusicapplication.screens.ReadExternalStoragePermissionTextProvider
import com.example.mymusicapplication.screens.ReadMediaAudioPermissionTextProvider
import com.example.mymusicapplication.screens.ReadMediaImagesPermissionTextProvider
import com.example.mymusicapplication.screens.ReadMediaVisualUserSelectedPermissionTextProvider
import com.example.mymusicapplication.screens.TagSearchModal
import com.example.mymusicapplication.screens.WriteExternalStoragePermissionTextProvider
import com.example.mymusicapplication.ui.theme.MyMusicApplicationTheme

class MainActivity : ComponentActivity() {

    private val permissionsToRequest: Array<String>
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

@Composable
fun MainApplication(albums: List<Album>, context: Context) {
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var currentlyPlayingAlbum by remember { mutableStateOf<Album?>(null) }
    var selectedSong by remember { mutableStateOf<Song?>(null) }
    var isModalOpen by remember { mutableStateOf(false) }
    var isSongPlaying by remember { mutableStateOf(false) }

    // tags and checked tags
    val tags = remember {
        mutableStateListOf(*albums.map { it.genre }.distinct().toTypedArray())
    }
    val checkedTags = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(tags) {
        tags.forEach { tag ->
            if (checkedTags[tag] == null) {
                checkedTags[tag] = false
            }
        }
    }

    val onAddTag: (String) -> Unit = { newTag ->
        if (newTag.isNotBlank() && !tags.contains(newTag)) {
            tags.add(newTag)
            checkedTags[newTag] = false
        }
    }

    val onRemoveTag: (String) -> Unit = { tag ->
        tags.remove(tag)
        checkedTags.remove(tag)
    }

    val onSongEnd: () -> Unit = {
        val songList = currentlyPlayingAlbum?.songs?.sortedBy { it.track.toInt() } ?: emptyList()
        val currentIndex = songList.indexOfFirst { it.title == selectedSong?.title }
        val nextIndex = currentIndex + 1
        if (nextIndex < songList.size) {
            val nextSong = songList[nextIndex]
            selectedSong = nextSong
            playSong(nextSong)
        } else {
            // No more songs in the album
            stopCurrentSong()
            selectedSong = null
        }
    }

    setOnSongEndListener(onSongEnd)

    val activeTags = checkedTags.filterValues { it }.keys

    val filteredAlbums = if (activeTags.isEmpty()) {
        albums
    } else {
        albums.filter { album ->
            activeTags.all { tag -> album.genre.contains(tag) }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(100.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black,
                contentPadding = PaddingValues(0.dp),
            ) {
                SongManagerComposable(
                    currentSong = selectedSong,
                    currentlyPlayingAlbum = currentlyPlayingAlbum,
                    onSongChange = { newSong ->
                        stopCurrentSong()
                        selectedSong = newSong
                        playSong(selectedSong!!)
                        Log.i("NEW_SONG", "$newSong")
                    },
                    onTagSearchClick = { isModalOpen = !isModalOpen },
                    isSongCurrentlyPlaying = isSongPlaying,
                    onIsSongCurrentlyPlayingChange = { newValue ->
                        isSongPlaying = newValue
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Color.White),
        ) {
            if (selectedAlbum == null) {
                TagSearchModal(
                    albums = albums,
                    tags = tags,
                    checkedTags = checkedTags,
                    onCheckedTagChange = { tag, isChecked ->
                        checkedTags[tag] = isChecked
                        Log.i("checked:", checkedTags.toString())
                    },
                    isModalOpen = isModalOpen,
                    onDismiss = { isModalOpen = false },
                    onAddTag = onAddTag,
                    onRemoveTag = onRemoveTag
                )
            }

            Row {
                if (selectedAlbum == null) {
                    AlbumListContainer(albums = filteredAlbums, selectedSong = selectedSong) { album ->
                        selectedAlbum = album
                    }
                } else {
                    AlbumSongList(
                        context = context,
                        album = selectedAlbum!!,
                        tags = tags,
                        onBackPress = {
                            selectedAlbum = null
                        },
                        onSongClicked = { song ->
                            selectedSong = song
                            currentlyPlayingAlbum = selectedAlbum
                        },
                        selectedSong = selectedSong,
                        isSongCurrentlyPlaying = isSongPlaying,
                        onIsSongCurrentlyPlayingChange = { newValue ->
                            isSongPlaying = newValue
                        }
                    )
                }
            }
        }
    }
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