package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun MultiAgentTeamSection(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val members by viewModel.multiAgentTeam.members.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC))
            .border(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
            .padding(12.dp)
    ) {
        Text(
            text = "⚡ Autonomous Multi-Agent Swarm",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            members.take(3).forEach { member ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                        .padding(8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(member.role.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = member.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                        Text(
                            text = member.currentThought,
                            fontSize = 10.sp,
                            color = if (themeMode.isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}
