package com.example.mymusicapplication.screens.albumslist

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mymusicapplication.screens.albumslist.components.AlbumCard
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song

@Composable
fun AlbumListContainer(albums: List<Album>, selectedSong: Song?, onAlbumClick: (Album) -> Unit) {
    val columnCount = 4

    val sortedAlbums = albums.sortedByDescending { it.lastVisited }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
    ) {
        items(sortedAlbums) {album ->
            AlbumCard(
                album = album,
                onClick = {
                    album.lastVisited = System.currentTimeMillis()
                    onAlbumClick(album)
                },
                selectedSong = selectedSong,
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            )
        }
    }
}