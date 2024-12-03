package com.example.mymusicapplication.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.ui.screens.albumslist.AlbumListContainer
import com.example.mymusicapplication.controllers.playSong
import com.example.mymusicapplication.controllers.setOnSongEndListener
import com.example.mymusicapplication.ui.screens.albumsonglist.AlbumSongList
import com.example.mymusicapplication.ui.screens.songmanager.SongManagerComposable
import com.example.mymusicapplication.controllers.stopCurrentSong
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.ui.screens.tagsearchmodal.TagSearchModal
import com.example.mymusicapplication.viewmodels.MusicViewModel

@Composable
fun MainApplication(
    musicViewModel: MusicViewModel,
    context: Context
) {
    val albums by musicViewModel.albums.collectAsState()

    val scope = rememberCoroutineScope()

    // Observe the tags and checkedTags state from the ViewModel
    val tags = musicViewModel.tags
    val checkedTags = musicViewModel.checkedTags

    val onAddGenreTag: (String) -> Unit = { newTag ->
        musicViewModel.onAddGenreTag(newTag)
    }

    val onRemoveTag: (String) -> Unit = { tag ->
        musicViewModel.onRemoveTag(tag, context, scope)
    }

    val onSongEnd: () -> Unit = {
        musicViewModel.onSongEnd(context, scope)
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

    // Filter albums that match all checked tags
    val matchedAlbums = albums.filter { album ->
        activeTags.all { tag -> album.genre.split(";").contains(tag) }
    }

    // Get all genres from the matched albums
    val additionalGenres = matchedAlbums
        .flatMap { it.genre.split(";") } // Split genres into a flat list
        .filter { tag -> tag.isNotBlank() } // Exclude empty or blank tags
        .distinct() // Ensure unique genres
        .filter { tag -> !checkedTags.getOrDefault(tag, false) }// Exclude already checked tags

    // Combine checked tags and additional genres
    val listedTags = (activeTags + additionalGenres).distinct()

    Scaffold(
        modifier = Modifier.background(Color.White),
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .height(100.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.Black,
                contentPadding = PaddingValues(0.dp),
            ) {
                SongManagerComposable(
                    currentSong = musicViewModel.selectedSong,
                    currentlyPlayingAlbum = musicViewModel.currentlyPlayingAlbum,
                    onSongChange = { newSong ->
                        stopCurrentSong()
                        musicViewModel.selectedSong = newSong
                        playSong(musicViewModel.selectedSong!!)
                        Log.i("NEW_SONG", "$newSong")
                    },
                    onTagSearchClick = { musicViewModel.isTagSearchModalOpen = !musicViewModel.isTagSearchModalOpen },
                    isSongCurrentlyPlaying = musicViewModel.isSongPlaying,
                    onIsSongCurrentlyPlayingChange = { newValue ->
                        musicViewModel.isSongPlaying = newValue
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
            if (musicViewModel.selectedAlbum == null) {
                TagSearchModal(
                    tags = tags,
                    remainingTags = listedTags,
                    checkedTags = checkedTags,
                    onCheckedTagChange = { tag, isChecked ->
                        checkedTags[tag] = isChecked
                        Log.i("checked:", checkedTags.toString())
                    },
                    isModalOpen = musicViewModel.isTagSearchModalOpen,
                    onDismiss = { musicViewModel.isTagSearchModalOpen = false },
                    onAddTag = onAddGenreTag,
                    onRemoveTag = onRemoveTag
                )
            }

            Row {
                if (musicViewModel.selectedAlbum == null) {
                    AlbumListContainer(albums = filteredAlbums, selectedSong = musicViewModel.selectedSong) { album ->
                        musicViewModel.selectedAlbum = album
                    }
                } else {
                    AlbumSongList(
                        context = context,
                        album = musicViewModel.selectedAlbum!!,
                        tags = tags,
                        onBackPress = {
                            musicViewModel.selectedAlbum = null
                        },
                        onSongClicked = { song ->
                            musicViewModel.selectedSong = song
                            musicViewModel.currentlyPlayingAlbum = musicViewModel.selectedAlbum
                        },
                        selectedSong = musicViewModel.selectedSong,
                        isSongCurrentlyPlaying = musicViewModel.isSongPlaying,
                        onIsSongCurrentlyPlayingChange = { newValue ->
                            musicViewModel.isSongPlaying = newValue
                        },
                        onTagAdded = onAddGenreTag,
                        musicViewModel = musicViewModel,
                    )
                }
            }
        }
    }
}
fun getRemainingTags(
    filteredAlbums: List<Album>,
    checkedTags: SnapshotStateMap<String, Boolean>
) = filteredAlbums.map { it.genre }
    .distinct()
    .filter { tag -> !checkedTags.getOrDefault(tag, false) }
