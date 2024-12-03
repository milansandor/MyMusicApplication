package com.example.mymusicapplication.ui.screens.albumslist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mymusicapplication.R
import com.example.mymusicapplication.models.Album
import com.example.mymusicapplication.models.Song

@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier,
    selectedSong: Song?,
) {
    val painter = if (album.albumArtUri != "null") {
        rememberAsyncImagePainter(model = album.albumArtUri)
    } else {
        painterResource(id = R.drawable.album_cover_palceholer_v2)
    }
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val itemHeight = screenWidth / 4

    Card(
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(bottom = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(color = Color.White)
                .height(140.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = if (album.songs.contains(selectedSong)) {
                    Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                        .border(4.dp, Color.Green, shape = RoundedCornerShape(2.dp))
                } else {
                    Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(2.dp))
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                Text(
                    text = album.albumName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                )
                Text(
                    text = album.artistName,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.Black,
                )
            }
        }
    }
}
