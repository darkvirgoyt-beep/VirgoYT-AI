package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.AppThemeMode
import com.example.manus.data.model.SystemStats
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.EngineerLightBorder
import com.example.ui.theme.EngineerLightBorderStrong
import com.example.ui.theme.EngineerLightCanvas
import com.example.ui.theme.EngineerLightCard
import com.example.ui.theme.EngineerLightCyan
import com.example.ui.theme.EngineerLightEmerald
import com.example.ui.theme.EngineerLightPrimary
import com.example.ui.theme.EngineerLightPrimaryBg
import com.example.ui.theme.EngineerLightPrimaryDark
import com.example.ui.theme.EngineerLightSubtle
import com.example.ui.theme.EngineerLightSurface
import com.example.ui.theme.EngineerLightTextMuted
import com.example.ui.theme.EngineerLightTextPrimary
import com.example.ui.theme.EngineerLightTextSecondary
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoDark
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusIndigoSoft
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
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

@Composable
fun CloudPcHeader(
    viewModel: ManusCloudViewModel,
    activeTab: ActiveWorkspaceTab,
    systemStats: SystemStats,
    isAgentBusy: Boolean,
    modifier: Modifier = Modifier
) {
    val session by viewModel.currentSession.collectAsState()
    val currentUser = session?.user
    val selectedModel by viewModel.modelRouterEngine.selectedModel.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isDark = themeMode.isDark

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val headerSurfaceColor = if (isDark) ManusSlate900 else EngineerLightSurface
    val headerBorderColor = if (isDark) SleekBorder else EngineerLightBorder
    val primaryTextColor = if (isDark) ManusWhite else EngineerLightTextPrimary
    val secondaryPillBg = if (isDark) SleekSurface else EngineerLightSubtle
    val secondaryPillBorder = if (isDark) SleekBorder else EngineerLightBorder
    val secondaryPillText = if (isDark) ManusSlate300 else EngineerLightTextSecondary

    Surface(
        color = headerSurfaceColor,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, headerBorderColor)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Sleek Interface Top Bar: Brand Badge, Status Pill, Live Metrics, Theme Mode Switch, User Auth Badge, Reset Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand: Square Badge "V" + VirgoYT AI Title + Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) ManusIndigo else EngineerLightPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "VirgoYT Cloud AI",
                                color = primaryTextColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(secondaryPillBg)
                                    .border(0.5.dp, secondaryPillBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = "Cloud node",
                                        tint = if (isDark) ManusCyan else EngineerLightCyan,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "v-cloud-titan",
                                        color = if (isDark) ManusCyan else EngineerLightCyan,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isAgentBusy) ManusAmber else if (isDark) ManusEmerald.copy(alpha = pulseAlpha) else EngineerLightEmerald)
                            )
                            Text(
                                text = if (isAgentBusy) "AI EXECUTING" else "CLOUD ACTIVE",
                                color = if (isAgentBusy) ManusAmber else if (isDark) ManusEmerald.copy(alpha = 0.9f) else EngineerLightEmerald,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isDark) ManusIndigoBg else EngineerLightPrimaryBg)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = selectedModel.iconEmoji,
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = selectedModel.displayName.take(14),
                                        color = if (isDark) ManusIndigoLight else EngineerLightPrimaryDark,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Center/Right: Light/Dark Theme Switch + Secret Vault + GitHub Sync + CPU/GPU Usage & Reset Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isGitHubConnected by viewModel.isGitHubConnected.collectAsState()
                    val gitHubUser by viewModel.gitHubUser.collectAsState()

                    // =========================================================================
                    // Light / Dark Mode Theme Toggle Switch (Holo Dark <-> Engineer Mode Light)
                    // =========================================================================
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDark) ManusIndigoBg else EngineerLightPrimaryBg
                            )
                            .border(
                                1.dp,
                                if (isDark) ManusIndigo.copy(alpha = 0.7f) else EngineerLightPrimary,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { viewModel.toggleThemeMode() }
                            .padding(horizontal = 7.dp, vertical = 5.dp)
                            .testTag("theme_mode_toggle_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Toggle Light/Dark Theme Mode",
                                tint = if (isDark) ManusCyan else EngineerLightPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isDark) "🌙 Holo Dark" else "☀️ Engineer",
                                color = if (isDark) ManusWhite else EngineerLightPrimaryDark,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // User Auth Quick Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentUser != null) (if (isDark) ManusIndigoBg else EngineerLightPrimaryBg) else secondaryPillBg)
                            .border(1.dp, if (currentUser != null) (if (isDark) ManusIndigo else EngineerLightPrimary) else secondaryPillBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openUserProfileDialog() }
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .testTag("user_auth_header_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Profile",
                                tint = if (currentUser != null) (if (isDark) ManusIndigoLight else EngineerLightPrimary) else (if (isDark) ManusSlate400 else EngineerLightTextMuted),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = currentUser?.username ?: "Profile",
                                color = if (currentUser != null) (if (isDark) ManusWhite else EngineerLightPrimaryDark) else secondaryPillText,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Secret Credential Vault Quick Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(secondaryPillBg)
                            .border(1.dp, secondaryPillBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.openSecretBox("GitHub / Gmail / Epic Games", "Securely enter auth credentials or take remote desktop control")
                            }
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .testTag("secret_vault_header_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Secret Vault",
                                tint = if (isDark) ManusCyan else EngineerLightCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Vault",
                                color = secondaryPillText,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // GitHub Integration Quick Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isGitHubConnected) (if (isDark) ManusIndigoBg else EngineerLightPrimaryBg) else secondaryPillBg)
                            .border(1.dp, if (isGitHubConnected) (if (isDark) ManusEmerald.copy(alpha = 0.5f) else EngineerLightEmerald) else secondaryPillBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openGitHubAuthDialog() }
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .testTag("github_header_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub Integration",
                                tint = if (isGitHubConnected) (if (isDark) ManusEmerald else EngineerLightEmerald) else (if (isDark) ManusCyan else EngineerLightCyan),
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isGitHubConnected) "@${gitHubUser?.username ?: "gh"}" else "GitHub",
                                color = if (isGitHubConnected) (if (isDark) ManusEmerald else EngineerLightEmerald) else secondaryPillText,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Hardware Telemetry
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "GPU ${systemStats.gpuUsagePercent.toInt()}%",
                            color = if (isDark) ManusCyan else EngineerLightCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "CPU ${systemStats.cpuUsagePercent.toInt()}%",
                            color = if (isDark) ManusIndigoLight else EngineerLightPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = { viewModel.resetCloudPcSnapshot() },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(secondaryPillBg)
                            .border(1.dp, secondaryPillBorder, CircleShape)
                            .testTag("reset_snapshot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Snapshot",
                            tint = if (isDark) ManusSlate200 else EngineerLightTextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Tabs (Horizontal Scrollable with Sleek Styling)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TabButton(
                    tab = ActiveWorkspaceTab.AGENT,
                    selected = activeTab == ActiveWorkspaceTab.AGENT,
                    icon = Icons.Default.AutoAwesome,
                    badge = if (isAgentBusy) "SWARM" else "15 AGENTS",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.AGENT) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.VOICE_ASSISTANT,
                    selected = activeTab == ActiveWorkspaceTab.VOICE_ASSISTANT,
                    icon = Icons.Default.Security,
                    badge = "VOICE",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.VOICE_ASSISTANT) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.WEB_DASHBOARD,
                    selected = activeTab == ActiveWorkspaceTab.WEB_DASHBOARD,
                    icon = Icons.Default.Public,
                    badge = "REACT",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.WEB_DASHBOARD) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.APP_GEN,
                    selected = activeTab == ActiveWorkspaceTab.APP_GEN,
                    icon = Icons.Default.RocketLaunch,
                    badge = "NEW",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.APP_GEN) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.PLUGINS_TOOLS,
                    selected = activeTab == ActiveWorkspaceTab.PLUGINS_TOOLS,
                    icon = Icons.Default.Hub,
                    badge = "TOOLS",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.PLUGINS_TOOLS) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.WORKFLOWS,
                    selected = activeTab == ActiveWorkspaceTab.WORKFLOWS,
                    icon = Icons.Default.Refresh,
                    badge = "CRON",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.WORKFLOWS) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.PROJECT_SCAN,
                    selected = activeTab == ActiveWorkspaceTab.PROJECT_SCAN,
                    icon = Icons.Default.Hub,
                    badge = "AST",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.PROJECT_SCAN) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.DATABASE_AI,
                    selected = activeTab == ActiveWorkspaceTab.DATABASE_AI,
                    icon = Icons.Default.Storage,
                    badge = "SQL",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.DATABASE_AI) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.MEMORY_RAG,
                    selected = activeTab == ActiveWorkspaceTab.MEMORY_RAG,
                    icon = Icons.Default.Psychology,
                    badge = "1536D",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.MEMORY_RAG) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.CLOUD_STORAGE,
                    selected = activeTab == ActiveWorkspaceTab.CLOUD_STORAGE,
                    icon = Icons.Default.Cloud,
                    badge = "S3",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.CLOUD_STORAGE) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.LIVE_COMPUTER,
                    selected = activeTab == ActiveWorkspaceTab.LIVE_COMPUTER,
                    icon = Icons.Default.Computer,
                    badge = "DIFF",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.LIVE_COMPUTER) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.GAME_STUDIO,
                    selected = activeTab == ActiveWorkspaceTab.GAME_STUDIO,
                    icon = Icons.Default.VideogameAsset,
                    badge = "UE5",
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.GAME_STUDIO) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.EDITOR,
                    selected = activeTab == ActiveWorkspaceTab.EDITOR,
                    icon = Icons.Default.Code,
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.EDITOR) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.TERMINAL,
                    selected = activeTab == ActiveWorkspaceTab.TERMINAL,
                    icon = Icons.Default.Terminal,
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.TERMINAL) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.FILES,
                    selected = activeTab == ActiveWorkspaceTab.FILES,
                    icon = Icons.Default.Folder,
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.FILES) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.BROWSER,
                    selected = activeTab == ActiveWorkspaceTab.BROWSER,
                    icon = Icons.Default.Public,
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.BROWSER) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.MONITOR,
                    selected = activeTab == ActiveWorkspaceTab.MONITOR,
                    icon = Icons.Default.Memory,
                    isDark = isDark,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.MONITOR) }
                )
            }
        }
    }
}

@Composable
private fun TabButton(
    tab: ActiveWorkspaceTab,
    selected: Boolean,
    icon: ImageVector,
    badge: String? = null,
    isDark: Boolean = true,
    onClick: () -> Unit
) {
    val bg = if (selected) {
        if (isDark) ManusIndigoBg else EngineerLightPrimaryBg
    } else {
        if (isDark) SleekSurface else EngineerLightSubtle.copy(alpha = 0.5f)
    }

    val borderColor = if (selected) {
        if (isDark) ManusIndigo.copy(alpha = 0.5f) else EngineerLightPrimary
    } else {
        if (isDark) Color.Transparent else EngineerLightBorder.copy(alpha = 0.6f)
    }

    val contentColor = if (selected) {
        if (isDark) ManusIndigoSoft else EngineerLightPrimary
    } else {
        if (isDark) ManusSlate400 else EngineerLightTextMuted
    }

    val textColor = if (selected) {
        if (isDark) ManusWhite else EngineerLightPrimaryDark
    } else {
        if (isDark) ManusSlate400 else EngineerLightTextSecondary
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 6.dp)
            .testTag("tab_${tab.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tab.label,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = tab.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (badge == "UE5") ManusPurple else if (badge == "DIFF") (if (isDark) ManusCyan else EngineerLightCyan) else ManusAmber)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = if (isDark) ManusSlate950 else Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}


