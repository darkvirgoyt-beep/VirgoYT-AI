package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VirgoTheme
import com.example.virgoyt.data.model.ActiveWorkspaceTab
import com.example.virgoyt.ui.VirgoCloudViewModel
import com.example.virgoyt.ui.components.AppCreationHubView
import com.example.virgoyt.ui.components.AuthDialog
import com.example.virgoyt.ui.components.BrowserSandboxView
import com.example.virgoyt.ui.components.CliInstallerDialog
import com.example.virgoyt.ui.components.CloudPcHeader
import com.example.virgoyt.ui.components.CloudStorageView
import com.example.virgoyt.ui.components.CloudTerminalView
import com.example.virgoyt.ui.components.CodeEditorView
import com.example.virgoyt.ui.components.DatabaseAiStudioView
import com.example.virgoyt.ui.components.FileManagerView
import com.example.virgoyt.ui.components.GameStudioView
import com.example.virgoyt.ui.components.GitHubAuthDialog
import com.example.virgoyt.ui.components.LiveComputerDiffView
import com.example.virgoyt.ui.components.VirgoAgentView
import com.example.virgoyt.ui.components.MemoryRagView
import com.example.virgoyt.ui.components.PluginsAndToolsView
import com.example.virgoyt.ui.components.ProjectUnderstandingView
import com.example.virgoyt.ui.components.SecretCredentialBoxDialog
import com.example.virgoyt.ui.components.SystemMonitorView
import com.example.virgoyt.ui.components.UniversalPromptBar
import com.example.virgoyt.ui.components.UserProfileDialog
import com.example.virgoyt.ui.components.VoiceAssistantView
import com.example.virgoyt.ui.components.WebDashboardView
import com.example.virgoyt.ui.components.WorkflowAutomationView
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SleekBorder

class MainActivity : ComponentActivity() {

    private val viewModel: VirgoCloudViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = themeMode) {
                VirgoCloudApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VirgoCloudApp(viewModel: VirgoCloudViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val systemStats by viewModel.systemStats.collectAsState()
    val isAgentBusy by viewModel.agentService.isAgentBusy.collectAsState()
    val statusToast by viewModel.statusToast.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("virgoyt_cloud_app_root"),
        containerColor = VirgoTheme.colors.canvas
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(VirgoTheme.colors.canvas)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cloud PC Header Bar & Navigation
                CloudPcHeader(
                    viewModel = viewModel,
                    activeTab = activeTab,
                    systemStats = systemStats,
                    isAgentBusy = isAgentBusy
                )

                // Tab Content Switcher with smooth crossfade
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TabContent"
                    ) { targetTab ->
                        when (targetTab) {
                            ActiveWorkspaceTab.AGENT -> {
                                VirgoAgentView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.VOICE_ASSISTANT -> {
                                VoiceAssistantView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.WEB_DASHBOARD -> {
                                WebDashboardView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.PLUGINS_TOOLS -> {
                                PluginsAndToolsView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.WORKFLOWS -> {
                                WorkflowAutomationView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.PROJECT_SCAN -> {
                                ProjectUnderstandingView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.DATABASE_AI -> {
                                DatabaseAiStudioView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.APP_GEN -> {
                                AppCreationHubView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.MEMORY_RAG -> {
                                MemoryRagView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.CLOUD_STORAGE -> {
                                CloudStorageView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.LIVE_COMPUTER -> {
                                LiveComputerDiffView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.GAME_STUDIO -> {
                                GameStudioView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.TERMINAL -> {
                                CloudTerminalView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.FILES -> {
                                FileManagerView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.EDITOR -> {
                                CodeEditorView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.BROWSER -> {
                                BrowserSandboxView(viewModel = viewModel)
                            }
                            ActiveWorkspaceTab.MONITOR -> {
                                SystemMonitorView(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Docked Universal Message Box at Bottom
                UniversalPromptBar(viewModel = viewModel)
            }

            // User Profile & Preferences Dialog
            val isUserProfileDialogOpen by viewModel.isUserProfileDialogOpen.collectAsState()
            UserProfileDialog(
                isOpen = isUserProfileDialogOpen,
                viewModel = viewModel,
                onDismiss = { viewModel.closeUserProfileDialog() }
            )

            // Authentication Modal Dialog
            val isAuthDialogOpen by viewModel.isAuthDialogOpen.collectAsState()
            AuthDialog(
                isOpen = isAuthDialogOpen,
                viewModel = viewModel,
                onDismiss = { viewModel.closeAuthDialog() }
            )

            // GitHub Authentication Modal Dialog
            val isGitHubAuthDialogOpen by viewModel.isGitHubAuthDialogOpen.collectAsState()
            GitHubAuthDialog(
                isOpen = isGitHubAuthDialogOpen,
                viewModel = viewModel,
                onDismiss = { viewModel.closeGitHubAuthDialog() }
            )

            // Multi-Platform Terminal CLI Installer Modal
            val isCliDialogOpen by viewModel.isCliDialogOpen.collectAsState()
            CliInstallerDialog(
                isOpen = isCliDialogOpen,
                viewModel = viewModel,
                onDismiss = { viewModel.closeCliDialog() }
            )

            // Secret Credential Vault Modal Dialog
            val isSecretBoxDialogOpen by viewModel.isSecretBoxDialogOpen.collectAsState()
            SecretCredentialBoxDialog(
                isOpen = isSecretBoxDialogOpen,
                viewModel = viewModel,
                onDismiss = { viewModel.closeSecretBoxDialog() }
            )

            // Floating Toast / Notification Banner
            if (statusToast != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 76.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusSlate900.copy(alpha = 0.95f))
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = statusToast!!,
                        color = ManusIndigoLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

