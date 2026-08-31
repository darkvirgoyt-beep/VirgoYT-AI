package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ProcessInfo
import com.example.manus.data.model.SystemStats
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
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

@Composable
fun SystemMonitorView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val stats by viewModel.systemStats.collectAsState()
    val processes by viewModel.processes.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Hardware Resource Utilization Cards in Sleek Obsidian styling
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // CPU Meter Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "VIRTUAL CPU LOAD",
                        color = ManusSlate500,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${stats.cpuUsagePercent.toInt()}%",
                        color = if (stats.cpuUsagePercent > 60f) ManusAmber else ManusIndigoLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    LinearProgressIndicator(
                        progress = { stats.cpuUsagePercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = if (stats.cpuUsagePercent > 60f) ManusAmber else ManusIndigo,
                        trackColor = ManusSlate800
                    )
                    Text(
                        text = "4 Cores (AMD EPYC™ 9754)",
                        color = ManusSlate500,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // RAM Meter Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "MEMORY ALLOCATION",
                        color = ManusSlate500,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "${stats.memoryUsedMb} MB",
                        color = ManusIndigoSoft,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    LinearProgressIndicator(
                        progress = { stats.memoryUsedMb.toFloat() / stats.memoryTotalMb },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = ManusIndigoLight,
                        trackColor = ManusSlate800
                    )
                    Text(
                        text = "Total: 8,192 MB (6.2 GB free)",
                        color = ManusSlate500,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Environment & Isolation Details Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "SANDBOX TELEMETRY",
                    color = ManusIndigoLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryItem("Sandbox Cluster", "asia-east1 / node-4")
                    TelemetryItem("Uptime", "${stats.uptimeSeconds / 60}m ${stats.uptimeSeconds % 60}s")
                    TelemetryItem("Network Ping", "14.2 ms")
                    TelemetryItem("Kernel", "Linux 6.8 LTS")
                }
            }
        }

        // Active Processes Table in Sleek styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE PROCESSES (${processes.size})",
                        color = ManusSlate400,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ManusSlate950)
                        .padding(horizontal = 6.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("PID", color = ManusSlate500, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.6f))
                    Text("USER", color = ManusSlate500, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.8f))
                    Text("CPU%", color = ManusSlate500, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.6f))
                    Text("COMMAND", color = ManusSlate500, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(2f))
                    Text("STATUS", color = ManusSlate500, fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(processes) { proc ->
                        ProcessRow(proc = proc)
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryItem(label: String, value: String) {
    Column {
        Text(text = label, color = ManusSlate500, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = ManusWhite, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ProcessRow(proc: ProcessInfo) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${proc.pid}",
            color = ManusIndigoLight,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = proc.user,
            color = ManusSlate400,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.8f)
        )
        Text(
            text = "${proc.cpu}%",
            color = if (proc.cpu > 5f) ManusAmber else ManusSlate400,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(0.6f)
        )
        Text(
            text = proc.command,
            color = ManusWhite,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(2f)
        )
        Text(
            text = proc.status,
            color = ManusEmerald,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.weight(1f)
        )
    }
}
