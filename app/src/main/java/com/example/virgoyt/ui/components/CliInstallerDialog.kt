package com.example.virgoyt.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun CliInstallerDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("💻 Install VirgoYT CLI") },
        text = { Text("Run 'curl -fsSL https://get.virgoyt.ai | bash' on your host machine.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
