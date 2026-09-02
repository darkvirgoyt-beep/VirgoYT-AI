package com.example.virgoyt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun UserProfileDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return
    val session by viewModel.authManager.currentSession.collectAsState()
    val user = session?.user

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("👤 User Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Username: ${user?.username ?: "virgoyt"}", fontWeight = FontWeight.SemiBold)
                Text("Email: ${user?.email ?: "developer@virgoyt.ai"}")
                Text("Role: ${user?.role ?: "Super Admin"}", color = Color(0xFF06B6D4))
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
