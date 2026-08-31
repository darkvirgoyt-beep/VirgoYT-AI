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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInBrowser
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
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

    var selectedTab by remember { mutableIntStateOf(0) }
    var inputAuthCode by remember { mutableStateOf("") }
    var isSimulatingBrowserAuth by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { viewModel.closeGitHubAuthDialog() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
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
                                .size(32.dp)
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
                                text = "GitHub Connection Center",
                                color = ManusWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Terminal Device OAuth & Web Integration",
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
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManusIndigoBg)
                            .border(1.dp, ManusIndigo, RoundedCornerShape(8.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(ManusSlate700),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.take(2).uppercase(),
                                            color = ManusWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
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
                                                    text = "CONNECTED",
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
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Disconnect", color = ManusSlate400, fontSize = 11.sp)
                                }
                            }

                            // Synced Repositories
                            Text(
                                text = "Synced Repositories (${repos.size}):",
                                color = ManusSlate200,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                repos.forEach { repo ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ManusSlate850)
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = repo.fullName,
                                                color = ManusIndigoLight,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = repo.description,
                                                color = ManusSlate400,
                                                fontSize = 10.sp,
                                                maxLines = 1
                                            )
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = ManusCyan, modifier = Modifier.size(12.dp))
                                            Text(text = "${repo.stars}", color = ManusSlate400, fontSize = 10.sp)
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
                                    Text("Terminal Auth (Code)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("Direct Web-to-Web", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )
                    }

                    if (selectedTab == 0) {
                        // ==========================================
                        // Flow 1: Device Auth Flow (Terminal / Windows / Mac / Linux / Termux)
                        // ==========================================
                        val currentAuth = pendingAuth ?: viewModel.startGitHubDeviceAuth()

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "1. Enter your one-time user authorization code:",
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
                                            text = "USER VERIFICATION CODE",
                                            color = ManusSlate500,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Text(
                                            text = currentAuth.userCode,
                                            color = ManusCyan,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 2.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(currentAuth.userCode))
                                            viewModel.showToast("✓ Copied code: ${currentAuth.userCode}")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Text("Copy Code", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Step 2: Open Auth Link in Browser
                            Text(
                                text = "2. Authorize in Browser Sandbox:",
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
                                        isSimulatingBrowserAuth = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusSlate800),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f).height(40.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = ManusIndigoLight, modifier = Modifier.size(16.dp))
                                        Text("Open github.com/login/device", color = ManusWhite, fontSize = 11.sp)
                                    }
                                }
                            }

                            // Browser Authorization Simulation Card
                            if (isSimulatingBrowserAuth) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ManusIndigoBg)
                                        .border(1.dp, ManusIndigoLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ManusGreen, modifier = Modifier.size(16.dp))
                                            Text("GitHub OAuth: Authorize Manus Cloud PC", color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = "Application 'Manus Cloud Terminal' requested access to your repositories, gists, and user profile.",
                                            color = ManusSlate200,
                                            fontSize = 11.sp
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.verifyGitHubAuthCode(currentAuth.userCode)
                                                    isSimulatingBrowserAuth = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = ManusGreen),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("Authorize & Connect", color = ManusSlate950, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            // Step 3: Enter Auth Code or Token from Web
                            Text(
                                text = "3. Or paste verification code/token to connect terminal:",
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
                                    placeholder = { Text("e.g. ${currentAuth.userCode} or gho_...", color = ManusSlate500, fontSize = 11.sp) },
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
                                    colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(44.dp).testTag("github_verify_button")
                                ) {
                                    Text("Connect", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // ==========================================
                        // Flow 2: Direct Web-to-Web GitHub Auth
                        // ==========================================
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Connect GitHub directly with 1-click Web OAuth authentication.",
                                color = ManusSlate300,
                                fontSize = 12.sp
                            )

                            Button(
                                onClick = { viewModel.connectGitHubDirectWeb() },
                                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("github_direct_web_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Language, contentDescription = null, tint = ManusWhite, modifier = Modifier.size(16.dp))
                                    Text("Direct Web Authorize with GitHub", color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "✓ Sets up git credentials and syncs your repositories across both Cloud VM and Localhost daemon.",
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
