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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.manus.data.github.GitHubRepo
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
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

@Composable
fun GitHubAuthDialog(viewModel: ManusCloudViewModel) {
    val isOpen by viewModel.isGitHubAuthDialogOpen.collectAsState()
    if (!isOpen) return

    val isConnected by viewModel.isGitHubConnected.collectAsState()
    val ghUser by viewModel.gitHubUser.collectAsState()
    val pendingAuth by viewModel.gitHubPendingAuth.collectAsState()
    val repos by viewModel.gitHubRepos.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var inputAuthCode by remember { mutableStateOf("") }
    var expandedRepoName by remember { mutableStateOf<String?>(null) }
    var newRepoNameInput by remember { mutableStateOf("") }
    var showAddRepoField by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { viewModel.closeGitHubAuthDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(14.dp))
                .testTag("github_auth_dialog"),
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
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ManusSlate800)
                                .border(1.dp, ManusIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "GitHub",
                                tint = ManusWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "GitHub Enterprise Connection",
                                color = ManusWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Autonomous Repository Swarm & CI/CD Pipeline",
                                color = ManusSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.closeGitHubAuthDialog() },
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

                if (isConnected && ghUser != null) {
                    // Connected User Profile View
                    val user = ghUser!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ManusIndigoBg)
                            .border(1.dp, ManusIndigo, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(ManusSlate700)
                                            .border(1.5.dp, ManusEmerald, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.take(2).uppercase(),
                                            color = ManusWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "@${user.username}",
                                                color = ManusWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(ManusEmerald.copy(alpha = 0.2f))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "AUTHORIZED",
                                                    color = ManusEmerald,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = user.email,
                                            color = ManusSlate400,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { viewModel.disconnectGitHub() },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusSlate800),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Disconnect", color = ManusSlate400, fontSize = 11.sp)
                                }
                            }

                            // Synced Repositories Header with Add Repo Trigger
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Connected Repositories (${repos.size}):",
                                    color = ManusSlate200,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                TextButton(
                                    onClick = { showAddRepoField = !showAddRepoField },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = ManusIndigoLight, modifier = Modifier.size(14.dp))
                                        Text("Add Repo", color = ManusIndigoLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Add New Repo Field
                            if (showAddRepoField) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = newRepoNameInput,
                                        onValueChange = { newRepoNameInput = it },
                                        placeholder = { Text("e.g. mobile-ai-studio", color = ManusSlate500, fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        textStyle = TextStyle(color = ManusWhite, fontSize = 11.5.sp, fontFamily = FontFamily.Monospace),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = ManusIndigo,
                                            unfocusedBorderColor = SleekBorder,
                                            focusedContainerColor = ManusSlate950,
                                            unfocusedContainerColor = ManusSlate950
                                        )
                                    )
                                    Button(
                                        onClick = {
                                            if (newRepoNameInput.isNotBlank()) {
                                                viewModel.githubManager.addRepo(newRepoNameInput.trim())
                                                viewModel.showToast("✓ Synced repository ${newRepoNameInput.trim()}")
                                                newRepoNameInput = ""
                                                showAddRepoField = false
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(44.dp)
                                    ) {
                                        Text("Sync", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Repositories List with Interactive Actions
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                repos.forEach { repo ->
                                    val isExpanded = expandedRepoName == repo.fullName
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ManusSlate850)
                                            .border(1.dp, if (isExpanded) ManusIndigo else SleekBorder, RoundedCornerShape(8.dp))
                                            .clickable {
                                                expandedRepoName = if (isExpanded) null else repo.fullName
                                            }
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text(
                                                        text = repo.fullName,
                                                        color = ManusIndigoLight,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(ManusSlate800)
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text(text = repo.language, color = ManusCyan, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                                    }
                                                }
                                                Text(
                                                    text = repo.description,
                                                    color = ManusSlate400,
                                                    fontSize = 10.5.sp,
                                                    maxLines = if (isExpanded) 3 else 1
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Star, contentDescription = null, tint = ManusAmber, modifier = Modifier.size(13.dp))
                                                Text(text = "${repo.stars}", color = ManusSlate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Expanded Repository Action Bar
                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "Select Autonomous AI Action for this Repository:",
                                                    color = ManusSlate300,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    RepoActionChip(
                                                        icon = Icons.Default.QuestionAnswer,
                                                        label = "Ask AI",
                                                        tint = ManusCyan,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        viewModel.closeGitHubAuthDialog()
                                                        viewModel.selectTab(ActiveWorkspaceTab.AGENT)
                                                        viewModel.dispatchUniversalAutonomousPrompt(
                                                            promptText = "Analyze repository ${repo.fullName}. Explain the codebase architecture, entry points, sub-agent capabilities, and provide an overview of the key modules.",
                                                            attachments = emptyList()
                                                        )
                                                    }

                                                    RepoActionChip(
                                                        icon = Icons.Default.BugReport,
                                                        label = "Fix Errors",
                                                        tint = ManusAmber,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        viewModel.closeGitHubAuthDialog()
                                                        viewModel.selectTab(ActiveWorkspaceTab.AGENT)
                                                        viewModel.dispatchUniversalAutonomousPrompt(
                                                            promptText = "Audit repository ${repo.fullName} for CI/CD failures, signing keystores, Gradle build errors, and missing dependencies. Automatically repair any bugs.",
                                                            attachments = emptyList()
                                                        )
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    RepoActionChip(
                                                        icon = Icons.Default.Edit,
                                                        label = "Edit & Commit",
                                                        tint = ManusIndigoLight,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        viewModel.closeGitHubAuthDialog()
                                                        viewModel.selectTab(ActiveWorkspaceTab.LIVE_COMPUTER)
                                                        viewModel.showToast("Opening ${repo.name} files in Live Code Editor & Diff Viewer")
                                                    }

                                                    RepoActionChip(
                                                        icon = Icons.Default.PlayArrow,
                                                        label = "Run Tests & Build",
                                                        tint = ManusEmerald,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        viewModel.closeGitHubAuthDialog()
                                                        viewModel.selectTab(ActiveWorkspaceTab.TERMINAL)
                                                        viewModel.executeTerminalCommand("./gradlew testDebugUnitTest assembleDebug")
                                                    }

                                                    RepoActionChip(
                                                        icon = Icons.Default.OpenInBrowser,
                                                        label = "Open Web",
                                                        tint = ManusWhite,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        uriHandler.openUri("https://github.com/${repo.fullName}")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Not connected: Mode Tabs (Device Code Flow vs Direct Web Flow)
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = ManusSlate850,
                        contentColor = ManusWhite,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = ManusIndigo
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("Browser Device Auth", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("Direct Web OAuth", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }

                    if (selectedTab == 0) {
                        // ==========================================
                        // Flow 1: Device Auth Flow (Terminal / Browser redirect)
                        // ==========================================
                        val currentAuth = pendingAuth ?: viewModel.startGitHubDeviceAuth()

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "1. Your GitHub User Verification Code:",
                                color = ManusSlate200,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Big User Code Display Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ManusSlate950)
                                    .border(1.dp, ManusIndigo, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "ONE-TIME ACTIVATION CODE",
                                            color = ManusSlate500,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = currentAuth.userCode,
                                            color = ManusCyan,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 2.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(currentAuth.userCode))
                                            viewModel.showToast("✓ Copied verification code: ${currentAuth.userCode}")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Step 2: Open Auth Link in Browser
                            Text(
                                text = "2. Open GitHub Authorization Page in Browser:",
                                color = ManusSlate200,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentAuth.userCode))
                                        viewModel.showToast("✓ Code copied! Redirecting to github.com/login/device...")
                                        try {
                                            uriHandler.openUri("https://github.com/login/device")
                                        } catch (e: Exception) {
                                            viewModel.showToast("Browser opened: github.com/login/device")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("open_github_browser_btn")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = ManusWhite, modifier = Modifier.size(18.dp))
                                        Text("Redirect to Browser (github.com/login/device)", color = ManusWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Step 3: Verify & Authenticate
                            Text(
                                text = "3. Confirm Activation & Connect Repositories:",
                                color = ManusSlate200,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inputAuthCode,
                                    onValueChange = { inputAuthCode = it },
                                    placeholder = { Text("Code: ${currentAuth.userCode} or Token", color = ManusSlate500, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).height(44.dp).testTag("github_code_input"),
                                    textStyle = TextStyle(color = ManusWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ManusIndigo,
                                        unfocusedBorderColor = SleekBorder,
                                        focusedContainerColor = ManusSlate850,
                                        unfocusedContainerColor = ManusSlate850
                                    )
                                )

                                Button(
                                    onClick = {
                                        val codeToUse = inputAuthCode.ifBlank { currentAuth.userCode }
                                        viewModel.verifyGitHubAuthCode(codeToUse)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(44.dp).testTag("github_verify_button")
                                ) {
                                    Text("Authenticate", color = ManusSlate950, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // ==========================================
                        // Flow 2: Direct Web-to-Web GitHub Auth
                        // ==========================================
                        var directUsernameInput by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Connect any GitHub account directly with 1-click Web OAuth or your username.",
                                color = ManusSlate300,
                                fontSize = 12.sp
                            )

                            OutlinedTextField(
                                value = directUsernameInput,
                                onValueChange = { directUsernameInput = it },
                                placeholder = { Text("GitHub Username (e.g. torvalds or your username)", color = ManusSlate500, fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                textStyle = TextStyle(color = ManusWhite, fontSize = 11.5.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigo,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedContainerColor = ManusSlate950,
                                    unfocusedContainerColor = ManusSlate950
                                )
                            )

                            Button(
                                onClick = {
                                    val currentUsername = if (directUsernameInput.isNotBlank()) directUsernameInput.trim() else (ghUser?.username ?: "developer")
                                    viewModel.githubManager.connectDirectWeb(currentUsername)
                                    viewModel.showToast("✓ Connected GitHub account @$currentUsername")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("github_direct_web_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = ManusWhite, modifier = Modifier.size(16.dp))
                                    Text("Authorize GitHub Account", color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "✓ Sets up git credentials and syncs your personal repositories across both Cloud VM and Localhost daemon.",
                                color = ManusSlate400,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoActionChip(
    icon: ImageVector,
    label: String,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ManusSlate800)
            .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(13.dp))
            Text(text = label, color = ManusWhite, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

