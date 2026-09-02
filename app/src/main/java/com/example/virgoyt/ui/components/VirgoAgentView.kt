package com.example.virgoyt.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.data.model.*
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun VirgoAgentView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val sessions by viewModel.routerEngine.activeChatSessions.collectAsState()
    val currentSessionId by viewModel.routerEngine.currentSessionId.collectAsState()
    val selectedModel by viewModel.routerEngine.selectedModel.collectAsState()
    val currentSession = sessions.find { it.id == currentSessionId } ?: sessions.firstOrNull()
    val isAgentBusy by viewModel.agentService.isAgentBusy.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("virgo_agent_view_root")
    ) {
        MultiAgentTeamSection(viewModel = viewModel)

        // Model Selector & Live Status Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (themeMode.isDark) Color(0xFF0D1527) else Color(0xFFF1F5F9),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isAgentBusy) Color(0xFFF59E0B) else Color(0xFF10B981))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAgentBusy) "AI Swarm Reasoning..." else "Model: ${selectedModel.displayName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF06B6D4)
                    )
                }
                TextButton(
                    onClick = {
                        val nextModel = if (selectedModel == AiModelTier.AUTO_ROUTER) AiModelTier.GEMINI_2_5_PRO else AiModelTier.AUTO_ROUTER
                        viewModel.routerEngine.selectModel(nextModel)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Switch (${selectedModel.badgeEmoji})", fontSize = 11.sp, color = Color(0xFF38BDF8))
                }
            }
        }

        // Unified Chat Workstation List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 6.dp)
        ) {
            items(currentSession?.messages ?: emptyList()) { message ->
                ChatMessageItem(
                    message = message,
                    isDark = themeMode.isDark,
                    onChipClick = { cmd -> viewModel.executePrompt(cmd) },
                    onRunCommand = { cmd ->
                        viewModel.runTerminalCommand(cmd)
                        viewModel.showToast("Executed: $cmd")
                    },
                    onCopyCode = { code ->
                        viewModel.showToast("Copied to clipboard!")
                    }
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDark: Boolean,
    onChipClick: (String) -> Unit,
    onRunCommand: (String) -> Unit,
    onCopyCode: (String) -> Unit
) {
    val isUser = message.role == "user"
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Avatar / Identity Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            if (isUser) {
                Text("🧑‍💻 You", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
            } else {
                Text(message.modelUsed.badgeEmoji, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "VirgoYT AI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF06B6D4)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF0284C7).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = message.modelUsed.displayName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF38BDF8),
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }

        // Bubble Content
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) {
                Color(0xFF0284C7)
            } else {
                if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF)
            },
            border = if (!isUser) {
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                )
            } else null,
            shadowElevation = if (isUser) 2.dp else 4.dp,
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.86f else 0.98f)
                .testTag(if (isUser) "user_message_bubble" else "ai_message_bubble")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Hard Thinking / Chain-of-Thought Reasoning Block (Deep Think mode)
                if (!isUser && !message.reasoningThought.isNullOrBlank()) {
                    var isReasoningExpanded by remember { mutableStateOf(false) }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDark) Color(0xFF1E1B4B).copy(alpha = 0.5f) else Color(0xFFFAF5FF),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0xFF6366F1).copy(alpha = 0.4f) else Color(0xFFC084FC)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isReasoningExpanded = !isReasoningExpanded }
                            .testTag("thought_reasoning_block")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = Color(0xFFA855F7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Deep Reasoning Thought Process",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFA855F7)
                                    )
                                }
                                Icon(
                                    imageVector = if (isReasoningExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand Reasoning",
                                    tint = Color(0xFFA855F7),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (isReasoningExpanded) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = message.reasoningThought!!,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp,
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Conversational Text / Markdown
                FormattedChatText(
                    text = message.content,
                    isUser = isUser,
                    isDark = isDark
                )

                // Generated Files Artifacts (Files created directly in project)
                if (message.generatedFiles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "📁 Files Created in Workspace:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.generatedFiles.forEach { file ->
                            GeneratedFileCard(file = file, isDark = isDark)
                        }
                    }
                }

                // Media & Artwork Generations (Images / Videos)
                if (message.mediaGenerations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.mediaGenerations.forEach { media ->
                            MediaGenerationCard(media = media, isDark = isDark)
                        }
                    }
                }

                // Inline Code Snippets (Unified in chat)
                if (message.codeSnippets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    message.codeSnippets.forEach { snippet ->
                        CodeBlockCard(
                            snippet = snippet,
                            isDark = isDark,
                            onCopy = { code ->
                                clipboardManager.setText(AnnotatedString(code))
                                onCopyCode(code)
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                // Inline File Diff (Unified in chat)
                if (message.inlineDiff != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    InlineDiffCard(diff = message.inlineDiff, isDark = isDark)
                }

                // Inline Terminal Commands (Unified in chat)
                if (message.terminalCommands.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.terminalCommands.forEach { cmd ->
                            TerminalCommandCard(
                                command = cmd,
                                isDark = isDark,
                                onRun = { onRunCommand(cmd) }
                            )
                        }
                    }
                }

                // Follow-up Questions (AI asking questions back to the user)
                if (message.followUpQuestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 What would you like to build or customize next?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        message.followUpQuestions.forEach { question ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0284C7).copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onChipClick(question) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⚡", fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = question,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFFE0F2FE) else Color(0xFF0369A1)
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Action Chips
                if (message.quickActionChips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        message.quickActionChips.forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF06B6D4).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onChipClick(chip.actionCommand) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(chip.iconEmoji, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = chip.label,
                                        fontSize = 11.sp,
                                        color = Color(0xFF06B6D4),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormattedChatText(
    text: String,
    isUser: Boolean,
    isDark: Boolean
) {
    val lines = text.split("\n")
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.removePrefix("### "),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White else Color(0xFF38BDF8),
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.removePrefix("## "),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) Color.White else Color(0xFF06B6D4),
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("• ") || trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = "• ",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) Color.White else Color(0xFF06B6D4)
                        )
                        Text(
                            text = trimmed.drop(2),
                            fontSize = 13.sp,
                            color = if (isUser) Color.White else if (isDark) Color(0xFFCBD5E1) else Color(0xFF1E293B)
                        )
                    }
                }
                else -> {
                    if (trimmed.isNotEmpty()) {
                        Text(
                            text = line,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = if (isUser) Color.White else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlockCard(
    snippet: CodeBlockSnippet,
    isDark: Boolean,
    onCopy: (String) -> Unit
) {
    var copied by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0A0F1D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Code block title & copy button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📄", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = snippet.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF94A3B8)
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            onCopy(snippet.code)
                            copied = true
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (copied) Color(0xFF10B981) else Color(0xFF38BDF8),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (copied) "Copied" else "Copy",
                        fontSize = 10.sp,
                        color = if (copied) Color(0xFF10B981) else Color(0xFF38BDF8)
                    )
                }
            }

            // Monospace code
            Text(
                text = snippet.code,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = Color(0xFFE2E8F0),
                modifier = Modifier
                    .padding(10.dp)
                    .horizontalScroll(rememberScrollState())
            )
        }
    }
}

@Composable
fun InlineDiffCard(
    diff: DiffSnippet,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0A0F1D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Unified Diff: ${diff.filePath}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }
                Row {
                    Text("+${diff.additionsCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("-${diff.deletionsCount}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                diff.diffText.split("\n").forEach { line ->
                    val isAdd = line.startsWith("+")
                    val isSub = line.startsWith("-")
                    val isHeader = line.startsWith("@@") || line.startsWith("---") || line.startsWith("+++")
                    val lineBg = when {
                        isAdd -> Color(0xFF10B981).copy(alpha = 0.15f)
                        isSub -> Color(0xFFEF4444).copy(alpha = 0.15f)
                        else -> Color.Transparent
                    }
                    val textColor = when {
                        isAdd -> Color(0xFF34D399)
                        isSub -> Color(0xFFF87171)
                        isHeader -> Color(0xFF38BDF8)
                        else -> Color(0xFF94A3B8)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(lineBg)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = textColor
                        )
                    }
                }
            }

            // Applied status badge
            Surface(
                color = Color(0xFF10B981).copy(alpha = 0.12f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "VFS Workspace Automatically Updated",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalCommandCard(
    command: String,
    isDark: Boolean,
    onRun: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = command,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF10B981),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onRun() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Run in Terminal",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun GeneratedFileCard(
    file: GeneratedFileArtifact,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFF10B981)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.path,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF34D399) else Color(0xFF059669)
                )
                Text(
                    text = file.description,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569)
                )
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "SAVED",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MediaGenerationCard(
    media: MediaGenerationArtifact,
    isDark: Boolean
) {
    val isImage = media.type == "image"
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF020617) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isImage) Color(0xFF06B6D4).copy(alpha = 0.5f) else Color(0xFFA855F7).copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isImage) Icons.Default.Image else Icons.Default.Movie,
                        contentDescription = null,
                        tint = if (isImage) Color(0xFF06B6D4) else Color(0xFFA855F7),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = media.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isImage) Color(0xFF38BDF8) else Color(0xFFC084FC)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isImage) Color(0xFF06B6D4).copy(alpha = 0.15f) else Color(0xFFA855F7).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = media.resolution,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isImage) Color(0xFF06B6D4) else Color(0xFFA855F7),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Prompt: \"${media.promptUsed}\"",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isDark) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Status: ${media.status}",
                        fontSize = 10.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isImage) "4K Vector / Diffusion Canvas Ready" else "Veo 3.1 60FPS Video Manifest",
                        fontSize = 9.sp,
                        color = if (isDark) Color(0xFF64748B) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

