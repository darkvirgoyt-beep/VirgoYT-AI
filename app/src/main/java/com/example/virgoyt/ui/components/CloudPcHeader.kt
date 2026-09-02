package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.theme.VirgoTheme
import com.example.virgoyt.data.model.ActiveWorkspaceTab
import com.example.virgoyt.data.model.SystemStats
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun CloudPcHeader(
    activeTab: ActiveWorkspaceTab,
    systemStats: SystemStats,
    isAgentBusy: Boolean,
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val scrollState = rememberScrollState()
    val colors = VirgoTheme.colors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(1.dp, colors.border)
            .padding(vertical = 8.dp)
    ) {
        // Top row: Brand & Status & Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.accentCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VirgoYT Cloud AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isAgentBusy) colors.accentAmber else colors.accentEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAgentBusy) "Swarm Busy..." else "Cloud VM Active (6.8.0-cloud)",
                            fontSize = 11.sp,
                            color = colors.textMuted
                        )
                    }
                }
            }

            // Action Icons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { viewModel.preferenceEngine.toggleTheme() },
                    modifier = Modifier.testTag("theme_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (themeMode.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Theme",
                        tint = colors.accentCyan
                    )
                }
                IconButton(
                    onClick = { viewModel.openGitHubAuthDialog() },
                    modifier = Modifier.testTag("github_auth_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "GitHub",
                        tint = colors.textPrimary
                    )
                }
                IconButton(
                    onClick = { viewModel.openUserProfileDialog() },
                    modifier = Modifier.testTag("user_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = colors.textPrimary
                    )
                }
            }
        }

        // Horizontal Tabs Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ActiveWorkspaceTab.entries.forEach { tab ->
                val isSelected = tab == activeTab
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) {
                        colors.accentCyan.copy(alpha = 0.2f)
                    } else {
                        colors.cardSubtle
                    },
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(1.dp, colors.accentCyan)
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.selectTab(tab) }
                        .testTag("tab_${tab.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tab.iconEmoji, fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                colors.accentCyan
                            } else {
                                colors.textSecondary
                            }
                        )
                    }
                }
            }
        }
    }
}
