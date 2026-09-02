package com.example.manus.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SettingsVoice
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.OutputType
import com.example.manus.data.model.TerminalEntry
import com.example.manus.data.model.TerminalMode
import com.example.manus.data.voice.SpeechEngineType
import com.example.manus.data.voice.VoiceDispatchTarget
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
import com.example.ui.theme.ManusSlate300
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
import com.example.ui.theme.TermBg
import com.example.ui.theme.TermGreen
import com.example.ui.theme.TermPrompt
import com.example.ui.theme.TermRed
import com.example.ui.theme.TermText
import com.example.ui.theme.TermYellow

@Composable
fun CloudTerminalView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.terminal.entries.collectAsState()
    val isExecuting by viewModel.terminal.isExecuting.collectAsState()
    val terminalInput by viewModel.terminalInput.collectAsState()
    val session by viewModel.currentSession.collectAsState()
    val terminalMode by viewModel.terminalMode.collectAsState()
    val isGitHubConnected by viewModel.isGitHubConnected.collectAsState()
    val gitHubUser by viewModel.gitHubUser.collectAsState()
    val activeUser = session?.user?.username ?: "developer"
    val currentDir = viewModel.terminal.getCurrentDir()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val isCursorTerminalOpen by viewModel.isCursorTerminalAiOpen.collectAsState()
    val cursorSuggestedCommand by viewModel.cursorTerminalSuggestedCommand.collectAsState()
    var cursorQueryInput by remember { mutableStateOf("") }

    val voiceManager = viewModel.webSpeechVoiceManager
    val isVoiceListening by voiceManager.isListening.collectAsState()
    val liveVoiceTranscript by voiceManager.liveTranscript.collectAsState()
    val partialVoiceTranscript by voiceManager.partialTranscript.collectAsState()
    val waveformBars by voiceManager.waveformBars.collectAsState()
    val voiceDispatchTarget by voiceManager.dispatchTarget.collectAsState()
    val activeEngine by voiceManager.activeEngine.collectAsState()
    val recentVoiceCommands by voiceManager.recentVoiceCommands.collectAsState()
    var showVoiceSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        voiceManager.startListening(context) { cmd, target ->
            viewModel.dispatchVoiceCommandToTarget(cmd, target)
        }
    }

    // Auto-scroll on new entries
    LaunchedEffect(entries.size) {
        if (entries.isNotEmpty()) {
            listState.animateScrollToItem(entries.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TermBg)
    ) {
        // Sleek Terminal Window Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate900)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Window indicator dots & prompt header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFEF4444).copy(alpha = 0.8f)))
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFF59E0B).copy(alpha = 0.8f)))
                        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(Color(0xFF10B981).copy(alpha = 0.8f)))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "$activeUser@${terminalMode.promptPrefix}:~${currentDir.replace("/workspace", "")}",
                            color = ManusSlate400,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Top Action buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Cursor AI Terminal Trigger (Cmd+K)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isCursorTerminalOpen) ManusIndigo else ManusIndigoBg)
                                .border(1.dp, if (isCursorTerminalOpen) ManusIndigoLight else ManusIndigo.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable { viewModel.toggleCursorTerminalAi() }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("terminal_cursor_ai_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Cursor AI",
                                    tint = if (isCursorTerminalOpen) ManusWhite else ManusIndigoLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Cursor",
                                    color = if (isCursorTerminalOpen) ManusWhite else ManusIndigoLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Web Speech API Voice Modal Trigger
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isVoiceListening) Color(0xFFEF4444) else ManusSlate800)
                                .border(1.dp, if (isVoiceListening) Color(0xFFFCA5A5) else SleekBorder, RoundedCornerShape(6.dp))
                                .clickable {
                                    if (isVoiceListening) {
                                        voiceManager.stopListening(submit = true)
                                    } else {
                                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("terminal_header_voice_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isVoiceListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                    contentDescription = "Web Speech API Voice",
                                    tint = if (isVoiceListening) ManusWhite else ManusEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isVoiceListening) "Listening..." else "Web Speech",
                                    color = ManusWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // CLI Installer Modal Trigger
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManusSlate800)
                                .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                                .clickable { viewModel.openCliInstallerDialog() }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                                .testTag("terminal_cli_install_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Install CLI",
                                    tint = ManusCyan,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "CLI",
                                    color = ManusWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val allText = entries.joinToString("\n") { it.text }
                                clipboardManager.setText(AnnotatedString(allText))
                                viewModel.showToast("Copied terminal output")
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Output",
                                tint = ManusSlate400,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.terminal.clear() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear Terminal",
                                tint = ManusSlate400,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Sub-Bar: Host Switcher (Cloud VM vs Localhost) & GitHub Integration Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Mode Switcher Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (terminalMode == TerminalMode.CLOUD_VM) ManusIndigo else ManusSlate850)
                                .border(1.dp, if (terminalMode == TerminalMode.CLOUD_VM) ManusIndigoLight else SleekBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.switchTerminalMode(TerminalMode.CLOUD_VM) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("mode_cloud_vm")
                        ) {
                            Text(
                                text = "☁️ Cloud VM",
                                color = if (terminalMode == TerminalMode.CLOUD_VM) ManusWhite else ManusSlate400,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (terminalMode == TerminalMode.LOCALHOST) ManusIndigo else ManusSlate850)
                                .border(1.dp, if (terminalMode == TerminalMode.LOCALHOST) ManusIndigoLight else SleekBorder, RoundedCornerShape(4.dp))
                                .clickable { viewModel.switchTerminalMode(TerminalMode.LOCALHOST) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .testTag("mode_localhost")
                        ) {
                            Text(
                                text = "💻 Localhost :8080",
                                color = if (terminalMode == TerminalMode.LOCALHOST) ManusWhite else ManusSlate400,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // GitHub Status Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isGitHubConnected) ManusIndigoBg else ManusSlate850)
                            .border(1.dp, if (isGitHubConnected) ManusGreen.copy(alpha = 0.6f) else SleekBorder, RoundedCornerShape(4.dp))
                            .clickable { viewModel.openGitHubAuthDialog() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .testTag("terminal_github_auth_chip")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub",
                                tint = if (isGitHubConnected) ManusGreen else ManusCyan,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (isGitHubConnected) "@${gitHubUser?.username ?: "connected"}" else "Connect GitHub",
                                color = if (isGitHubConnected) ManusGreen else ManusSlate300,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // Cursor AI Terminal Assistant Bar (Cmd+K Natural Language)
        // ==========================================
        if (isCursorTerminalOpen) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ManusSlate900)
                    .border(1.dp, ManusIndigo.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ManusIndigoLight, modifier = Modifier.size(13.dp))
                            Text(
                                "Cursor AI: Natural Language to Bash (Cmd+K)",
                                color = ManusWhite,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { viewModel.closeCursorTerminalAi() },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = ManusSlate400, modifier = Modifier.size(13.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = cursorQueryInput,
                            onValueChange = { cursorQueryInput = it },
                            placeholder = { Text("e.g. 'find all python files', 'check memory', 'run analyzer'...", color = ManusSlate500, fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("terminal_cursor_query_input"),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = ManusWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ManusIndigo,
                                unfocusedBorderColor = SleekBorder,
                                focusedTextColor = ManusWhite,
                                unfocusedTextColor = ManusWhite,
                                focusedContainerColor = ManusSlate850,
                                unfocusedContainerColor = ManusSlate850
                            )
                        )

                        Button(
                            onClick = {
                                if (cursorQueryInput.isNotBlank()) {
                                    viewModel.translateNaturalLanguageToTerminal(cursorQueryInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(44.dp).testTag("terminal_cursor_translate_button")
                        ) {
                            Text("Generate", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (cursorSuggestedCommand != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusIndigoBg)
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Suggested: ${cursorSuggestedCommand}",
                                color = ManusIndigoSoft,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    viewModel.executeTerminalCommand(cursorSuggestedCommand)
                                    viewModel.closeCursorTerminalAi()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ManusGreen),
                                shape = RoundedCornerShape(4.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Run Now", color = ManusSlate950, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Terminal Log Display Screen (Pure pitch black with crisp monospace typography)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(TermBg)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    TerminalEntryItem(
                        entry = entry,
                        activeUser = activeUser,
                        onFixError = { errorText ->
                            val lastCmd = entries.filter { it.type == OutputType.COMMAND }.lastOrNull()?.text ?: ""
                            viewModel.autoFixTerminalError(lastCmd, errorText)
                        }
                    )
                }

                if (isExecuting) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                color = ManusIndigoLight,
                                strokeWidth = 1.5.dp,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Executing process in cloud kernel...",
                                color = ManusIndigoLight,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Sleek Terminal Keyboard Keys Row (Esc, Tab, Ctrl, Alt, Fn, |, ~, /)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate900)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item { TerminalKeyButton("Esc") { viewModel.setTerminalInput("") } }
                item { TerminalKeyButton("Tab") { viewModel.setTerminalInput(terminalInput + "  ") } }
                item { TerminalKeyButton("Ctrl+C") { viewModel.terminal.clear() } }
                item { TerminalKeyButton("Alt") { } }
                item { TerminalKeyButton("Fn") { } }
                item { TerminalKeyButton("|") { viewModel.setTerminalInput(terminalInput + " | ") } }
                item { TerminalKeyButton("~") { viewModel.setTerminalInput(terminalInput + "~") } }
                item { TerminalKeyButton("/") { viewModel.setTerminalInput(terminalInput + "/") } }
                item { TerminalKeyButton("-la") { viewModel.setTerminalInput(terminalInput + " -la") } }
            }
        }

        // Web Speech API Voice Command & Swarm Audio Overlay (Active when listening or has transcript)
        AnimatedVisibility(
            visible = isVoiceListening || liveVoiceTranscript.isNotBlank() || partialVoiceTranscript.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ManusSlate950)
                    .border(1.dp, if (isVoiceListening) ManusEmerald.copy(alpha = 0.8f) else SleekBorder)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Top strip: Engine badge + Target selector + Waveform equalizer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isVoiceListening) Color(0xFF10B981) else ManusAmber)
                            )
                            Text(
                                text = if (isVoiceListening) "🎙️ WEB SPEECH MIC ACTIVE" else "🎙️ VOICE SPEECH READY",
                                color = if (isVoiceListening) ManusEmerald else ManusAmber,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "• [${activeEngine.displayName}]",
                                color = ManusSlate500,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Target Selector Chips
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            VoiceDispatchTarget.values().forEach { target ->
                                val isSelected = voiceDispatchTarget == target
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSelected) ManusIndigo else ManusSlate850)
                                        .border(1.dp, if (isSelected) ManusIndigoLight else SleekBorder, RoundedCornerShape(4.dp))
                                        .clickable { voiceManager.setDispatchTarget(target) }
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${target.iconEmoji} ${target.displayName}",
                                        color = if (isSelected) ManusWhite else ManusSlate400,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Live Waveform Visualizer & Transcript Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Animated Waveform Bars
                        Row(
                            modifier = Modifier
                                .height(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusSlate900)
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            waveformBars.forEach { heightRatio ->
                                val barHeight = (4 + heightRatio * 16).coerceIn(4f, 20f)
                                val barColor = when {
                                    heightRatio > 0.6f -> ManusCyan
                                    heightRatio > 0.3f -> ManusEmerald
                                    else -> ManusIndigo
                                }
                                Box(
                                    modifier = Modifier
                                        .width(2.5.dp)
                                        .height(barHeight.dp)
                                        .clip(RoundedCornerShape(1.dp))
                                        .background(barColor)
                                )
                            }
                        }

                        // Live Speech Transcript Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManusSlate900)
                                .border(1.dp, ManusIndigo.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            val displayText = when {
                                partialVoiceTranscript.isNotBlank() -> partialVoiceTranscript
                                liveVoiceTranscript.isNotBlank() -> liveVoiceTranscript
                                else -> "Listening for voice commands (e.g., 'Swarm build 3D app', 'Run wine calc.exe')..."
                            }
                            Text(
                                text = displayText,
                                color = if (partialVoiceTranscript.isNotBlank() || liveVoiceTranscript.isNotBlank()) ManusWhite else ManusSlate500,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = if (partialVoiceTranscript.isNotBlank()) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 2
                            )
                        }

                        // Immediate Action buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isVoiceListening) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF10B981))
                                        .clickable {
                                            voiceManager.stopListening(submit = true)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                        .testTag("terminal_voice_submit_button")
                                ) {
                                    Text(
                                        text = "⚡ Send",
                                        color = ManusWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ManusSlate800)
                                    .clickable {
                                        voiceManager.stopListening(submit = false)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel Voice",
                                    tint = ManusSlate400,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    // Voice Command Shortcut Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Text(
                                text = "SAY:",
                                color = ManusSlate500,
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        item {
                            QuickCmdButton("🤖 Swarm: Audit full security") {
                                viewModel.dispatchVoiceCommandToTarget("Swarm audit all security vulnerabilities", VoiceDispatchTarget.AGENT_SWARM)
                            }
                        }
                        item {
                            QuickCmdButton("🤖 Swarm: Build cross-platform app") {
                                viewModel.dispatchVoiceCommandToTarget("Agent team build cross platform responsive web app", VoiceDispatchTarget.AGENT_SWARM)
                            }
                        }
                        item {
                            QuickCmdButton("⚡ Run: wine setup.exe") {
                                viewModel.dispatchVoiceCommandToTarget("wine setup.exe", VoiceDispatchTarget.TERMINAL_EXEC)
                            }
                        }
                        item {
                            QuickCmdButton("⚡ Run: gcc -O3 main.c") {
                                viewModel.dispatchVoiceCommandToTarget("gcc -O3 main.c -o app && ./app", VoiceDispatchTarget.TERMINAL_EXEC)
                            }
                        }
                        item {
                            QuickCmdButton("⚡ Run: python3 test.py") {
                                viewModel.dispatchVoiceCommandToTarget("python3 scripts/data_analyzer.py", VoiceDispatchTarget.TERMINAL_EXEC)
                            }
                        }
                    }
                }
            }
        }

        // Quick Command Preset Chips Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate950)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = "QUICK:",
                        color = ManusSlate500,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                item { QuickCmdButton("gh auth login") { viewModel.executeTerminalCommand("gh auth login") } }
                item { QuickCmdButton("gh repo list") { viewModel.executeTerminalCommand("gh repo list") } }
                item { QuickCmdButton("virgoyt install") { viewModel.executeTerminalCommand("virgoyt install") } }
                item { QuickCmdButton("virgoyt mode local") { viewModel.executeTerminalCommand("virgoyt mode local") } }
                item { QuickCmdButton("git status") { viewModel.executeTerminalCommand("git status") } }
                item { QuickCmdButton("ls -la") { viewModel.executeTerminalCommand("ls -la") } }
                item { QuickCmdButton("whoami") { viewModel.executeTerminalCommand("whoami") } }
                item { QuickCmdButton("python3 data_analyzer.py") { viewModel.executeTerminalCommand("python3 scripts/data_analyzer.py") } }
                item { QuickCmdButton("node benchmark.js") { viewModel.executeTerminalCommand("node scripts/benchmark.js") } }
                item { QuickCmdButton("gcc main.c -o sort && ./sort") { viewModel.executeTerminalCommand("gcc main.c -o sort && ./sort") } }
                item { QuickCmdButton("bash run.sh") { viewModel.executeTerminalCommand("bash run.sh") } }
                item { QuickCmdButton("top") { viewModel.executeTerminalCommand("top") } }
                item { QuickCmdButton("help") { viewModel.executeTerminalCommand("help") } }
            }
        }

        // Interactive Prompt Input Bar with Sleek Styling
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate900)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Target Dispatch Indicator / Mode Switcher
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ManusSlate850)
                        .border(1.dp, SleekBorder, RoundedCornerShape(4.dp))
                        .clickable {
                            val nextTarget = when (voiceDispatchTarget) {
                                VoiceDispatchTarget.AGENT_SWARM -> VoiceDispatchTarget.TERMINAL_EXEC
                                VoiceDispatchTarget.TERMINAL_EXEC -> VoiceDispatchTarget.TERMINAL_PROMPT
                                VoiceDispatchTarget.TERMINAL_PROMPT -> VoiceDispatchTarget.AGENT_SWARM
                            }
                            voiceManager.setDispatchTarget(nextTarget)
                        }
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                        .testTag("terminal_voice_target_toggle")
                ) {
                    Text(
                        text = "${voiceDispatchTarget.iconEmoji} ${voiceDispatchTarget.displayName}",
                        color = ManusIndigoLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "$activeUser@cloud-pc:~$",
                    color = ManusIndigoLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )

                OutlinedTextField(
                    value = terminalInput,
                    onValueChange = { viewModel.setTerminalInput(it) },
                    placeholder = {
                        Text(
                            "cp, mv, upload, download, whoami, python3, gcc...",
                            color = ManusSlate600,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = ManusWhite,
                        unfocusedTextColor = ManusSlate200,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (!isExecuting && terminalInput.isNotBlank()) {
                            viewModel.executeTerminalCommand()
                        }
                    })
                )

                // Voice Mic Action Button (Web Speech API)
                IconButton(
                    onClick = {
                        if (isVoiceListening) {
                            voiceManager.stopListening(submit = true)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isVoiceListening) Color(0xFFEF4444) else ManusSlate800)
                        .border(1.dp, if (isVoiceListening) Color(0xFFF87171) else SleekBorder, RoundedCornerShape(8.dp))
                        .testTag("terminal_voice_mic_button")
                ) {
                    Icon(
                        imageVector = if (isVoiceListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Web Speech Voice Input",
                        tint = if (isVoiceListening) ManusWhite else ManusCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.executeTerminalCommand() },
                    enabled = !isExecuting && terminalInput.isNotBlank(),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (terminalInput.isNotBlank()) ManusIndigo else SleekSurface)
                        .testTag("terminal_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Execute",
                        tint = if (terminalInput.isNotBlank()) ManusWhite else ManusSlate600,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalEntryItem(
    entry: TerminalEntry,
    activeUser: String = "developer",
    onFixError: ((String) -> Unit)? = null
) {
    val (color, prefix) = when (entry.type) {
        OutputType.COMMAND -> Pair(ManusIndigoLight, "$activeUser@cloud-pc:~$ ")
        OutputType.STDOUT -> Pair(ManusSlate200, "")
        OutputType.STDERR -> Pair(TermRed, "✖ ")
        OutputType.SYSTEM -> Pair(ManusIndigoSoft, "")
        OutputType.AGENT_ACTION -> Pair(ManusEmerald, "[INFO] ")
        OutputType.SUCCESS -> Pair(TermGreen, "✓ ")
        OutputType.WARNING -> Pair(TermYellow, "⚠ ")
    }

    if (entry.type == OutputType.COMMAND) {
        Row {
            Text(
                text = "$activeUser@cloud-pc:~$ ",
                color = ManusIndigoLight,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = entry.text.substringAfter("$ ").ifEmpty { entry.text },
                color = ManusWhite,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "$prefix${entry.text}",
                color = color,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp
            )

            if (entry.type == OutputType.STDERR && onFixError != null) {
                Box(
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ManusIndigoBg)
                        .border(1.dp, ManusIndigo.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onFixError(entry.text) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ManusIndigoLight,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Fix with Cursor AI",
                            color = ManusIndigoLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TerminalKeyButton(key: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SleekSurface)
            .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = key,
            color = ManusSlate400,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun QuickCmdButton(cmd: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SleekSurface)
            .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = cmd,
            color = ManusIndigoSoft,
            fontSize = 9.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
