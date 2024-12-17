package com.example.mymusicapplication.ui.screens.tagsearchmodal

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mymusicapplication.R


@Composable
fun TagSearchModal(
    tags: List<String>,
    remainingTags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    isModalOpen: Boolean,
    onRemoveChecks: (List<String>) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onModifyTagName: (String, String) -> Unit,
    albumCount: Int,
) {
    if (isModalOpen) {
        TagListModal(
            onRemoveChecks = onRemoveChecks,
            tags = tags,
            remainingTags = remainingTags,
            checkedTags = checkedTags,
            onCheckedTagChange = onCheckedTagChange,
            onAddTag = onAddTag,
            onRemoveTag = onRemoveTag,
            onModifyTagName = onModifyTagName,
            albumCount = albumCount
        )
    }
}

@Composable
fun TagListModal(
    onRemoveChecks: (List<String>) -> Unit,
    tags: List<String>,
    remainingTags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onModifyTagName: (String, String) -> Unit,
    albumCount: Int,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$albumCount albums",
                    modifier = Modifier.padding(start = 16.dp)
                )
                TextButton(
                    onClick = {
                      onRemoveChecks(tags)
                    },
                ) {
                    Text("Remove check(s)")
                }
            }
            TagList(
                tags = tags,
                remainingTags = remainingTags,
                checkedTags = checkedTags,
                onCheckedTagChange = onCheckedTagChange,
                onAddTag = onAddTag,
                onRemoveTag = onRemoveTag,
                onModifyTagName = onModifyTagName
            )
        }
    }
}

@Composable
fun TagInput(
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
    remainingTags: List<String>,
    checkedTags: Map<String, Boolean>,
    onCheckedTagChange: (String, Boolean) -> Unit,
    onAddTag: (String) -> Unit,
    onRemoveTag: (String) -> Unit,
    onModifyTagName: (String, String) -> Unit
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
            items(if (remainingTags.isNotEmpty()) remainingTags.sortedBy { it.lowercase() } else tags.sortedBy { it.lowercase() }) { tag ->
                TagItem(
                    tag = tag,
                    isChecked = checkedTags[tag] ?: false,
                    onCheckedChange = { isChecked ->
                        onCheckedTagChange(tag, isChecked)
                    },
                    onRemoveTag = {
                        onRemoveTag(tag)
                    },
                    onModifyTagName = onModifyTagName
                )
            }
        }
        TagInput(
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
    onModifyTagName: (String, String) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isTagEditing by remember { mutableStateOf(false) }
    var editedTagName by remember { mutableStateOf(tag) }

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
            if (isTagEditing) {
                TextField(
                    value = editedTagName,
                    onValueChange = { editedTagName = it },
                    singleLine = true,
                    modifier = Modifier.widthIn(min = 100.dp, max = 200.dp)
                )
            } else {
                Text(
                    text = tag,
                    fontSize = 12.sp,
                )
            }
        }

        Row {
            if (isTagEditing) {
                IconButton(
                    onClick = {
                        onModifyTagName(tag, editedTagName)
                        isTagEditing = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm tag",
                        modifier = Modifier.size(12.dp)
                    )
                }
                IconButton(
                    onClick = {
                        editedTagName = tag
                        isTagEditing = false
                    }
                ) {
                    Icon(
                        painterResource(id = R.drawable.undo_button),
                        contentDescription = "Cancel tag",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }

            } else {
                IconButton(
                    onClick = {
                        isTagEditing = true
                        editedTagName = tag
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit tag",
                        modifier = Modifier.size(12.dp)
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                title = { Text(text = "Delete Tag") },
                text = { Text(text = "Are you sure you want to delete this tag?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRemoveTag()
                            showDeleteDialog = false
                        }) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text(text = "No")
                    }
                },
                onDismissRequest = { showDeleteDialog = false },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TagListPreview() {
}
