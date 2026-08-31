package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Html
import androidx.compose.material.icons.filled.Javascript
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.VirtualFile
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusIndigoSoft
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate600
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.TermBg
import com.example.ui.theme.TermText

@Composable
fun CodeEditorView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val files by viewModel.vfs.fileListState.collectAsState()
    val selectedFile by viewModel.selectedFile.collectAsState()
    val editorContent by viewModel.editorContent.collectAsState()
    val isDirty by viewModel.isEditorDirty.collectAsState()

    // Cursor AI States
    val isCursorAiOpen by viewModel.isCursorAiEditorOpen.collectAsState()
    val isCursorGenerating by viewModel.isCursorAiGenerating.collectAsState()
    val cursorPendingResult by viewModel.cursorAiPendingResult.collectAsState()
    val cursorExplanation by viewModel.cursorAiExplanationDialog.collectAsState()

    var cursorPromptInput by remember { mutableStateOf("") }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var newFilePathInput by remember { mutableStateOf("") }
    var showFileDrawer by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
    ) {
        // Sleek Top Toolbar: File tabs matching design (`main.py`, `utils.js`, `.env`), Cursor AI, Save, Run in Terminal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate850)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // File Tabs list
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(files.filter { !it.isDirectory }) { file ->
                        FileTabItem(
                            file = file,
                            isSelected = selectedFile?.path == file.path,
                            isDirty = isDirty && selectedFile?.path == file.path,
                            onClick = { viewModel.selectFile(file) }
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Action icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Cursor AI Trigger Button (Cmd+K style)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCursorAiOpen) ManusIndigo else ManusIndigoBg)
                            .border(1.dp, if (isCursorAiOpen) ManusIndigoLight else ManusIndigo.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .clickable { viewModel.toggleCursorAiEditor() }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("editor_cursor_ai_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Cursor AI",
                                tint = if (isCursorAiOpen) ManusWhite else ManusIndigoLight,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Cursor",
                                color = if (isCursorAiOpen) ManusWhite else ManusIndigoLight,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Explain Code with Cursor
                    IconButton(
                        onClick = { viewModel.explainCurrentCodeWithCursor() },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SleekSurface)
                            .testTag("editor_explain_code_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Explain with Cursor AI",
                            tint = ManusCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // New file
                    IconButton(
                        onClick = {
                            newFilePathInput = "/workspace/"
                            showNewFileDialog = true
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SleekSurface)
                            .testTag("editor_new_file_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New File",
                            tint = ManusSlate200,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Save file
                    IconButton(
                        onClick = { viewModel.saveCurrentFile() },
                        enabled = isDirty,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDirty) ManusIndigo else SleekSurface)
                            .testTag("editor_save_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save File",
                            tint = if (isDirty) ManusWhite else ManusSlate600,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Run in terminal
                    IconButton(
                        onClick = {
                            if (selectedFile != null) {
                                viewModel.runCurrentFileInTerminal()
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ManusIndigoBg)
                            .testTag("editor_run_terminal_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Run in Terminal",
                            tint = ManusIndigoLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Toggle Workspace File Explorer
                    IconButton(
                        onClick = { showFileDrawer = !showFileDrawer },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (showFileDrawer) ManusIndigoBg else SleekSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = "File Explorer",
                            tint = if (showFileDrawer) ManusIndigoLight else ManusSlate400,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        // Subheader: Path & language badge & live preview shortcut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate900)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = selectedFile?.path ?: "No file selected",
                        color = ManusSlate400,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    if (isDirty) {
                        Text(
                            text = "(unsaved)",
                            color = ManusAmber,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (selectedFile?.name?.endsWith(".html") == true || selectedFile?.name?.endsWith(".js") == true) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusIndigoBg)
                                .clickable { viewModel.selectTab(ActiveWorkspaceTab.BROWSER) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = ManusIndigoLight, modifier = Modifier.size(11.dp))
                                Text("Preview Sandbox", color = ManusIndigoLight, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (selectedFile != null && selectedFile?.path != "/workspace/index.html") {
                        IconButton(
                            onClick = {
                                viewModel.deleteCurrentFile()
                            },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete File",
                                tint = ManusRed.copy(alpha = 0.7f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }

        // Workspace File Drawer (Optional collapsible view)
        if (showFileDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder)
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(files) { file ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (selectedFile?.path == file.path) ManusIndigoBg else Color.Transparent)
                                .clickable {
                                    if (!file.isDirectory) {
                                        viewModel.selectFile(file)
                                        showFileDrawer = false
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (file.isDirectory) ManusIndigoLight else ManusSlate400,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = file.path,
                                    color = if (selectedFile?.path == file.path) ManusWhite else ManusSlate400,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "${file.sizeBytes} B",
                                color = ManusSlate600,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // Cursor AI Inline Assistant (Cmd+K Prompt Panel)
        // ==========================================
        if (isCursorAiOpen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ManusSlate900)
                    .border(1.dp, ManusIndigo.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ManusIndigoLight,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Cursor AI Inline Assistant (Cmd+K)",
                                color = ManusWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { viewModel.closeCursorAiEditor() },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = ManusSlate400,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Cursor Prompt Input Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = cursorPromptInput,
                            onValueChange = { cursorPromptInput = it },
                            placeholder = {
                                Text(
                                    "Ask Cursor to edit code, generate UI, refactor, or fix...",
                                    color = ManusSlate500,
                                    fontSize = 11.5.sp
                                )
                            },
                            singleLine = true,
                            enabled = !isCursorGenerating,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("cursor_editor_prompt_input"),
                            textStyle = TextStyle(
                                color = ManusWhite,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManusIndigo,
                                unfocusedBorderColor = SleekBorder,
                                focusedTextColor = ManusWhite,
                                unfocusedTextColor = ManusWhite,
                                focusedContainerColor = ManusSlate850,
                                unfocusedContainerColor = ManusSlate850
                            )
                        )

                        Button(
                            onClick = {
                                if (cursorPromptInput.isNotBlank()) {
                                    viewModel.generateCursorCode(cursorPromptInput.trim())
                                }
                            },
                            enabled = cursorPromptInput.isNotBlank() && !isCursorGenerating,
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("cursor_generate_button")
                        ) {
                            if (isCursorGenerating) {
                                Text("Thinking...", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ManusWhite, modifier = Modifier.size(13.dp))
                                    Text("Apply", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Quick Cursor Action Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            CursorPromptChip(
                                label = "⚡ Add Dark Mode",
                                onClick = {
                                    cursorPromptInput = "Add high-contrast dark mode styling and theme support"
                                    viewModel.generateCursorCode(cursorPromptInput)
                                }
                            )
                        }
                        item {
                            CursorPromptChip(
                                label = "🛠️ Fix Bugs & Types",
                                onClick = {
                                    cursorPromptInput = "Fix potential bugs, null pointers, and add error boundary guards"
                                    viewModel.generateCursorCode(cursorPromptInput)
                                }
                            )
                        }
                        item {
                            CursorPromptChip(
                                label = "✨ Refactor & Clean",
                                onClick = {
                                    cursorPromptInput = "Refactor and optimize code structure for modern readability"
                                    viewModel.generateCursorCode(cursorPromptInput)
                                }
                            )
                        }
                        item {
                            CursorPromptChip(
                                label = "🧪 Add Unit Tests",
                                onClick = {
                                    cursorPromptInput = "Write complete automated unit test suite for this file"
                                    viewModel.generateCursorCode(cursorPromptInput)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // Cursor AI Diff / Review Banner
        // ==========================================
        if (cursorPendingResult != null) {
            val pending = cursorPendingResult!!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ManusIndigoBg)
                    .border(1.dp, ManusIndigo, RoundedCornerShape(0.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ManusIndigoLight, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Cursor AI Generated Code Ready",
                                color = ManusWhite,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = pending.diffSummary.ifBlank { pending.explanation },
                            color = ManusSlate200,
                            fontSize = 10.5.sp,
                            maxLines = 1
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Accept
                        Button(
                            onClick = { viewModel.acceptCursorAiCode() },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusGreen),
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("cursor_accept_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ManusSlate950, modifier = Modifier.size(13.dp))
                                Text("Accept", color = ManusSlate950, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Reject
                        Button(
                            onClick = { viewModel.rejectCursorAiCode() },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusSlate800),
                            shape = RoundedCornerShape(5.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp).testTag("cursor_reject_button")
                        ) {
                            Text("Discard", color = ManusSlate400, fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }

        // Main Code Editor Area with Line Numbers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(TermBg)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Line Number Gutter
                val lineCount = editorContent.lines().size.coerceAtLeast(1)
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(36.dp)
                        .background(ManusSlate950)
                        .border(1.dp, SleekBorder)
                        .verticalScroll(scrollState)
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    for (i in 1..lineCount) {
                        Text(
                            text = "$i",
                            color = ManusSlate600,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }

                // Text Input Area
                OutlinedTextField(
                    value = editorContent,
                    onValueChange = { viewModel.updateEditorContent(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("code_editor_textarea"),
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp,
                        color = TermText,
                        lineHeight = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = TermBg,
                        unfocusedContainerColor = TermBg,
                        focusedTextColor = TermText,
                        unfocusedTextColor = TermText
                    )
                )
            }
        }
    }

    // New File Dialog
    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text("Create New Workspace File", color = ManusWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter relative or full path (e.g. /workspace/game.js):", color = ManusSlate400, fontSize = 11.sp)
                    OutlinedTextField(
                        value = newFilePathInput,
                        onValueChange = { newFilePathInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManusIndigo,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite,
                            focusedContainerColor = ManusSlate850,
                            unfocusedContainerColor = ManusSlate850
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFilePathInput.isNotBlank()) {
                            viewModel.createNewFile(newFilePathInput.trim())
                            showNewFileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                ) {
                    Text("Create", color = ManusWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Cancel", color = ManusSlate400)
                }
            },
            containerColor = ManusSlate900
        )
    }

    // Cursor AI Explanation Dialog
    if (cursorExplanation != null) {
        AlertDialog(
            onDismissRequest = { viewModel.closeCursorExplanationDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = ManusCyan, modifier = Modifier.size(20.dp))
                    Text("Cursor AI Code Explanation", color = ManusWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = cursorExplanation!!,
                        color = ManusSlate200,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.closeCursorExplanationDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo)
                ) {
                    Text("Close", color = ManusWhite, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = ManusSlate900
        )
    }
}

@Composable
private fun CursorPromptChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(ManusIndigoBg)
            .border(1.dp, ManusIndigo.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = ManusIndigoSoft,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FileTabItem(
    file: VirtualFile,
    isSelected: Boolean,
    isDirty: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) ManusIndigoBg else SleekSurface
    val border = if (isSelected) ManusIndigo.copy(alpha = 0.4f) else Color.Transparent
    val textColor = if (isSelected) ManusIndigoSoft else ManusSlate400

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("file_tab_${file.name}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "${file.name}${if (isDirty) "*" else ""}",
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
