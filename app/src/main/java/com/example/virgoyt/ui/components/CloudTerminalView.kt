package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.data.model.OutputType
import com.example.virgoyt.data.model.TerminalEntry
import com.example.virgoyt.ui.VirgoCloudViewModel
import kotlinx.coroutines.launch

@Composable
fun CloudTerminalView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val entries by viewModel.terminalEngine.terminalEntries.collectAsState()
    val terminalMode by viewModel.terminalEngine.terminalMode.collectAsState()
    val isExecuting by viewModel.terminalEngine.isExecuting.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(12.dp)
            .testTag("cloud_terminal_view")
    ) {
        // Top Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⌨️ ${terminalMode.label} [${terminalMode.host}]",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { viewModel.terminalEngine.clearTerminal() },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
            }
        }

        // Terminal Output Log
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0B0F19), RoundedCornerShape(8.dp))
                .padding(8.dp),
            reverseLayout = false
        ) {
            items(entries) { entry ->
                val color = when (entry.type) {
                    OutputType.INPUT -> Color(0xFF38BDF8)
                    OutputType.STDOUT -> Color(0xFFE2E8F0)
                    OutputType.STDERR -> Color(0xFFEF4444)
                    OutputType.SYSTEM -> Color(0xFF10B981)
                    OutputType.SUCCESS -> Color(0xFF34D399)
                    OutputType.WARNING -> Color(0xFFFBBF24)
                    else -> Color(0xFF94A3B8)
                }
                Text(
                    text = entry.text,
                    color = color,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Command Prompt Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$ ",
                color = Color(0xFF38BDF8),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            TextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = { Text("Enter shell command (e.g. ls, cat, test)...", fontSize = 12.sp, color = Color(0xFF64748B)) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (commandInput.isNotBlank() && !isExecuting) {
                        val cmd = commandInput
                        commandInput = ""
                        coroutineScope.launch {
                            viewModel.terminalEngine.executeCommand(cmd)
                        }
                    }
                })
            )
            IconButton(
                onClick = {
                    if (commandInput.isNotBlank() && !isExecuting) {
                        val cmd = commandInput
                        commandInput = ""
                        coroutineScope.launch {
                            viewModel.terminalEngine.executeCommand(cmd)
                        }
                    }
                }
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Execute", tint = Color(0xFF38BDF8))
            }
        }
    }
}
