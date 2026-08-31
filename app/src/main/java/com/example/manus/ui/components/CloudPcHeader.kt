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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
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
import com.example.manus.data.model.SystemStats
import com.example.manus.ui.ManusCloudViewModel
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

    Surface(
        color = ManusSlate900,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, SleekBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            // Sleek Interface Top Bar: Brand Badge, Status Pill, Live Metrics, User Auth Badge, Reset Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Brand: Purple-Indigo Square Badge "V" + VirgoYT AI Title + Emerald Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManusIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "V",
                            color = ManusWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "VirgoYT AI",
                                color = ManusWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SleekSurface)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Cloud,
                                        contentDescription = "Cloud node",
                                        tint = ManusCyan,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Text(
                                        text = "v-cloud-titan",
                                        color = ManusCyan,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isAgentBusy) ManusAmber else ManusEmerald.copy(alpha = pulseAlpha))
                            )
                            Text(
                                text = if (isAgentBusy) "AI EXECUTING" else "CLOUD ACTIVE",
                                color = if (isAgentBusy) ManusAmber else ManusEmerald.copy(alpha = 0.9f),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ManusIndigoBg)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = selectedModel.iconEmoji,
                                        fontSize = 8.sp
                                    )
                                    Text(
                                        text = selectedModel.displayName.take(14),
                                        color = ManusIndigoLight,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                // Center/Right: Secret Box + GitHub Sync + CPU/GPU Usage & Reset Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isGitHubConnected by viewModel.isGitHubConnected.collectAsState()
                    val gitHubUser by viewModel.gitHubUser.collectAsState()

                    // User Auth Quick Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (currentUser != null) ManusIndigoBg else SleekSurface)
                            .border(1.dp, if (currentUser != null) ManusIndigo else SleekBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openAuthDialog() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("user_auth_header_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Auth",
                                tint = if (currentUser != null) ManusIndigoLight else ManusSlate400,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = currentUser?.username ?: "Login",
                                color = if (currentUser != null) ManusWhite else ManusSlate300,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Secret Credential Vault Quick Trigger
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SleekSurface)
                            .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.openSecretBox("GitHub / Gmail / Epic Games", "Securely enter auth credentials or take remote desktop control")
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("secret_vault_header_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Secret Vault",
                                tint = ManusCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Vault",
                                color = ManusSlate300,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // GitHub Integration Quick Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isGitHubConnected) ManusIndigoBg else SleekSurface)
                            .border(1.dp, if (isGitHubConnected) ManusEmerald.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.openGitHubAuthDialog() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("github_header_badge")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub Integration",
                                tint = if (isGitHubConnected) ManusEmerald else ManusCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isGitHubConnected) "@${gitHubUser?.username ?: "gh"}" else "GitHub",
                                color = if (isGitHubConnected) ManusEmerald else ManusSlate300,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Hardware Telemetry
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "GPU ${systemStats.gpuUsagePercent.toInt()}%",
                            color = ManusCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "CPU ${systemStats.cpuUsagePercent.toInt()}%",
                            color = ManusIndigoLight,
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
                            .background(SleekSurface)
                            .border(1.dp, SleekBorder, CircleShape)
                            .testTag("reset_snapshot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Snapshot",
                            tint = ManusSlate200,
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
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.AGENT) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.APP_GEN,
                    selected = activeTab == ActiveWorkspaceTab.APP_GEN,
                    icon = Icons.Default.RocketLaunch,
                    badge = "NEW",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.APP_GEN) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.PROJECT_SCAN,
                    selected = activeTab == ActiveWorkspaceTab.PROJECT_SCAN,
                    icon = Icons.Default.Hub,
                    badge = "AST",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.PROJECT_SCAN) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.DATABASE_AI,
                    selected = activeTab == ActiveWorkspaceTab.DATABASE_AI,
                    icon = Icons.Default.Storage,
                    badge = "SQL",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.DATABASE_AI) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.MEMORY_RAG,
                    selected = activeTab == ActiveWorkspaceTab.MEMORY_RAG,
                    icon = Icons.Default.Psychology,
                    badge = "1536D",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.MEMORY_RAG) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.LIVE_COMPUTER,
                    selected = activeTab == ActiveWorkspaceTab.LIVE_COMPUTER,
                    icon = Icons.Default.Computer,
                    badge = "DIFF",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.LIVE_COMPUTER) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.GAME_STUDIO,
                    selected = activeTab == ActiveWorkspaceTab.GAME_STUDIO,
                    icon = Icons.Default.VideogameAsset,
                    badge = "UE5",
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.GAME_STUDIO) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.EDITOR,
                    selected = activeTab == ActiveWorkspaceTab.EDITOR,
                    icon = Icons.Default.Code,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.EDITOR) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.TERMINAL,
                    selected = activeTab == ActiveWorkspaceTab.TERMINAL,
                    icon = Icons.Default.Terminal,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.TERMINAL) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.FILES,
                    selected = activeTab == ActiveWorkspaceTab.FILES,
                    icon = Icons.Default.Folder,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.FILES) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.BROWSER,
                    selected = activeTab == ActiveWorkspaceTab.BROWSER,
                    icon = Icons.Default.Public,
                    onClick = { viewModel.selectTab(ActiveWorkspaceTab.BROWSER) }
                )
                TabButton(
                    tab = ActiveWorkspaceTab.MONITOR,
                    selected = activeTab == ActiveWorkspaceTab.MONITOR,
                    icon = Icons.Default.Memory,
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
    onClick: () -> Unit
) {
    val bg = if (selected) ManusIndigoBg else SleekSurface
    val borderColor = if (selected) ManusIndigo.copy(alpha = 0.4f) else Color.Transparent
    val contentColor = if (selected) ManusIndigoSoft else ManusSlate400

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
                color = if (selected) ManusWhite else ManusSlate400,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                fontFamily = FontFamily.SansSerif
            )
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (badge == "UE5") ManusPurple else if (badge == "DIFF") ManusCyan else ManusAmber)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge,
                        color = ManusSlate950,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

