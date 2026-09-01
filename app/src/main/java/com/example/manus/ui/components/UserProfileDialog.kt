package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.manus.data.model.AiTone
import com.example.manus.data.model.CodeStylePreference
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusPurple
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

@Composable
fun UserProfileDialog(
    viewModel: ManusCloudViewModel,
    onDismiss: () -> Unit
) {
    val prefEngine = viewModel.userPreferenceEngine
    val preferences by prefEngine.preferences.collectAsState()
    val learnedTraits by prefEngine.learnedTraits.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()

    var systemPrompt by remember { mutableStateOf(preferences.customSystemPrompt) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(VirgoCyanGlow.copy(alpha = 0.5f), VirgoNeonViolet.copy(alpha = 0.5f))),
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = ManusSlate950)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(VirgoCyanGlow, VirgoNeonViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = ManusWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Column {
                            Text(
                                text = currentSession?.user?.displayName ?: "darkvirgoyt-beep",
                                color = ManusWhite,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Chief AI Architect • ${currentSession?.user?.email ?: "darkvirgoyt@gmail.com"}",
                                color = VirgoCyanGlow,
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = ManusSlate400)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // AI Response Tone Section
                    item {
                        Text(
                            text = "AI RESPONSE TONE",
                            color = ManusSlate500,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            AiTone.values().forEach { tone ->
                                val isSelected = preferences.tone == tone
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VirgoCyanGlow.copy(alpha = 0.15f) else ManusSlate900)
                                        .border(1.dp, if (isSelected) VirgoCyanGlow else SleekBorder, RoundedCornerShape(8.dp))
                                        .clickable { prefEngine.updateTone(tone) }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = tone.label,
                                        color = if (isSelected) VirgoCyanGlow else ManusSlate300,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = VirgoCyanGlow, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Code Style Preference Section
                    item {
                        Text(
                            text = "CODE GENERATION STYLE",
                            color = ManusSlate500,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            CodeStylePreference.values().forEach { style ->
                                val isSelected = preferences.codeStyle == style
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) VirgoNeonViolet.copy(alpha = 0.15f) else ManusSlate900)
                                        .border(1.dp, if (isSelected) VirgoNeonViolet else SleekBorder, RoundedCornerShape(8.dp))
                                        .clickable { prefEngine.updateCodeStyle(style) }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = style.label,
                                        color = if (isSelected) VirgoNeonViolet else ManusSlate300,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = VirgoNeonViolet, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Learned User Traits
                    item {
                        Text(
                            text = "LEARNED MEMORY PATTERNS (${learnedTraits.size})",
                            color = ManusSlate500,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate900)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            learnedTraits.forEach { trait ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = "🧠", fontSize = 11.sp)
                                    Text(
                                        text = trait,
                                        color = ManusSlate300,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // Custom System Prompt
                    item {
                        Text(
                            text = "CUSTOM SYSTEM PROMPT",
                            color = ManusSlate500,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = systemPrompt,
                            onValueChange = { systemPrompt = it },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = VirgoCyanGlow,
                                unfocusedBorderColor = SleekBorder,
                                focusedTextColor = ManusWhite,
                                unfocusedTextColor = ManusWhite,
                                focusedContainerColor = ManusSlate900,
                                unfocusedContainerColor = ManusSlate900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        prefEngine.updateSystemPrompt(systemPrompt)
                        viewModel.showToast("✓ Preferences updated cleanly")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("save_profile_prefs_btn")
                ) {
                    Text("Save & Apply to AI Engine", color = ManusWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
