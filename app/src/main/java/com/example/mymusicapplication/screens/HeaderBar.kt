package com.example.mymusicapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymusicapplication.models.Album


@Composable
fun TagSearchModal(
    albums: List<Album>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    isModalOpen: Boolean,
    onDismiss: () -> Unit,
) {
    val tags = albums.map { it.genre }.distinct()

    if (isModalOpen) {
        TagListModal(
            onDismiss = onDismiss,
            tags = tags,
            checkedTags = checkedTags,
            onCheckedTagChange = onCheckedTagChange,
        )
    }
}

@Composable()
fun TagSearchButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search icon",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TagListModal(
    onDismiss: () -> Unit,
    tags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                ) {
                    Text("X")
                }
            }
            TagList(
                tags = tags,
                checkedTags = checkedTags,
                onCheckedTagChange = onCheckedTagChange
            )
        }
    }
}

@Composable
fun SearchAlbumButton(modifier: Modifier = Modifier) {
    TextButton(
        onClick = {
            /*TODO: filter the albums based on the checked tags*/

        },
        modifier = modifier
        ) {
        Row() {
            Text(text = "Search in tags")
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search icon"
            )
        }
    }
}

@Composable
fun TagList(
    tags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp, 16.dp, 5.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                items(tags) {tag ->
                    TagItem(
                        tag = tag,
                        isChecked = checkedTags[tag] ?: false,
                        onCheckedChange = { isChecked ->
                            onCheckedTagChange(tag, isChecked)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            SearchAlbumButton()
        }
    }
}

@Composable
fun TagItem(
    tag: String, 
    isChecked: Boolean, 
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = tag,
            fontSize = 12.sp,
        )
    }
}

val mockTags = listOf("rock", "pop", "jazz")
val mockCheckTags = mockTags.map { it to true }.toMap()
@Preview(showBackground = true)
@Composable
fun TagListPreview() {
//    TagListModal(onDismiss = {false}, mockTags, mockCheckTags)
}
