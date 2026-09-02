package com.example.virgoyt.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun SecretCredentialBoxDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🔒 API Secrets & Vault") },
        text = { Text("Gemini API, DeepSeek, and GitHub credentials securely loaded via BuildConfig.") },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}
