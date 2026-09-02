package com.example.virgoyt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun GitHubAuthDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return
    val user by viewModel.githubManager.currentUser.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🐙 GitHub Integration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Connected as: ${user?.name ?: "virgoyt-dev"}")
                Text("Public Repos: ${user?.publicRepos ?: 18}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
