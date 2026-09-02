package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun CliInstallerDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: VirgoCloudViewModel
) {
    if (!isOpen) return

    val themeMode by viewModel.themeMode.collectAsState()
    val isDark = themeMode.isDark
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableStateOf(0) } // 0: Termux, 1: Node/NPM, 2: Python, 3: Fix Errors

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color(0xFF0B132B) else Color(0xFFFFFFFF),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF06B6D4).copy(alpha = 0.4f) else Color(0xFF0284C7)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("cli_installer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF06B6D4).copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = Color(0xFF06B6D4),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VirgoYT Terminal Agent Harness",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                            Text(
                                text = "Run interactive AI coding CLI like Claude Code in Termux",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isDark) Color(0xFF070D1E) else Color(0xFFF1F5F9))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf("📱 Termux", "⚡ Node/npx", "🐍 Python", "⚠️ Fix Errors")
                    tabs.forEachIndexed { index, title ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (selectedTab == index) Color(0xFF0284C7) else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            onClick = { selectedTab = index }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // Termux Tab
                        Text(
                            text = "🚀 Run Directly Inside Termux (Android)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "No Java or OpenJDK needed! Run VirgoYT interactive terminal coding harness directly inside Termux just like Claude Code:",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CliCommandCard(
                            title = "1-Line Setup & Run (Termux)",
                            command = "pkg update -y && pkg install -y nodejs git curl && curl -sSL https://raw.githubusercontent.com/darkvirgoyt-beep/VirgoYT-AI/main/cli/install.sh | bash",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied Termux 1-liner to clipboard!")
                            },
                            isDark = isDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CliCommandCard(
                            title = "Instant REPL Harness (If Node.js installed)",
                            command = "npx virgoyt-ai",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied 'npx virgoyt-ai' to clipboard!")
                            },
                            isDark = isDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "💡 After installation, simply type 'virgoyt' in any directory to start coding, editing files, and executing bash commands!",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981)
                        )
                    }

                    1 -> {
                        // Node / NPM Tab
                        Text(
                            text = "⚡ Node.js & npm / npx CLI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Zero install required with npx or install globally via npm:",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CliCommandCard(
                            title = "Run instantly with NPX",
                            command = "npx virgoyt-ai",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied to clipboard!")
                            },
                            isDark = isDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CliCommandCard(
                            title = "Global npm installation",
                            command = "npm install -g virgoyt-ai && virgoyt",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied to clipboard!")
                            },
                            isDark = isDark
                        )
                    }

                    2 -> {
                        // Python Tab
                        Text(
                            text = "🐍 Python Agent Harness",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Run the autonomous coding harness in Python on Termux, Linux or macOS:",
                            fontSize = 12.sp,
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CliCommandCard(
                            title = "Install & Run via Python",
                            command = "pkg install -y python git && curl -sSL https://raw.githubusercontent.com/darkvirgoyt-beep/VirgoYT-AI/main/cli/virgoyt.py -o virgoyt.py && python3 virgoyt.py",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied Python command to clipboard!")
                            },
                            isDark = isDark
                        )
                    }

                    3 -> {
                        // Fix Errors Tab (Specifically explaining openjdk-17)
                        Text(
                            text = "⚠️ Why 'openjdk-17' failed in Termux",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF59E0B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "1. Why it happened: Termux updated its repositories and package names. 'openjdk-17' was replaced by 'openjdk-21' or moved.\n" +
                                    "2. More importantly: You DO NOT need Java or OpenJDK to run an AI coding agent harness! Claude Code, DeepSeek harness, and VirgoYT run on Node.js or Python.\n\n" +
                            "Run this clean command instead:",
                            fontSize = 11.sp,
                            color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CliCommandCard(
                            title = "Fixed Clean Termux Command (No Java Required)",
                            command = "pkg update -y && pkg install -y nodejs git curl && curl -sSL https://raw.githubusercontent.com/darkvirgoyt-beep/VirgoYT-AI/main/cli/install.sh | bash",
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(it))
                                viewModel.showToast("Copied fixed command to clipboard!")
                            },
                            isDark = isDark
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "If you really need Java in Termux for other purposes, use 'pkg install -y openjdk-21' instead of openjdk-17.",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
fun CliCommandCard(
    title: String,
    command: String,
    onCopy: (String) -> Unit,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) Color(0xFF030712) else Color(0xFFF1F5F9),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1F2937) else Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF38BDF8)
                )
                IconButton(
                    onClick = { onCopy(command) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy command",
                        tint = Color(0xFF06B6D4),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = command,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = if (isDark) Color(0xFF34D399) else Color(0xFF059669),
                lineHeight = 15.sp
            )
        }
    }
}

