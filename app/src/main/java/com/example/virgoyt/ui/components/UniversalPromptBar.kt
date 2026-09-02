package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun UniversalPromptBar(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val isBusy by viewModel.agentService.isAgentBusy.collectAsState()
    var promptInput by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    viewModel.voiceEngine.voiceManager.simulateVoiceInput(
                        "Deploy modern Next.js 15 app with 3D canvas and automated test pipeline"
                    ) { text, _ ->
                        viewModel.executePrompt(text)
                    }
                },
                modifier = Modifier.testTag("voice_input_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color(0xFF06B6D4)
                )
            }

            TextField(
                value = promptInput,
                onValueChange = { promptInput = it },
                placeholder = {
                    Text(
                        "Ask VirgoYT AI Swarm, generate code, run terminal...",
                        fontSize = 13.sp,
                        color = if (themeMode.isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("universal_prompt_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = if (themeMode.isDark) Color.White else Color(0xFF0F172A),
                    unfocusedTextColor = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
                ),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (promptInput.isNotBlank() && !isBusy) {
                        val input = promptInput
                        promptInput = ""
                        viewModel.selectTab(com.example.virgoyt.data.model.ActiveWorkspaceTab.AGENT)
                        viewModel.executePrompt(input)
                    }
                })
            )

            IconButton(
                onClick = {
                    if (promptInput.isNotBlank() && !isBusy) {
                        val input = promptInput
                        promptInput = ""
                        viewModel.selectTab(com.example.virgoyt.data.model.ActiveWorkspaceTab.AGENT)
                        viewModel.executePrompt(input)
                    }
                },
                enabled = promptInput.isNotBlank() && !isBusy,
                modifier = Modifier.testTag("prompt_submit_btn")
            ) {
                if (isBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF06B6D4)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Prompt",
                        tint = if (promptInput.isNotBlank()) Color(0xFF06B6D4) else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
