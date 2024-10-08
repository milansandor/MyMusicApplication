package com.example.mymusicapplication.controllers.albumcontroller

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

@Composable
fun AlbumCard(
    album: Album,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val painter = if (album.albumArtUri != null) {
        rememberAsyncImagePainter(model = album.albumArtUri)
    } else {
        painterResource(id = R.drawable.test_cover)
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
                .height(120.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(itemHeight)
                    .fillMaxWidth()
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
                )
                Text(
                    text = album.artistName,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
