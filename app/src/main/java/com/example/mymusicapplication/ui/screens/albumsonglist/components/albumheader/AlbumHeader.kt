package com.example.mymusicapplication.ui.screens.albumsonglist.components.albumheader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlbumHeader(
    albumCover: Painter,
    albumName: String,
    artistName: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Image(
            painter = albumCover,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(125.dp)
                .height(125.dp),
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
        ) {
            Text(
                text = albumName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(text = artistName)
        }
    }
}
