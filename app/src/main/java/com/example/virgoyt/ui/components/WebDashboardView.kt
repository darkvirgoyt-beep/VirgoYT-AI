package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
