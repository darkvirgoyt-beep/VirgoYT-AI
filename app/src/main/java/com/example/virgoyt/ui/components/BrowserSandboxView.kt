package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun BrowserSandboxView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    var previewHtml by remember { mutableStateOf(viewModel.vfs.getBundledWebPreviewHtml()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("browser_sandbox_view")
    ) {
        // Address Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔒 https://sandbox.virgoyt.ai/dev", fontSize = 12.sp, color = Color(0xFF06B6D4), modifier = Modifier.weight(1f))
            IconButton(onClick = { previewHtml = viewModel.vfs.getBundledWebPreviewHtml() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF06B6D4))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (themeMode.isDark) Color(0xFF0B0F19) else Color(0xFFFFFFFF),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌐 Live Next.js Web Preview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF38BDF8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Turbopack dev server active on port :3000. Real-time HMR and DOM sync ready.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(if (themeMode.isDark) Color(0xFF020617) else Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Rendering preview payload:\n\n" + previewHtml.take(300) + "...",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}
