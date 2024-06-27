package com.example.mymusicapplication.controllers.albumcontroller

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.models.Album

@Composable
fun AlbumListContainer(albums: List<Album>, onAlbumClick: (Album) -> Unit) {
    val columnCount = 4

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnCount),
    ) {
        items(albums) {album ->
            AlbumCard(
                album = album,
                onClick = {
                    onAlbumClick(album)
                },
                modifier = Modifier
                    .height(IntrinsicSize.Min)
            )
        }
    }
}