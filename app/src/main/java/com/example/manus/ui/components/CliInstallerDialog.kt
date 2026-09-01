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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.CliPlatformInstall
import com.example.manus.data.model.TerminalMode
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusIndigoSoft
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder

@Composable
fun CliInstallerDialog(viewModel: ManusCloudViewModel) {
    val isOpen by viewModel.isCliInstallerDialogOpen.collectAsState()
    if (!isOpen) return

    val currentMode by viewModel.terminalMode.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    var selectedPlatformIndex by remember { mutableIntStateOf(0) }

    val platforms = listOf(
        CliPlatformInstall(
            platformName = "Linux",
            osBadge = "🐧 Linux (Ubuntu / Debian / Arch)",
            installCommand = "curl -fsSL https://virgoyt.cloud/install.sh | bash",
            runCommand = "virgoyt login && virgoyt start --port 8080",
            description = "Native x86_64 and aarch64 binary with systemd service integration and isolated cgroups sandbox.",
            requirements = "curl, bash, sudo (optional for systemd)"
        ),
        CliPlatformInstall(
            platformName = "macOS",
            osBadge = "🍎 macOS (Apple Silicon / Intel)",
            installCommand = "brew install virgoyt-ai/tap/virgoyt-cli",
            runCommand = "virgoyt start",
            description = "Homebrew formula and universal binary compiled for arm64 & x86_64 Darwin kernels.",
            requirements = "Homebrew / curl, zsh or bash"
        ),
        CliPlatformInstall(
            platformName = "Windows",
            osBadge = "🪟 Windows (PowerShell / WSL2 / Cmd)",
            installCommand = "powershell -c \"irm https://virgoyt.cloud/install.ps1 | iex\"",
            runCommand = "virgoyt.exe start",
            description = "Native Windows terminal CLI with WSL2 bridge and ConPTY pseudo-console support.",
            requirements = "PowerShell 5.1+ or Windows Terminal / WSL2"
        ),
        CliPlatformInstall(
            platformName = "Termux",
            osBadge = "📱 Android (Termux Terminal)",
            installCommand = "pkg update && pkg install -y git python curl openssh nodejs && curl -fsSL https://virgoyt.cloud/install-termux.sh | bash",
            runCommand = "virgoyt-termux --host 127.0.0.1:8080",
            description = "Termux package for on-device Android terminal execution and localhost bridge server.",
            requirements = "Termux app (F-Droid / GitHub releases)"
        )
    )

    val currentPlatform = platforms[selectedPlatformIndex]

    Dialog(
        onDismissRequest = { viewModel.closeCliInstallerDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .testTag("cli_installer_dialog"),
            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Bar
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ManusIndigoBg)
                                .border(1.dp, ManusIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = "CLI Installer",
                                tint = ManusIndigoLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "VirgoYT Terminal CLI Installer",
                                color = ManusWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Install & run terminal across all platforms",
                                color = ManusSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.closeCliInstallerDialog() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ManusSlate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Platform Selector Tabs
                TabRow(
                    selectedTabIndex = selectedPlatformIndex,
                    containerColor = ManusSlate850,
                    contentColor = ManusWhite,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedPlatformIndex]),
                            color = ManusIndigo
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    platforms.forEachIndexed { index, p ->
                        Tab(
                            selected = selectedPlatformIndex == index,
                            onClick = { selectedPlatformIndex = index },
                            text = {
                                Text(
                                    text = p.platformName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    }
                }

                // Current Platform Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusSlate950)
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = currentPlatform.osBadge,
                            color = ManusIndigoLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentPlatform.description,
                            color = ManusSlate400,
                            fontSize = 11.sp
                        )

                        // 1-Line Install Command Box
                        Text(
                            text = "1-LINE INSTALLATION COMMAND:",
                            color = ManusSlate500,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManusSlate850)
                                .border(1.dp, ManusIndigo.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentPlatform.installCommand,
                                    color = ManusCyan,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentPlatform.installCommand))
                                        viewModel.showToast("✓ Copied install command")
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ManusSlate400, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Run Command Box
                        Text(
                            text = "START / RUN COMMAND:",
                            color = ManusSlate500,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(ManusSlate850)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = currentPlatform.runCommand,
                                    color = ManusGreen,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentPlatform.runCommand))
                                        viewModel.showToast("✓ Copied start command")
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = ManusSlate400, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Text(
                            text = "Requirements: ${currentPlatform.requirements}",
                            color = ManusSlate500,
                            fontSize = 10.sp
                        )
                    }
                }

                // Execution Mode Switcher Section (Cloud VM vs Localhost)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusIndigoBg)
                        .border(1.dp, ManusIndigo, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Terminal Host Execution Options",
                            color = ManusWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "You can run commands directly on the Cloud VM sandbox or connect the terminal to your local machine (localhost daemon).",
                            color = ManusSlate200,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.switchTerminalMode(TerminalMode.CLOUD_VM) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentMode == TerminalMode.CLOUD_VM) ManusIndigo else ManusSlate800
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("☁️ Cloud VM (Default)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.switchTerminalMode(TerminalMode.LOCALHOST) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (currentMode == TerminalMode.LOCALHOST) ManusIndigo else ManusSlate800
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Text("💻 Localhost :8080", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
