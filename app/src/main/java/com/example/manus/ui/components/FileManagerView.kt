package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.VirtualFile
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusBorderLight
import com.example.ui.theme.ManusCyanAccent
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FileModalType {
    NONE,
    CREATE_FILE,
    CREATE_FOLDER,
    RENAME,
    COPY,
    MOVE,
    UPLOAD,
    DOWNLOAD,
    DELETE_CONFIRM
}

@Composable
fun FileManagerView(viewModel: ManusCloudViewModel) {
    val allFiles by viewModel.vfs.fileListState.collectAsState()
    val currentDir by viewModel.explorerCurrentDir.collectAsState()
    val searchQuery by viewModel.fileSearchQuery.collectAsState()
    val session by viewModel.currentSession.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var selectedFileForAction by remember { mutableStateOf<VirtualFile?>(null) }
    var activeModal by remember { mutableStateOf(FileModalType.NONE) }
    var modalInputName by remember { mutableStateOf("") }
    var modalInputTargetDir by remember { mutableStateOf("") }
    var modalUploadContent by remember { mutableStateOf("") }

    // Filter files in current directory or search query
    val displayedFiles = remember(allFiles, currentDir, searchQuery) {
        if (searchQuery.isNotBlank()) {
            allFiles.filter { it.name.contains(searchQuery, ignoreCase = true) || it.path.contains(searchQuery, ignoreCase = true) }
        } else {
            val normalizedDir = if (currentDir.endsWith("/")) currentDir.dropLast(1) else currentDir
            allFiles.filter { file ->
                val parent = file.path.substringBeforeLast('/')
                val normalizedParent = if (parent.isEmpty()) "/" else parent
                (normalizedParent == normalizedDir || file.path.startsWith("$normalizedDir/")) && file.path != normalizedDir
            }.filter { file ->
                // Only direct children
                val rel = file.path.removePrefix("$normalizedDir/")
                !rel.contains('/')
            }.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.name })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
            .testTag("file_manager_view")
    ) {
        // Top Action Bar & Quick Shortcuts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shortcuts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                Text(
                    text = "VFS Shortcuts:",
                    color = ManusSlate400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                val userHome: String = session?.user?.homeDir ?: "/home/developer"
                listOf<Pair<String, String>>(
                    "/workspace" to "Workspace",
                    userHome to "Home",
                    "/workspace/scripts" to "Scripts",
                    "/workspace/data" to "Data"
                ).forEach { (path, label) ->
                    val isCurrent = currentDir == path
                    Box(
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCurrent) ManusIndigo.copy(alpha = 0.3f) else ManusSlate800)
                            .border(1.dp, if (isCurrent) ManusIndigo else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable {
                                viewModel.setExplorerDir(path)
                                viewModel.setFileSearchQuery("")
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isCurrent) ManusWhite else ManusSlate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Operations Toolbar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        modalInputName = ""
                        activeModal = FileModalType.CREATE_FILE
                    },
                    modifier = Modifier.size(30.dp).testTag("file_manager_btn_new_file")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New File",
                        tint = ManusIndigoLight,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = {
                        modalInputName = ""
                        activeModal = FileModalType.CREATE_FOLDER
                    },
                    modifier = Modifier.size(30.dp).testTag("file_manager_btn_new_folder")
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "New Folder",
                        tint = ManusEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = {
                        modalInputName = "sample_data.json"
                        modalUploadContent = "{\n  \"status\": \"active\",\n  \"imported_at\": \"${Date()}\"\n}"
                        activeModal = FileModalType.UPLOAD
                    },
                    modifier = Modifier.size(30.dp).testTag("file_manager_btn_upload")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = "Upload File",
                        tint = ManusCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Breadcrumb Navigation & Search Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Breadcrumbs / Back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
            ) {
                if (currentDir != "/" && currentDir != "/workspace") {
                    IconButton(
                        onClick = {
                            val parent = currentDir.substringBeforeLast('/')
                            viewModel.setExplorerDir(if (parent.isEmpty()) "/" else parent)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ManusSlate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Text(
                    text = currentDir,
                    color = ManusWhite,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Search Bar
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ManusSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setFileSearchQuery(it) },
                        placeholder = { Text("Filter...", fontSize = 10.sp, color = ManusSlate400) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Files Table / List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                .padding(8.dp)
        ) {
            if (displayedFiles.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "Empty",
                            tint = ManusSlate700,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Directory is empty", color = ManusSlate400, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(displayedFiles) { file ->
                        val dateFormatted = SimpleDateFormat("MMM dd HH:mm", Locale.US).format(Date(file.lastModified))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate950.copy(alpha = 0.5f))
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (file.isDirectory) {
                                        viewModel.setExplorerDir(file.path)
                                    } else {
                                        viewModel.selectFile(file)
                                        viewModel.selectTab(ActiveWorkspaceTab.EDITOR)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // File Icon and Name
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
                                    contentDescription = if (file.isDirectory) "Dir" else "File",
                                    tint = if (file.isDirectory) ManusCyanAccent else ManusIndigoLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = file.name,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = file.permissions,
                                            color = ManusSlate400,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(text = " • ", color = ManusSlate700, fontSize = 9.sp)
                                        Text(
                                            text = "${file.sizeBytes} B",
                                            color = ManusSlate400,
                                            fontSize = 9.sp
                                        )
                                        Text(text = " • ", color = ManusSlate700, fontSize = 9.sp)
                                        Text(
                                            text = file.owner,
                                            color = ManusEmerald,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            // Action Buttons
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!file.isDirectory) {
                                    // Open in Editor
                                    IconButton(
                                        onClick = {
                                            viewModel.selectFile(file)
                                            viewModel.selectTab(ActiveWorkspaceTab.EDITOR)
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Code,
                                            contentDescription = "Edit",
                                            tint = ManusIndigoLight,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    // Run in Terminal
                                    IconButton(
                                        onClick = {
                                            viewModel.selectFile(file)
                                            viewModel.runCurrentFileInTerminal()
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Run",
                                            tint = ManusEmerald,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }

                                    // Download / Export
                                    IconButton(
                                        onClick = {
                                            selectedFileForAction = file
                                            activeModal = FileModalType.DOWNLOAD
                                        },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FileDownload,
                                            contentDescription = "Download",
                                            tint = ManusCyanAccent,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                // Rename
                                IconButton(
                                    onClick = {
                                        selectedFileForAction = file
                                        modalInputName = file.name
                                        activeModal = FileModalType.RENAME
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Rename",
                                        tint = ManusSlate400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                // Copy
                                IconButton(
                                    onClick = {
                                        selectedFileForAction = file
                                        modalInputTargetDir = currentDir
                                        activeModal = FileModalType.COPY
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = ManusSlate400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                // Move
                                IconButton(
                                    onClick = {
                                        selectedFileForAction = file
                                        modalInputTargetDir = "/workspace"
                                        activeModal = FileModalType.MOVE
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DriveFileMove,
                                        contentDescription = "Move",
                                        tint = ManusSlate400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                // Delete
                                IconButton(
                                    onClick = {
                                        selectedFileForAction = file
                                        activeModal = FileModalType.DELETE_CONFIRM
                                    },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs for File Operations
    if (activeModal != FileModalType.NONE) {
        Dialog(onDismissRequest = { activeModal = FileModalType.NONE }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SleekBorder, RoundedCornerShape(14.dp)),
                color = ManusSlate950
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    when (activeModal) {
                        FileModalType.CREATE_FILE -> {
                            Text("Create New File", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputName,
                                onValueChange = { modalInputName = it },
                                label = { Text("File Name (e.g. script.py)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (modalInputName.isNotBlank()) {
                                            viewModel.createNewFile(modalInputName.trim())
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                                ) {
                                    Text("Create")
                                }
                            }
                        }

                        FileModalType.CREATE_FOLDER -> {
                            Text("Create New Directory", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputName,
                                onValueChange = { modalInputName = it },
                                label = { Text("Directory Name (e.g. components)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusEmerald,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (modalInputName.isNotBlank()) {
                                            viewModel.createNewDirectory(modalInputName.trim())
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusEmerald)
                                ) {
                                    Text("Create Folder")
                                }
                            }
                        }

                        FileModalType.RENAME -> {
                            Text("Rename '${selectedFileForAction?.name}'", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputName,
                                onValueChange = { modalInputName = it },
                                label = { Text("New Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (selectedFileForAction != null && modalInputName.isNotBlank()) {
                                            viewModel.renameFileOrDirectory(selectedFileForAction!!.path, modalInputName.trim())
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                                ) {
                                    Text("Rename")
                                }
                            }
                        }

                        FileModalType.COPY -> {
                            Text("Copy '${selectedFileForAction?.name}'", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputTargetDir,
                                onValueChange = { modalInputTargetDir = it },
                                label = { Text("Destination Directory") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (selectedFileForAction != null && modalInputTargetDir.isNotBlank()) {
                                            viewModel.copyFileOrDirectory(selectedFileForAction!!.path, modalInputTargetDir.trim())
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                                ) {
                                    Text("Copy")
                                }
                            }
                        }

                        FileModalType.MOVE -> {
                            Text("Move '${selectedFileForAction?.name}'", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputTargetDir,
                                onValueChange = { modalInputTargetDir = it },
                                label = { Text("Target Directory") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusCyanAccent,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (selectedFileForAction != null && modalInputTargetDir.isNotBlank()) {
                                            viewModel.moveFileOrDirectory(selectedFileForAction!!.path, modalInputTargetDir.trim())
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusCyanAccent)
                                ) {
                                    Text("Move", color = ManusSlate950, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        FileModalType.UPLOAD -> {
                            Text("Upload File to Sandbox", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = modalInputName,
                                onValueChange = { modalInputName = it },
                                label = { Text("File Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusCyanAccent,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = modalUploadContent,
                                onValueChange = { modalUploadContent = it },
                                label = { Text("File Content / Payload") },
                                maxLines = 6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusCyanAccent,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (modalInputName.isNotBlank()) {
                                            viewModel.importUploadedFile(modalInputName.trim(), modalUploadContent)
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusCyanAccent)
                                ) {
                                    Text("Upload to Sandbox", color = ManusSlate950, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        FileModalType.DOWNLOAD -> {
                            val f = selectedFileForAction
                            Text("Download / Export '${f?.name}'", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ManusSlate900)
                                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = f?.content ?: "",
                                    color = ManusSlate400,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Close", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (f != null) {
                                            clipboardManager.setText(AnnotatedString(f.content))
                                            viewModel.showToast("✓ Copied ${f.name} to clipboard!")
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                                ) {
                                    Text("Copy Content")
                                }
                            }
                        }

                        FileModalType.DELETE_CONFIRM -> {
                            Text("Confirm Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Are you sure you want to remove '${selectedFileForAction?.name}'? This action cannot be undone.",
                                color = ManusSlate400,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { activeModal = FileModalType.NONE }) {
                                    Text("Cancel", color = ManusSlate400)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (selectedFileForAction != null) {
                                            viewModel.deleteFileOrDirectory(selectedFileForAction!!.path)
                                            activeModal = FileModalType.NONE
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                                ) {
                                    Text("Delete")
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
