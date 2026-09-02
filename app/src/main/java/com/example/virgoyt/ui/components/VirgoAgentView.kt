package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.virgoyt.data.model.AiModelTier
import com.example.virgoyt.data.model.ChatMessage
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun VirgoAgentView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val sessions by viewModel.routerEngine.activeChatSessions.collectAsState()
    val currentSessionId by viewModel.routerEngine.currentSessionId.collectAsState()
    val selectedModel by viewModel.routerEngine.selectedModel.collectAsState()
    val currentSession = sessions.find { it.id == currentSessionId } ?: sessions.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("virgo_agent_view_root")
    ) {
        MultiAgentTeamSection(viewModel = viewModel)

        // Model Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Model: ${selectedModel.displayName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF06B6D4)
            )
            TextButton(onClick = {
                val nextModel = if (selectedModel == AiModelTier.AUTO_ROUTER) AiModelTier.GEMINI_2_5_PRO else AiModelTier.AUTO_ROUTER
                viewModel.routerEngine.selectModel(nextModel)
            }) {
                Text("Switch (${selectedModel.badgeEmoji})", fontSize = 11.sp)
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(currentSession?.messages ?: emptyList()) { message ->
                ChatMessageItem(message = message, isDark = themeMode.isDark, onChipClick = { cmd ->
                    viewModel.executePrompt(cmd)
                })
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isDark: Boolean,
    onChipClick: (String) -> Unit
) {
    val isUser = message.role == "user"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (isUser) {
                Color(0xFF0284C7)
            } else {
                if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
            },
            border = if (!isUser) {
                androidx.compose.foundation.BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))
            } else null,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(message.modelUsed.badgeEmoji, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = message.modelUsed.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else if (isDark) Color(0xFFE2E8F0) else Color(0xFF0F172A)
                )

                if (message.quickActionChips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        message.quickActionChips.forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF06B6D4).copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onChipClick(chip.actionCommand) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(chip.iconEmoji, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(chip.label, fontSize = 11.sp, color = Color(0xFF06B6D4), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
