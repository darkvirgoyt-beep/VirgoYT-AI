package com.example.virgoyt.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun AuthDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Authentication") },
        text = { Text("You are signed in as virgoyt.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}
