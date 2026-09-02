package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun CodeEditorView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    var selectedFilePath by remember { mutableStateOf("/workspace/src/App.tsx") }
    var fileContent by remember { mutableStateOf(viewModel.vfs.readFile(selectedFilePath) ?: "") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("code_editor_view")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💻 $selectedFilePath",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF06B6D4)
            )
            Row {
                Button(
                    onClick = {
                        val aiSuggestion = fileContent + "\n// AI Refactor: Verified high performance rendering"
                        fileContent = aiSuggestion
                        viewModel.vfs.writeFile(selectedFilePath, aiSuggestion)
                        viewModel.showToast("AI code optimization applied")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = "AI Assist", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Refactor", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = {
                    viewModel.vfs.writeFile(selectedFilePath, fileContent)
                    viewModel.showToast("Saved $selectedFilePath")
                }) {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = Color(0xFF10B981))
                }
            }
        }

        // Code Editor Box
        TextField(
            value = fileContent,
            onValueChange = { fileContent = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                .border(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                .testTag("editor_text_area"),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = if (themeMode.isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A),
                unfocusedTextColor = if (themeMode.isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
            ),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        )
    }
}
