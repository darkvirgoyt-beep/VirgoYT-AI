package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.AiModelTier
import com.example.manus.data.model.AttachmentType
import com.example.manus.data.model.PromptAttachment
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
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

@Composable
fun UniversalPromptBar(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val modelRouter = viewModel.modelRouterEngine
    val selectedModel by modelRouter.selectedModel.collectAsState()
    val pendingAttachments by modelRouter.pendingAttachments.collectAsState()

    var promptText by remember { mutableStateOf("") }
    var isAttachMenuExpanded by remember { mutableStateOf(false) }
    var isModelMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ManusSlate950)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Pending Attachment Chips Row
        if (pendingAttachments.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(pendingAttachments) { attachment ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(ManusSlate850)
                            .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = attachment.type.icon, fontSize = 12.sp)
                            Text(
                                text = attachment.name,
                                color = ManusWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = ManusSlate400,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable { modelRouter.removeAttachment(attachment.id) }
                            )
                        }
                    }
                }
            }
        }

        // Top Micro-Row: Model Selector Pill & Quick Action Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Model Selector Dropdown Pill
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(ManusSlate900)
                        .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
                        .clickable { isModelMenuExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("model_selector_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = selectedModel.iconEmoji, fontSize = 12.sp)
                        Text(
                            text = selectedModel.displayName,
                            color = ManusCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Model",
                            tint = ManusSlate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = isModelMenuExpanded,
                    onDismissRequest = { isModelMenuExpanded = false },
                    modifier = Modifier
                        .background(ManusSlate900)
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                ) {
                    AiModelTier.values().forEach { tier ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(tier.iconEmoji, fontSize = 13.sp)
                                        Text(
                                            tier.displayName,
                                            color = if (selectedModel == tier) ManusCyan else ManusWhite,
                                            fontWeight = if (selectedModel == tier) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.5.sp
                                        )
                                        if (selectedModel == tier) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Active",
                                                tint = ManusCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${tier.provider} • ${tier.specialty}",
                                        color = ManusSlate400,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            onClick = {
                                modelRouter.selectModel(tier)
                                isModelMenuExpanded = false
                                viewModel.showToast("Switched to ${tier.displayName}")
                            }
                        )
                    }
                }
            }

            // New Chat Trigger
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(ManusSlate850)
                    .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                    .clickable {
                        viewModel.startNewChatSession("Session #${(100..999).random()}")
                        viewModel.showToast("✨ Started New Autonomous Chat Session")
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("new_chat_btn")
            ) {
                Text(
                    text = "+ New Chat",
                    color = ManusSlate300,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ==========================================
        // Main Message Box with [+] and [Send]
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // [+] Attachment Button
            Box {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(ManusSlate900)
                        .border(1.dp, SleekBorder, CircleShape)
                        .clickable { isAttachMenuExpanded = true }
                        .testTag("universal_plus_attach_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Attachments",
                        tint = ManusCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                DropdownMenu(
                    expanded = isAttachMenuExpanded,
                    onDismissRequest = { isAttachMenuExpanded = false },
                    modifier = Modifier
                        .background(ManusSlate900)
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                ) {
                    AttachmentDropdownItem(
                        icon = "📸",
                        title = "Photo / Concept Art",
                        subtitle = "Upload images for vision analysis & textures",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "concept_landscape.png",
                                    type = AttachmentType.IMAGE,
                                    uriOrData = "file:///assets/concept_landscape.png",
                                    metaDescription = "High-res concept art for open-world game"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached concept image")
                        }
                    )
                    AttachmentDropdownItem(
                        icon = "🧊",
                        title = "3D Model / GLB Asset",
                        subtitle = "Import 3D mesh, GLTF, or OBJ into scene",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "dinosaur_t_rex.glb",
                                    type = AttachmentType.MODEL_3D,
                                    uriOrData = "/workspace/assets/dinosaur_t_rex.glb",
                                    metaDescription = "Nanite-compatible 3D skeletal mesh"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached 3D GLB Model")
                        }
                    )
                    AttachmentDropdownItem(
                        icon = "🌍",
                        title = "Google Earth GIS Coordinates",
                        subtitle = "LiDAR DEM elevation and satellite maps",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "GrandCanyon_36.10N_112.11W.dem",
                                    type = AttachmentType.GIS_COORDINATES,
                                    uriOrData = "lat:36.1069,lon:-112.1129,dem:0.5m",
                                    metaDescription = "Google Earth 3D elevation heightmap"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached Google Earth GIS map data")
                        }
                    )
                    AttachmentDropdownItem(
                        icon = "🎥",
                        title = "Video / Cinematic Prompt",
                        subtitle = "Generate cutscenes & trailer sequences",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "cinematic_intro.mp4",
                                    type = AttachmentType.VIDEO,
                                    uriOrData = "prompt:ue5_cinematic_4k_trailer",
                                    metaDescription = "UE5 4K Ray Traced Cinematic"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached Cinematic Video Prompt")
                        }
                    )
                    AttachmentDropdownItem(
                        icon = "📁",
                        title = "Project Files / Codebase",
                        subtitle = "Attach repo, C++ classes, or scripts",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "UnrealEngine5_Project.zip",
                                    type = AttachmentType.FILE,
                                    uriOrData = "/workspace/UnrealEngine5",
                                    metaDescription = "UE5 C++ and Blueprint project files"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached project codebase")
                        }
                    )
                    AttachmentDropdownItem(
                        icon = "🔌",
                        title = "API & Cloud Connectors",
                        subtitle = "GitHub, OpenRouter, NVIDIA, Epic Games",
                        onClick = {
                            modelRouter.addAttachment(
                                PromptAttachment(
                                    name = "NVIDIA_NIM_OpenRouter_Connector",
                                    type = AttachmentType.CONNECTOR,
                                    uriOrData = "auth:oauth2_bearer",
                                    metaDescription = "Cloud AI Model Connector"
                                )
                            )
                            isAttachMenuExpanded = false
                            viewModel.showToast("Attached Cloud API Connector")
                        }
                    )
                }
            }

            // Input TextField
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                placeholder = {
                    Text(
                        "Ask VirgoYT AI (UE5 Game, 3D GLB, Google Earth, Code, Video)...",
                        color = ManusSlate500,
                        fontSize = 12.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ManusCyan,
                    unfocusedBorderColor = SleekBorder,
                    focusedTextColor = ManusWhite,
                    unfocusedTextColor = ManusWhite,
                    focusedContainerColor = ManusSlate900,
                    unfocusedContainerColor = ManusSlate900
                ),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("universal_prompt_input")
            )

            // Send Button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (promptText.isNotBlank() || pendingAttachments.isNotEmpty()) ManusIndigo else ManusSlate850)
                    .border(1.dp, SleekBorder, CircleShape)
                    .clickable {
                        if (promptText.isNotBlank() || pendingAttachments.isNotEmpty()) {
                            viewModel.dispatchUniversalAutonomousPrompt(promptText, pendingAttachments)
                            promptText = ""
                            modelRouter.clearPendingAttachments()
                        }
                    }
                    .testTag("universal_send_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (promptText.isNotBlank() || pendingAttachments.isNotEmpty()) ManusWhite else ManusSlate500,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AttachmentDropdownItem(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = icon, fontSize = 16.sp)
                Column {
                    Text(text = title, color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = ManusSlate400, fontSize = 10.sp)
                }
            }
        },
        onClick = onClick
    )
}
