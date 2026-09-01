package com.example.manus.ui.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.VoiceState
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
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
import com.example.ui.theme.VirgoCyanGlow
import com.example.ui.theme.VirgoGlassCard
import com.example.ui.theme.VirgoHoloPink
import com.example.ui.theme.VirgoNeonViolet

@Composable
fun VoiceAssistantView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val voiceEngine = viewModel.voiceAssistantEngine
    val voiceState by voiceEngine.voiceState.collectAsState()
    val currentVoice by voiceEngine.currentVoice.collectAsState()
    val liveTranscript by voiceEngine.liveTranscript.collectAsState()
    val conversation by voiceEngine.conversationHistory.collectAsState()
    val waveformAmplitudes by voiceEngine.waveformAmplitudes.collectAsState()

    val listState = rememberLazyListState()
    LaunchedEffect(conversation.size) {
        if (conversation.isNotEmpty()) {
            listState.animateScrollToItem(conversation.size - 1)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) 1.18f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VirgoGlassCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.RecordVoiceOver,
                    contentDescription = "Voice Mode",
                    tint = VirgoCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "VirgoYT Neural Voice Hub",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Active Synthesizer: ${currentVoice.name} (${currentVoice.gender})",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (voiceState) {
                            VoiceState.LISTENING -> ManusEmerald.copy(alpha = 0.2f)
                            VoiceState.PROCESSING -> ManusAmber.copy(alpha = 0.2f)
                            VoiceState.SPEAKING -> VirgoNeonViolet.copy(alpha = 0.2f)
                            VoiceState.IDLE -> ManusSlate800
                        }
                    )
                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (voiceState) {
                        VoiceState.LISTENING -> "● LISTENING"
                        VoiceState.PROCESSING -> "● PROCESSING"
                        VoiceState.SPEAKING -> "● SPEAKING"
                        VoiceState.IDLE -> "○ STANDBY"
                    },
                    color = when (voiceState) {
                        VoiceState.LISTENING -> ManusEmerald
                        VoiceState.PROCESSING -> ManusAmber
                        VoiceState.SPEAKING -> VirgoNeonViolet
                        VoiceState.IDLE -> ManusSlate400
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Holographic Voice Orb & Waveform Visualizer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(VirgoCyanGlow.copy(alpha = 0.4f), VirgoNeonViolet.copy(alpha = 0.4f))),
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = ManusSlate900.copy(alpha = 0.85f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Orb & Mic Button
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulseScale),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Hologram Rings
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        when (voiceState) {
                                            VoiceState.LISTENING -> ManusEmerald.copy(alpha = 0.35f)
                                            VoiceState.SPEAKING -> VirgoCyanGlow.copy(alpha = 0.35f)
                                            VoiceState.PROCESSING -> ManusAmber.copy(alpha = 0.35f)
                                            VoiceState.IDLE -> VirgoNeonViolet.copy(alpha = 0.2f)
                                        },
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Inner Interactive Action Button
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    when (voiceState) {
                                        VoiceState.LISTENING -> listOf(ManusEmerald, Color(0xFF059669))
                                        VoiceState.SPEAKING -> listOf(VirgoCyanGlow, VirgoNeonViolet)
                                        VoiceState.PROCESSING -> listOf(ManusAmber, Color(0xFFD97706))
                                        VoiceState.IDLE -> listOf(ManusIndigo, VirgoNeonViolet)
                                    }
                                )
                            )
                            .clickable {
                                if (voiceState == VoiceState.IDLE) {
                                    voiceEngine.startListening { query ->
                                        viewModel.dispatchUniversalAutonomousPrompt(query, emptyList())
                                    }
                                } else if (voiceState == VoiceState.LISTENING) {
                                    voiceEngine.stopListeningAndSubmit { query ->
                                        viewModel.dispatchUniversalAutonomousPrompt(query, emptyList())
                                    }
                                }
                            }
                            .testTag("voice_assistant_mic_orb"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (voiceState) {
                                VoiceState.LISTENING -> Icons.Default.Mic
                                VoiceState.SPEAKING -> Icons.AutoMirrored.Filled.VolumeUp
                                VoiceState.PROCESSING -> Icons.Default.GraphicEq
                                VoiceState.IDLE -> Icons.Default.Mic
                            },
                            contentDescription = "Mic",
                            tint = ManusWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-Time Waveform Frequency Bars (24 Bars)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    waveformAmplitudes.forEachIndexed { index, amp ->
                        val barHeight = (amp * 36.dp.value).coerceIn(4f, 36f).dp
                        val barColor = if (index % 2 == 0) VirgoCyanGlow else VirgoNeonViolet
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(barHeight)
                                .clip(RoundedCornerShape(2.dp))
                                .background(barColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = when (voiceState) {
                        VoiceState.LISTENING -> "Listening... Tap orb again to finish"
                        VoiceState.PROCESSING -> "Synthesizing voice intent & routing model..."
                        VoiceState.SPEAKING -> "VirgoYT Neural AI is speaking..."
                        VoiceState.IDLE -> "Tap orb to speak or choose a quick command"
                    },
                    color = when (voiceState) {
                        VoiceState.LISTENING -> ManusEmerald
                        VoiceState.SPEAKING -> VirgoCyanGlow
                        VoiceState.PROCESSING -> ManusAmber
                        VoiceState.IDLE -> ManusSlate400
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Voice Synthesizer Selector Pill Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            voiceEngine.availableVoices.forEach { voice ->
                val isSelected = currentVoice.id == voice.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) VirgoCyanGlow.copy(alpha = 0.2f) else ManusSlate900)
                        .border(
                            1.dp,
                            if (isSelected) VirgoCyanGlow else SleekBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { voiceEngine.selectVoice(voice) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (voice.gender == "Female") "👩" else "👨",
                            fontSize = 11.sp
                        )
                        Text(
                            text = voice.name,
                            color = if (isSelected) VirgoCyanGlow else ManusSlate300,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Command Action Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            QuickVoiceChip(
                label = "🎮 Generate UE5 Scene",
                onClick = {
                    voiceEngine.triggerVoiceResponse("Generating Unreal Engine 5 scene with Nanite terrain and Lumen lighting.")
                    viewModel.dispatchUniversalAutonomousPrompt("Generate Unreal Engine 5 scene with Nanite terrain and Lumen lighting", emptyList())
                }
            )
            QuickVoiceChip(
                label = "🛡️ Run Security Audit",
                onClick = {
                    voiceEngine.triggerVoiceResponse("Initiating multi-agent security and code vulnerability audit.")
                    viewModel.dispatchUniversalAutonomousPrompt("Run multi-agent security audit across workspace", emptyList())
                }
            )
            QuickVoiceChip(
                label = "🌐 Build Next.js Web App",
                onClick = {
                    voiceEngine.triggerVoiceResponse("Scaffolding modern Next.js web application with glassmorphism UI.")
                    viewModel.dispatchUniversalAutonomousPrompt("Build Next.js web application with modern glassmorphism UI", emptyList())
                }
            )
            QuickVoiceChip(
                label = "🧠 Query Vector Memory",
                onClick = {
                    voiceEngine.triggerVoiceResponse("Searching 1536-D vector embeddings database for user context.")
                    viewModel.dispatchUniversalAutonomousPrompt("Query vector memory for user coding preferences", emptyList())
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Voice Conversation Transcript Feed
        Text(
            text = "TRANSCRIPT STREAM",
            color = ManusSlate500,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(ManusSlate900)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversation) { item ->
                val isAssistant = item.speaker.contains("Virgo", ignoreCase = true)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAssistant) VirgoGlassCard else ManusSlate850)
                        .border(
                            1.dp,
                            if (isAssistant) VirgoCyanGlow.copy(alpha = 0.3f) else SleekBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isAssistant) "🤖 VirgoYT AI" else "👤 User Voice",
                            color = if (isAssistant) VirgoCyanGlow else ManusEmerald,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.text,
                        color = ManusWhite,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickVoiceChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ManusSlate850)
            .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Text(
            text = label,
            color = ManusSlate300,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
