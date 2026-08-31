package com.example.manus.ui.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.manus.data.agent.AgentExecutionState
import com.example.manus.data.agent.AgentRole
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder

@Composable
fun MultiAgentTeamSection(viewModel: ManusCloudViewModel) {
    val teamEngine = viewModel.multiAgentTeam
    val members by teamEngine.teamMembers.collectAsState()
    val pipeline by teamEngine.activePipeline.collectAsState()
    val chatLogs by teamEngine.teamChatLogs.collectAsState()

    var selectedMember by remember { mutableStateOf(members.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(ManusSlate950),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Active Multi-Agent Sprint Progress Card
        if (pipeline != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ManusCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🐝", fontSize = 14.sp)
                            Text(
                                text = "MULTI-AGENT SPRINT PIPELINE",
                                color = ManusCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "${pipeline?.progressPercent ?: 0}% COMPLETED",
                            color = ManusEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = pipeline?.goal ?: "",
                        color = ManusWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LinearProgressIndicator(
                        progress = { (pipeline?.progressPercent ?: 0) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ManusCyan,
                        trackColor = ManusSlate800
                    )
                }
            }
        }

        // Horizontal Grid / Row of all 15 Autonomous Agents
        Text(
            text = "Autonomous Multi-Agent Hive (15 Specialized Roles):",
            color = ManusWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members) { member ->
                val isSelected = selectedMember?.role == member.role
                val isBusy = member.state != AgentExecutionState.IDLE && member.state != AgentExecutionState.COMPLETED
                val stateColor = when (member.state) {
                    AgentExecutionState.IDLE -> ManusSlate400
                    AgentExecutionState.CODING, AgentExecutionState.THINKING, AgentExecutionState.EXECUTING_TOOL -> ManusAmber
                    AgentExecutionState.COMPLETED -> ManusEmerald
                    else -> ManusCyan
                }

                Box(
                    modifier = Modifier
                        .width(135.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.6f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedMember = member }
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = member.avatarEmoji, fontSize = 16.sp)
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(stateColor)
                            )
                        }
                        Text(
                            text = member.role.title.substringBefore(" Agent").substringBefore(" Specialist"),
                            color = ManusWhite,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = member.state.name,
                            color = stateColor,
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Inter-Agent Communication Stream
        Text(
            text = "Shared Vector Communication & Task Stream:",
            color = ManusSlate400,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(chatLogs.takeLast(12).reversed()) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = msg.fromRole.emoji, fontSize = 12.sp)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = msg.fromRole.title.substringBefore(" Agent"),
                                color = ManusCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = msg.message,
                            color = ManusWhite,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}
