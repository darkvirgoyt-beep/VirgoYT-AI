package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.AgentSubtask
import com.example.manus.data.model.AgentTask
import com.example.manus.data.model.TaskStatus
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
fun ManusAgentView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val currentTask by viewModel.agentService.currentTask.collectAsState()
    val isBusy by viewModel.agentService.isAgentBusy.collectAsState()
    val statusText by viewModel.agentService.agentStatusText.collectAsState()
    val reasoningLogs by viewModel.agentService.agentReasoningLogs.collectAsState()
    val agentInputGoal by viewModel.agentInputGoal.collectAsState()

    var showReasoningLogs by remember { mutableStateOf(true) }
    var activeMode by remember { mutableStateOf("SWARM") } // SWARM, GOAL_RUNNER

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
    ) {
        // Mode Selector Bar (Swarm vs Goal Runner)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeMode == "SWARM") ManusIndigoBg else ManusSlate900)
                    .border(1.dp, if (activeMode == "SWARM") ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                    .clickable { activeMode = "SWARM" }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🐝 15-Agent Autonomous Hive",
                    color = if (activeMode == "SWARM") ManusCyan else ManusSlate400,
                    fontSize = 11.5.sp,
                    fontWeight = if (activeMode == "SWARM") FontWeight.Bold else FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeMode == "GOAL_RUNNER") ManusIndigoBg else ManusSlate900)
                    .border(1.dp, if (activeMode == "GOAL_RUNNER") ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                    .clickable { activeMode = "GOAL_RUNNER" }
                    .padding(vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⚡ Lead AI Goal Execution",
                    color = if (activeMode == "GOAL_RUNNER") ManusCyan else ManusSlate400,
                    fontSize = 11.5.sp,
                    fontWeight = if (activeMode == "GOAL_RUNNER") FontWeight.Bold else FontWeight.Medium
                )
            }
        }

        if (activeMode == "SWARM") {
            MultiAgentTeamSection(viewModel = viewModel)
        }

        // Sleek Interface Goal Input Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ManusIndigoBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ManusIndigoLight,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Text(
                        text = "MANUS AUTONOMOUS REASONING CORE",
                        color = ManusWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = agentInputGoal,
                        onValueChange = { viewModel.setAgentInputGoal(it) },
                        placeholder = {
                            Text(
                                "Instruct agent to build an app, compile code, or solve tasks...",
                                color = ManusSlate500,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("agent_goal_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManusIndigo,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite,
                            focusedContainerColor = ManusSlate850,
                            unfocusedContainerColor = ManusSlate850
                        ),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = false,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (!isBusy && agentInputGoal.isNotBlank()) {
                                viewModel.runAgentGoal()
                            }
                        })
                    )

                    Button(
                        onClick = { viewModel.runAgentGoal() },
                        enabled = !isBusy && agentInputGoal.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ManusIndigo,
                            disabledContainerColor = SleekSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        modifier = Modifier.testTag("agent_run_button")
                    ) {
                        if (isBusy) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ManusWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Run Goal",
                                tint = ManusWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Preset Goals chips in Sleek styling
                Text(
                    text = "Suggested Goals:",
                    color = ManusSlate500,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        PresetChip(
                            icon = Icons.Default.SportsEsports,
                            label = "Cyber Snake Game",
                            onClick = { viewModel.runAgentGoal("Build an interactive 2D Cyberpunk Snake game with HTML5 canvas, scoreboard, and touch controls.") }
                        )
                    }
                    item {
                        PresetChip(
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            label = "Crypto Quant Dashboard",
                            onClick = { viewModel.runAgentGoal("Build a Crypto market quant tracker with live tickers, order flow simulation, and Chart.js charts.") }
                        )
                    }
                    item {
                        PresetChip(
                            icon = Icons.Default.DataObject,
                            label = "Python Data Science",
                            onClick = { viewModel.runAgentGoal("Run Python regression analysis on server metrics CSV, generate report, and output statistics.") }
                        )
                    }
                    item {
                        PresetChip(
                            icon = Icons.Default.Code,
                            label = "Multi-Runtime Benchmark",
                            onClick = { viewModel.runAgentGoal("Compile C sorting algorithms with GCC, run Node prime benchmark, and test system uptime.") }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status bar & Toggle Reasoning
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isBusy) ManusAmber else ManusEmerald)
                )
                Text(
                    text = statusText,
                    color = if (isBusy) ManusAmber else ManusEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            if (reasoningLogs.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SleekSurface)
                        .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                        .clickable { showReasoningLogs = !showReasoningLogs }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Reasoning Logs (${reasoningLogs.size})",
                        color = ManusSlate400,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Icon(
                        imageVector = if (showReasoningLogs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = ManusSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Expandable Reasoning Log Block (Sleek Obsidian Card)
        AnimatedVisibility(
            visible = showReasoningLogs && reasoningLogs.isNotEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    items(reasoningLogs) { log ->
                        Text(
                            text = log,
                            color = when {
                                log.startsWith("🧠") || log.startsWith("💭") -> ManusIndigoSoft
                                log.startsWith("⚡") -> ManusAmber
                                log.startsWith("✓") || log.startsWith("🎉") -> ManusGreen
                                log.startsWith("⚠️") -> ManusRed
                                else -> ManusSlate200
                            },
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Structured Tasks List
        if (currentTask != null) {
            TaskPlanDisplay(
                task = currentTask!!,
                onOpenBrowser = { viewModel.selectTab(ActiveWorkspaceTab.BROWSER) },
                onOpenTerminal = { viewModel.selectTab(ActiveWorkspaceTab.TERMINAL) }
            )
        } else {
            // Empty State Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ManusIndigo.copy(alpha = 0.15f))
                            .border(1.dp, ManusIndigo.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = ManusIndigoLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Text(
                        text = "Autonomous Virtual PC Ready",
                        color = ManusWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Select a preset or describe any task. Manus will autonomously inspect workspace, compile code, execute scripts, and verify live preview.",
                        color = ManusSlate400,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetChip(icon: ImageVector, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SleekSurface)
            .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ManusIndigoLight,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                color = ManusSlate200,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TaskPlanDisplay(
    task: AgentTask,
    onOpenBrowser: () -> Unit,
    onOpenTerminal: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Goal header & explanation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PLAN: ${task.subtasks.count { it.status == TaskStatus.COMPLETED }}/${task.subtasks.size} COMPLETED",
                        color = if (task.status == TaskStatus.COMPLETED) ManusGreen else ManusIndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    StatusBadge(status = task.status)
                }
                Text(
                    text = task.explanation,
                    color = ManusSlate200,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (task.status == TaskStatus.COMPLETED) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenBrowser,
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = ManusWhite, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Open Live Sandbox Preview", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = onOpenTerminal,
                            colors = ButtonDefaults.buttonColors(containerColor = SleekSurface),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = ManusSlate200, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Terminal Logs", color = ManusSlate200, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Subtasks LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(task.subtasks) { subtask ->
                SubtaskCard(subtask = subtask)
            }
        }
    }
}

@Composable
private fun SubtaskCard(subtask: AgentSubtask) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ManusSlate900)
            .border(
                1.dp,
                when (subtask.status) {
                    TaskStatus.IN_PROGRESS -> ManusAmber
                    TaskStatus.COMPLETED -> ManusEmerald.copy(alpha = 0.5f)
                    TaskStatus.FAILED -> ManusRed
                    TaskStatus.PENDING -> SleekBorder
                },
                RoundedCornerShape(8.dp)
            )
            .clickable { expanded = !expanded }
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    when (subtask.status) {
                        TaskStatus.COMPLETED -> Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = ManusEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        TaskStatus.IN_PROGRESS -> CircularProgressIndicator(
                            color = ManusAmber,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                        TaskStatus.FAILED -> Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Failed",
                            tint = ManusRed,
                            modifier = Modifier.size(16.dp)
                        )
                        TaskStatus.PENDING -> Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = "Pending",
                            tint = ManusSlate600,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = subtask.title,
                        color = if (subtask.status == TaskStatus.PENDING) ManusSlate400 else ManusWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (subtask.toolName != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusIndigoBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = subtask.toolName,
                                color = ManusIndigoLight,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = ManusSlate400,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (!subtask.thought.isNullOrBlank()) {
                Text(
                    text = "💭 ${subtask.thought}",
                    color = ManusIndigoSoft,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = if (expanded) Int.MAX_VALUE else 1
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ManusSlate950)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    if (!subtask.toolInput.isNullOrBlank()) {
                        Text(
                            text = "INPUT: ${subtask.toolInput.take(120)}",
                            color = ManusSlate400,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    if (!subtask.toolOutput.isNullOrBlank()) {
                        Text(
                            text = "OUTPUT: ${subtask.toolOutput}",
                            color = ManusGreen,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    val (bg, text, color) = when (status) {
        TaskStatus.PENDING -> Triple(SleekSurface, "PENDING", ManusSlate400)
        TaskStatus.IN_PROGRESS -> Triple(ManusAmber.copy(alpha = 0.15f), "IN PROGRESS", ManusAmber)
        TaskStatus.COMPLETED -> Triple(ManusEmerald.copy(alpha = 0.15f), "COMPLETED", ManusEmerald)
        TaskStatus.FAILED -> Triple(ManusRed.copy(alpha = 0.15f), "FAILED", ManusRed)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
