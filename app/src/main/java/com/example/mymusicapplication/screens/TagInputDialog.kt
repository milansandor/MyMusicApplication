package com.example.mymusicapplication.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun TagInputDialog(
    onDismiss: () -> Unit,
    onTagAdded: (String) -> Unit
) {
    var tagInput by remember { mutableStateOf("") } // State to hold the input value

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add Tag") },
        text = {
            Column {
                TextField(
                    value = tagInput,
                    onValueChange = { tagInput = it },
                    label = { Text("Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (tagInput.isNotBlank()) {
                        onTagAdded(tagInput) // Call the callback to add the tag
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
