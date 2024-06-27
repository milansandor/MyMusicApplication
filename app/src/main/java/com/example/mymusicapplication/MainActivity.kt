package com.example.mymusicapplication

import android.Manifest
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mymusicapplication.controllers.MusicPlayerViewModel
import com.example.mymusicapplication.controllers.albumcontroller.AlbumListContainer
import com.example.mymusicapplication.controllers.songplayercontroller.AlbumSongList
import com.example.mymusicapplication.controllers.songplayercontroller.SongManagerComposable
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song
import com.example.mymusicapplication.screens.PermissionViewModel
import com.example.mymusicapplication.ui.theme.MyMusicApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMusicApplicationTheme {
                val viewModel = viewModel<PermissionViewModel>()

                LaunchedEffect(Unit) {
                    viewModel.checkPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                val isPermissionGranted by viewModel.isPermissionGranted
                val albums by viewModel.albums.collectAsState()

                val imagePermissionResultLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission = Manifest.permission.READ_EXTERNAL_STORAGE,
                            isGranted = isGranted
                        )
                    }
                )
                if (isPermissionGranted) {
                    MainApplication(albums)
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = {
                            imagePermissionResultLauncher.launch(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        }) {
                            Text(text = "Request one permission")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApplication(albums: List<Album>) {
    var selectedAlbum by remember {
        mutableStateOf<Album?>(null)
    }

    var selectedSong by remember {
        mutableStateOf<Song?>(null)
    }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                SongManagerComposable(
                    selectedSong,
                    selectedAlbum,
                    onSongChange = { newSong ->
                        selectedSong = newSong
                    }
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .background(color = Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (selectedAlbum == null) {
                AlbumListContainer(albums = albums) { album ->
                    selectedAlbum = album
                }
            } else {
                AlbumSongList(
                    album = selectedAlbum!!,
                    onBackPress = {
                        selectedAlbum = null
                    },) {
                    selectedSong = it
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