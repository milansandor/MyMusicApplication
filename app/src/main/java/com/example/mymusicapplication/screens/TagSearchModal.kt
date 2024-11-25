package com.example.mymusicapplication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TagSearchModal(
    tags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    isModalOpen: Boolean,
    onDismiss: () -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    if (isModalOpen) {
        TagListModal(
            onDismiss = onDismiss,
            tags = tags,
            checkedTags = checkedTags,
            onCheckedTagChange = onCheckedTagChange,
            onAddTag = onAddTag,
            onRemoveTag = onRemoveTag
        )
    }
}

@Composable
fun TagListModal(
    onDismiss: () -> Unit,
    tags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
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
                onCheckedTagChange = onCheckedTagChange,
                onAddTag = onAddTag,
                onRemoveTag = onRemoveTag
            )
        }
    }
}

@Composable
fun TagAddButton(
    onAddTag: (String) -> Unit,
    modifier: Modifier
) {
    var newTag by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        TextField(
            value = newTag, 
            onValueChange = { newTag = it },
            placeholder = { Text(text = "Add new tag") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                if (newTag.isNotBlank()) {
                    onAddTag(newTag.trim())
                    newTag = ""
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun TagList(
    tags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 5.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {
            items(tags) { tag ->
                TagItem(
                    tag = tag,
                    isChecked = checkedTags[tag] ?: false,
                    onCheckedChange = { isChecked ->
                        onCheckedTagChange(tag, isChecked)
                    },
                    onRemoveTag = {
                        onRemoveTag(tag)
                    }
                )
            }
        }
        TagAddButton(
            onAddTag = onAddTag,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )

    }
}

@Composable
fun TagItem(
    tag: String, 
    isChecked: Boolean, 
    onCheckedChange: (Boolean) -> Unit,
    onRemoveTag: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
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
        IconButton(
            onClick = { onRemoveTag() }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier.size(12.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagListPreview() {
}
