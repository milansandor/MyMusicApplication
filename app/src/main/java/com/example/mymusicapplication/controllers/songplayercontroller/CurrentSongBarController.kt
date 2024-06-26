package com.example.mymusicapplication.controllers.songplayercontroller

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mymusicapplication.R
import com.example.mymusicapplication.controllers.currentlyPlayingSongTitle

@Composable
fun SongManagerComposable() {
    val img = painterResource(id = R.drawable.test_cover)

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(8.dp, 8.dp, 8.dp, 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = img,
            contentDescription = null
        )
        
        Text(text = currentlyPlayingSongTitle.toString())

        IconButton(
            onClick = { }
        ) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                "",
                modifier = Modifier.size(35.dp)
            )
        }

        IconButton(
            onClick = { }
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                "",
                modifier = Modifier.size(35.dp)
            )
        }

        IconButton(
            onClick = { /*TODO*/ },
        ) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                "",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}
