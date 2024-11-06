package com.example.mymusicapplication.controllers.songplayercontroller.songTagController

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SongTagsContainer(
    songTags: List<String>,
    onTagRemoved: (String) -> Unit,
) {
    Text(
        text = "Songs' tags:",
        fontWeight = FontWeight.Bold,
    )

    LazyColumn(
        modifier = Modifier.heightIn(max = 150.dp),
    ) {
        items(songTags) {tag ->
            SongTag(
                tag = tag,
                onTagRemoved = onTagRemoved
            )
        }
    }
}

@Composable
fun SongTag(
    tag: String,
    onTagRemoved: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = tag,
                fontSize = 12.sp,
            )
        }
        IconButton(
            onClick = {
                onTagRemoved(tag)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Composable
fun AvailableTags(
    tags: List<String>,
    selectedTags: MutableList<String>,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Add existing tags:",
            fontWeight = FontWeight.Bold,
        )
    }

    LazyColumn {
        items(tags) {tag ->
            val isChecked = selectedTags.contains(tag)
            SelectableTag(
                tag = tag,
                checked = isChecked,
                onCheckedChange = { checked ->
                    if (checked) {
                        selectedTags.add(tag)
                    } else {
                        selectedTags.remove(tag)
                    }
                }
            )
        }
    }
}

@Composable
fun SelectableTag(
    tag: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = tag,
            fontSize = 12.sp,
        )
    }
}

/*Text(
    text = "Apply tag(s) +",
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.Bold,
    modifier = Modifier.clickable {

    }
)*/
