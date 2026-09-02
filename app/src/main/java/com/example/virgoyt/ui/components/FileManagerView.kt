package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.data.model.ActiveWorkspaceTab
import com.example.virgoyt.data.model.VirtualFile
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun FileManagerView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val allFiles by viewModel.vfs.fileListState.collectAsState()
    var currentDir by remember { mutableStateOf("/workspace") }
    val filesInDir = viewModel.vfs.listFilesInDir(currentDir)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("file_manager_view")
    ) {
        // Top Path Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📁 $currentDir",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF06B6D4)
            )
            Row {
                IconButton(onClick = {
                    viewModel.vfs.addFile("$currentDir/new_module_${System.currentTimeMillis() % 1000}.ts", "new_module.ts", "// New module\n")
                }) {
                    Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "New File", tint = Color(0xFF06B6D4))
                }
                IconButton(onClick = {
                    viewModel.vfs.createDirectory("$currentDir/folder_${System.currentTimeMillis() % 1000}")
                }) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder", tint = Color(0xFF06B6D4))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (currentDir != "/workspace") {
            Button(
                onClick = {
                    currentDir = currentDir.substringBeforeLast("/").ifEmpty { "/workspace" }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⬆ Up to Parent Directory")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filesInDir) { file ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (file.isDirectory) {
                                currentDir = file.path
                            } else {
                                viewModel.selectTab(ActiveWorkspaceTab.EDITOR)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (file.isDirectory) "📁" else "📄", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = file.name,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${file.sizeBytes} B • ${file.permissions}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.vfs.deleteFile(file.path) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}
