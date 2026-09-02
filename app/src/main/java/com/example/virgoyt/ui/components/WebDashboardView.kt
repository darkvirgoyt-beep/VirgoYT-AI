package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun WebDashboardView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val stats by viewModel.systemStats.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("web_dashboard_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🌐 Cloud Platform Dashboard",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "CPU Utilization",
                    value = "${stats.cpuUsagePercent}%",
                    subtitle = "8 Cores Active",
                    color = Color(0xFF06B6D4),
                    isDark = themeMode.isDark,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "RAM Memory",
                    value = "${stats.memoryUsedMb} MB",
                    subtitle = "/ ${stats.memoryTotalMb} MB",
                    color = Color(0xFF10B981),
                    isDark = themeMode.isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Disk Sandbox",
                    value = "${stats.diskUsedGb} GB",
                    subtitle = "/ ${stats.diskTotalGb} GB",
                    color = Color(0xFF8B5CF6),
                    isDark = themeMode.isDark,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Live Swarm",
                    value = "${stats.activeBackgroundAgents} Agents",
                    subtitle = "${stats.activeThreads} Threads",
                    color = Color(0xFFF59E0B),
                    isDark = themeMode.isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📦 Multi-Platform Downloads & GitHub Host",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
            Text(
                text = "Same live build mirrored on GitHub Pages, Android APK, Windows, and Mac.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        item {
            val uriHandler = LocalUriHandler.current
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DownloadRowCard(
                    title = "Live Web App (GitHub Pages)",
                    subtitle = "darkvirgoyt-beep.github.io/VirgoYT-AI",
                    icon = Icons.Default.Public,
                    accentColor = Color(0xFF06B6D4),
                    actionText = "Open Web Host",
                    isDark = themeMode.isDark,
                    onAction = {
                        uriHandler.openUri("https://darkvirgoyt-beep.github.io/VirgoYT-AI/")
                    }
                )
                DownloadRowCard(
                    title = "Android APK (Release & Debug)",
                    subtitle = "Latest ARM64 / x86 release builds",
                    icon = Icons.Default.Android,
                    accentColor = Color(0xFF10B981),
                    actionText = "Download APK",
                    isDark = themeMode.isDark,
                    onAction = {
                        uriHandler.openUri("https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest")
                    }
                )
                DownloadRowCard(
                    title = "Windows Desktop App (.exe)",
                    subtitle = "Windows 10 / 11 64-bit installer & portable",
                    icon = Icons.Default.Laptop,
                    accentColor = Color(0xFF38BDF8),
                    actionText = "Download EXE",
                    isDark = themeMode.isDark,
                    onAction = {
                        uriHandler.openUri("https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest")
                    }
                )
                DownloadRowCard(
                    title = "macOS Bundle (.dmg / .app)",
                    subtitle = "Apple Silicon M-Series & Intel Mac build",
                    icon = Icons.Default.Computer,
                    accentColor = Color(0xFFA855F7),
                    actionText = "Download DMG",
                    isDark = themeMode.isDark,
                    onAction = {
                        uriHandler.openUri("https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest")
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "⚡ Terminal & Termux Download Commands",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        item {
            val clipboard = LocalClipboardManager.current
            var selectedTab by remember { mutableStateOf(0) }
            val tabs = listOf("Termux", "Linux", "macOS", "cURL")
            val commands = listOf(
                "pkg update -y && pkg install -y git openjdk-17 curl && curl -sL https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk -o VirgoYT-AI.apk && termux-open VirgoYT-AI.apk",
                "wget -O virgo-yt-ai.apk https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk",
                "curl -sLO https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk && open .",
                "curl -L -O https://github.com/darkvirgoyt-beep/VirgoYT-AI/releases/latest/download/app-release.apk"
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (themeMode.isDark) Color(0xFF090D16) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tabs.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) Color(0xFF06B6D4).copy(alpha = 0.2f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4)) else null,
                                modifier = Modifier
                                    .clickable { selectedTab = index }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF06B6D4) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF020617),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = commands[selectedTab],
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(commands[selectedTab]))
                                    viewModel.showToast("Command copied to clipboard!")
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy command",
                                    tint = Color(0xFF06B6D4),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Production UI & Full-Stack Website Templates Section
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "💻 Production Web & UI Templates",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
            Text(
                text = "Instant 1-click starter templates ready for deployment on Vercel, Docker & Cloud Run.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        item {
            val webTemplates = listOf(
                Triple("Next.js 15 App Router + Tailwind v4 SaaS", "Full-stack dashboard with TypeScript, Supabase Auth & Stripe billing", Color(0xFF06B6D4)),
                Triple("FastAPI + React 19 Modern Web App", "High-performance async Python backend with real-time WebSocket state", Color(0xFF10B981)),
                Triple("Cyberpunk Three.js WebGL Landing", "Interactive 3D particle landscape, glow shaders and audio visualizer", Color(0xFFA855F7))
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                webTemplates.forEach { (tplTitle, tplDesc, tplColor) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(tplTitle, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = tplColor)
                                Text(tplDesc, fontSize = 11.sp, color = Color(0xFF94A3B8))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    viewModel.executePrompt("Scaffold $tplTitle and write full code to workspace")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = tplColor),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Scaffold", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadRowCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    actionText: String,
    isDark: Boolean,
    onAction: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color(0xFF0F172A))
                    Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(actionText, fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF64748B))
        }
    }
}
