package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
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
import kotlin.math.roundToInt

@Composable
fun LiveComputerDiffView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    var selectedDiffMode by remember { mutableIntStateOf(2) } // 0 = Diff, 1 = Original, 2 = Modified (matches screenshot)
    var timelineProgress by remember { mutableFloatStateOf(0.92f) }
    var isUserInRemoteControl by remember { mutableStateOf(false) }
    var isComputerExpanded by remember { mutableStateOf(false) } // Toggles small/compact vs big/expanded fullscreen computer view
    var virtualCursorX by remember { mutableFloatStateOf(180f) }
    var virtualCursorY by remember { mutableFloatStateOf(240f) }

    val infiniteTransition = rememberInfiniteTransition(label = "LivePulse")
    val livePulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LiveAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(if (isComputerExpanded) 8.dp else 14.dp)
    ) {
        // ==========================================
        // Top Header: "VirgoYT AI's computer" + Stream Controls
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { viewModel.selectTab(ActiveWorkspaceTab.AGENT) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Computer View",
                    tint = ManusWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = if (isComputerExpanded) "VirgoYT AI's Computer (Expanded)" else "VirgoYT AI's computer",
                color = ManusWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Resize & Live Remote Cast Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Small / Big Size Toggle Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isComputerExpanded) ManusIndigo else ManusSlate850)
                        .border(1.dp, if (isComputerExpanded) ManusIndigoLight else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            isComputerExpanded = !isComputerExpanded
                            viewModel.showToast(if (isComputerExpanded) "🖥 Cloud Computer Expanded (Big)" else "📱 Cloud Computer Compact (Small)")
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("cloud_computer_resize_toggle")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isComputerExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = "Resize Computer",
                            tint = if (isComputerExpanded) ManusWhite else ManusCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isComputerExpanded) "Small" else "Big",
                            color = ManusWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isUserInRemoteControl) ManusIndigo else ManusSlate850)
                        .border(1.dp, if (isUserInRemoteControl) ManusIndigoLight else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable {
                            isUserInRemoteControl = !isUserInRemoteControl
                            viewModel.showToast(if (isUserInRemoteControl) "🎮 Remote Desktop Control Taken" else "🤖 Returned to VirgoYT AI Autonomous Mode")
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("remote_control_toggle")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isUserInRemoteControl) Icons.Default.TouchApp else Icons.Default.Tv,
                            contentDescription = "Cast Status",
                            tint = if (isUserInRemoteControl) ManusWhite else ManusCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isUserInRemoteControl) "Controlling" else "Auto Stream",
                            color = ManusWhite,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // ==========================================
        // Center Display: Code Window / Virtual Screen (Matching Screenshot)
        // ==========================================
        Card(
            modifier = Modifier
                .weight(if (isComputerExpanded) 1f else 0.85f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, if (isComputerExpanded) ManusCyan.copy(alpha = 0.6f) else SleekBorder, RoundedCornerShape(12.dp))
                .clickable {
                    // Tap on screen to toggle small and big when not actively dragging cursor
                    if (!isUserInRemoteControl) {
                        isComputerExpanded = !isComputerExpanded
                        viewModel.showToast(if (isComputerExpanded) "🖥 Cloud Computer Expanded (Big)" else "📱 Cloud Computer Compact (Small)")
                    }
                }
                .pointerInput(isUserInRemoteControl) {
                    if (isUserInRemoteControl) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            virtualCursorX += dragAmount.x
                            virtualCursorY += dragAmount.y
                        }
                    }
                },
            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Window Top Bar: Centered File Name "llm.ts" + Tap to Resize Hint
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ManusSlate850)
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isComputerExpanded) "🖥 EXPANDED" else "📱 COMPACT",
                                color = ManusCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "llm.ts",
                                color = ManusSlate300,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap screen to toggle size",
                                color = ManusSlate500,
                                fontSize = 9.sp
                            )
                        }
                    }

                    // Code Content Area with Syntax & Diff
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        when (selectedDiffMode) {
                            0 -> {
                                // DIFF VIEW (Added/Removed Lines)
                                DiffCodeLine("- export type Role = \"user\" | \"assistant\";", isRemoved = true)
                                DiffCodeLine("+ export type Role = \"system\" | \"user\" | \"assistant\" | \"tool\" | \"function\";", isAdded = true)
                                DiffCodeLine("  import { ENV } from \"./env\";")
                                DiffCodeLine("  export type TextContent = { type: \"text\"; text: string; };")
                                DiffCodeLine("+ export type ImageContent = {")
                                DiffCodeLine("+   type: \"image_url\";")
                                DiffCodeLine("+   image_url: { url: string; detail?: \"auto\" | \"low\" | \"high\"; };")
                                DiffCodeLine("+ };", isAdded = true)
                                DiffCodeLine("+ export type ModelRouterTier = \"auto\" | \"nvidia_nemotron\" | \"claude_3_5\" | \"ue5_studio\";", isAdded = true)
                            }
                            1 -> {
                                // ORIGINAL VIEW
                                CodeSnippetLine("import { ENV } from \"./env\";")
                                CodeSnippetLine("export type Role = \"user\" | \"assistant\";")
                                CodeSnippetLine("export type TextContent = {")
                                CodeSnippetLine("  type: \"text\";")
                                CodeSnippetLine("  text: string;")
                                CodeSnippetLine("};")
                            }
                            2 -> {
                                // MODIFIED VIEW (Exact match to User Screenshot)
                                CodeSnippetLine("import { ENV } from \"./env\";")
                                CodeSnippetLine("")
                                CodeSnippetLine("export type Role = \"system\" | \"user\" | \"assistant\" | \"tool\" |")
                                CodeSnippetLine("\"function\";")
                                CodeSnippetLine("")
                                CodeSnippetLine("export type TextContent = {")
                                CodeSnippetLine("  type: \"text\";")
                                CodeSnippetLine("  text: string;")
                                CodeSnippetLine("};")
                                CodeSnippetLine("")
                                CodeSnippetLine("export type ImageContent = {")
                                CodeSnippetLine("  type: \"image_url\";")
                                CodeSnippetLine("  image_url: {")
                                CodeSnippetLine("    url: string;")
                                CodeSnippetLine("    detail?: \"auto\" | \"low\" | \"high\";")
                                CodeSnippetLine("  };")
                                CodeSnippetLine("};")
                            }
                        }
                    }

                    // Floating Segmented Selector: [ Diff | Original | Modified ]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(ManusSlate950.copy(alpha = 0.85f))
                                .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SegmentedTabButton(
                                    label = "Diff",
                                    isSelected = selectedDiffMode == 0,
                                    onClick = { selectedDiffMode = 0 }
                                )
                                SegmentedTabButton(
                                    label = "Original",
                                    isSelected = selectedDiffMode == 1,
                                    onClick = { selectedDiffMode = 1 }
                                )
                                SegmentedTabButton(
                                    label = "Modified",
                                    isSelected = selectedDiffMode == 2,
                                    onClick = { selectedDiffMode = 2 }
                                )
                            }
                        }
                    }
                }

                // Interactive Virtual Mouse Pointer (Visible when Remote Desktop Control is active)
                if (isUserInRemoteControl) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(virtualCursorX.roundToInt(), virtualCursorY.roundToInt()) }
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Virtual Cursor",
                            tint = ManusCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // ==========================================
        // Bottom Action Card: "VirgoYT AI is using Editor" (Matching Screenshot)
        // ==========================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Square Icon with subtle border
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusSlate850)
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editor Status",
                        tint = ManusSlate300,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VirgoYT AI is using Editor",
                        color = ManusWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Editing file: jarvis-personal-ai/server/_core/llm.ts",
                        color = ManusSlate400,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Secret Vault Quick Trigger
                IconButton(
                    onClick = { viewModel.openSecretBox("GitHub / Epic Games", "Authentication required for repository commit & cloud build") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Credential Vault",
                        tint = ManusIndigoLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ==========================================
        // Live Timeline Scrubber Bar (Matching Screenshot)
        // ==========================================
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Slider(
                value = timelineProgress,
                onValueChange = { timelineProgress = it },
                colors = SliderDefaults.colors(
                    thumbColor = ManusWhite,
                    activeTrackColor = ManusSlate300,
                    inactiveTrackColor = ManusSlate800
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            // Playback Controls: [ Rewind | ● Live | Forward ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        timelineProgress = (timelineProgress - 0.1f).coerceAtLeast(0f)
                        viewModel.showToast("⏮ Rewound 10s")
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind",
                        tint = ManusWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Center Pulsing Live Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.clickable {
                        timelineProgress = 1.0f
                        viewModel.showToast("● Jumped to Live Stream")
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ManusEmerald)
                            .alpha(livePulseAlpha)
                    )
                    Text(
                        text = "Live",
                        color = ManusWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        timelineProgress = (timelineProgress + 0.1f).coerceAtMost(1.0f)
                        viewModel.showToast("⏭ Fast Forwarded to Live")
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward",
                        tint = ManusWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SegmentedTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ManusSlate800 else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) ManusWhite else ManusSlate400,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun CodeSnippetLine(text: String) {
    Text(
        text = text,
        color = ManusSlate300,
        fontSize = 11.5.sp,
        fontFamily = FontFamily.Monospace,
        lineHeight = 16.sp
    )
}

@Composable
private fun DiffCodeLine(
    text: String,
    isAdded: Boolean = false,
    isRemoved: Boolean = false
) {
    val bgColor = when {
        isAdded -> ManusEmerald.copy(alpha = 0.15f)
        isRemoved -> ManusRed.copy(alpha = 0.15f)
        else -> Color.Transparent
    }
    val textColor = when {
        isAdded -> ManusEmerald
        isRemoved -> ManusRed
        else -> ManusSlate300
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 16.sp
        )
    }
}
