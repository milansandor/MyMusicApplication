package com.example.mymusicapplication.ui.screens.permission

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                text = if (isPermanentlyDeclined) {
                    "Grant permission"
                } else {
                    "OK"
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettings()
                        } else {
                            onOkClick()
                        }
                    }
                    .padding(16.dp)
            )
        },
        title = {
            Text(text = "Permission required")
        },
        text = {
            Text(
                text = permissionTextProvider.getDescription(
                    isPermanentlyDeclined = isPermanentlyDeclined
                )
            )
        },
        modifier = modifier
    )
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class ReadExternalStoragePermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined read storage permission. " +
                    "If you want this application to have access to your music " +
                    "go to app setting to grant it."
        } else {
            "This app needs access to read your External Storage to be able to play your music."
        }
    }
}

class ReadMediaVisualUserSelectedPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined to hand pick the access for your visual media permission. " +
                    "If you want this application to have access to your visual media " +
                    "go to app setting to grant it."
        } else {
            "This app needs access to read your visual media to be able to display album covers."
        }
    }
}

class ReadMediaAudioPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined read media audio permission. " +
                    "If you want this application to have access to your music " +
                    "go to app setting to grant it."
        } else {
            "This app needs access to read your media audio to be able to play your music."
        }
    }
}

class ReadMediaImagesPermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined read media images permission. " +
                    "If you want this application to have access to your images " +
                    "go to app setting to grant it."
        } else {
            "This app needs access to read your media images to be able to show existing album arts."
        }
    }
}


class WriteExternalStoragePermissionTextProvider: PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined write storage permission. " +
                    "If you want this application to customise your genre fields " +
                    "go to app setting to grant it."
        } else {
            "This app needs access to write on your External Storage to be able to use the tagging function."
        }
    }
}
