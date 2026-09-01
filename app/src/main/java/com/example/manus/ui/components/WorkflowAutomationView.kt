package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.WorkflowPipeline
import com.example.manus.data.model.WorkflowStep
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.VirgoCyanGlow
import com.example.ui.theme.VirgoGlassCard
import com.example.ui.theme.VirgoNeonViolet

enum class WorkflowSubTab {
    PIPELINES,
    RUN_LOGS
}

@Composable
fun WorkflowAutomationView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val workflowEngine = viewModel.workflowAutomationEngine
    val pipelines by workflowEngine.pipelines.collectAsState()
    val logs by workflowEngine.executionLogs.collectAsState()
    val isRunningId by workflowEngine.isRunningWorkflow.collectAsState()

    var subTab by remember { mutableStateOf(WorkflowSubTab.PIPELINES) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VirgoGlassCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Workflows",
                    tint = VirgoCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Autonomous Workflows & Pipelines",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${pipelines.size} Active Pipelines • Cron & Webhook Orchestration",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Sub Tab Pills
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                WorkflowSubTab.values().forEach { tab ->
                    val isSelected = subTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VirgoCyanGlow.copy(alpha = 0.2f) else ManusSlate850)
                            .border(1.dp, if (isSelected) VirgoCyanGlow else SleekBorder, RoundedCornerShape(8.dp))
                            .clickable { subTab = tab }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (tab == WorkflowSubTab.PIPELINES) "Pipelines (${pipelines.size})" else "Run Logs (${logs.size})",
                            color = if (isSelected) VirgoCyanGlow else ManusSlate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (subTab) {
            WorkflowSubTab.PIPELINES -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pipelines) { pipeline ->
                        val isRunning = isRunningId == pipeline.id
                        PipelineCard(
                            pipeline = pipeline,
                            isRunning = isRunning,
                            onToggle = { workflowEngine.togglePipelineActive(pipeline.id) },
                            onTriggerNow = {
                                workflowEngine.triggerWorkflowManually(pipeline.id) { msg ->
                                    viewModel.showToast(msg)
                                }
                            }
                        )
                    }
                }
            }
            WorkflowSubTab.RUN_LOGS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.workflowName,
                                        color = ManusWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "✓ SUCCESS in ${log.executionTimeMs}ms",
                                        color = ManusEmerald,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Trigger: ${log.triggerSource}",
                                    color = ManusSlate400,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ManusSlate950)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = log.logOutput,
                                        color = VirgoCyanGlow,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 14.sp
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
private fun PipelineCard(
    pipeline: WorkflowPipeline,
    isRunning: Boolean,
    onToggle: () -> Unit,
    onTriggerNow: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SleekBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ManusSlate900)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = pipeline.triggerType.iconEmoji, fontSize = 16.sp)
                    Column {
                        Text(
                            text = pipeline.name,
                            color = ManusWhite,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Trigger: ${pipeline.triggerType.label} (${pipeline.cronSchedule})",
                            color = VirgoCyanGlow,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Switch(
                        checked = pipeline.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ManusWhite,
                            checkedTrackColor = ManusEmerald,
                            uncheckedTrackColor = ManusSlate800
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pipeline.description,
                color = ManusSlate300,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Step Pipeline Graph Nodes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ManusSlate950)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "EXECUTION PIPELINE GRAPH",
                    color = ManusSlate500,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                pipeline.steps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "[${index + 1}]",
                            color = VirgoNeonViolet,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(text = step.actionType.iconEmoji, fontSize = 12.sp)
                        Text(
                            text = step.title,
                            color = ManusWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "(${step.configPayload})",
                            color = ManusSlate500,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Action: Trigger Now & Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Runs: ${pipeline.totalRuns} • Last: Completed",
                    color = ManusSlate400,
                    fontSize = 10.sp
                )

                Button(
                    onClick = onTriggerNow,
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) ManusSlate800 else ManusIndigo),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp).testTag("trigger_workflow_${pipeline.id}")
                ) {
                    if (isRunning) {
                        CircularProgressIndicator(color = VirgoCyanGlow, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Executing...", color = ManusSlate300, fontSize = 10.5.sp)
                    } else {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", tint = ManusWhite, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run Now", color = ManusWhite, fontSize = 10.5.sp)
                    }
                }
            }
        }
    }
}
