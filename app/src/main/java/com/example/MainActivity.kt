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
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.manus.ui.components.AppCreationHubView
import com.example.manus.ui.components.AuthDialog
import com.example.manus.ui.components.BrowserSandboxView
import com.example.manus.ui.components.CliInstallerDialog
import com.example.manus.ui.components.CloudPcHeader
import com.example.manus.ui.components.CloudStorageView
import com.example.manus.ui.components.CloudTerminalView
import com.example.manus.ui.components.CodeEditorView
import com.example.manus.ui.components.DatabaseAiStudioView
import com.example.manus.ui.components.FileManagerView
import com.example.manus.ui.components.GameStudioView
import com.example.manus.ui.components.GitHubAuthDialog
import com.example.manus.ui.components.LiveComputerDiffView
import com.example.manus.ui.components.ManusAgentView
import com.example.manus.ui.components.MemoryRagView
import com.example.manus.ui.components.PluginsAndToolsView
import com.example.manus.ui.components.ProjectUnderstandingView
import com.example.manus.ui.components.SecretCredentialBoxDialog
import com.example.manus.ui.components.SystemMonitorView
import com.example.manus.ui.components.UniversalPromptBar
import com.example.manus.ui.components.UserProfileDialog
import com.example.manus.ui.components.VoiceAssistantView
import com.example.manus.ui.components.WebDashboardView
import com.example.manus.ui.components.WorkflowAutomationView
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SleekBorder

class MainActivity : ComponentActivity() {

    private val viewModel: ManusCloudViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = themeMode) {
                ManusCloudApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ManusCloudApp(viewModel: ManusCloudViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val systemStats by viewModel.systemStats.collectAsState()
    val isAgentBusy by viewModel.agentService.isAgentBusy.collectAsState()
    val statusToast by viewModel.statusToast.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("virgoyt_cloud_app_root"),
        containerColor = if (themeMode.isDark) ManusSlate950 else com.example.ui.theme.EngineerLightCanvas
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(if (themeMode.isDark) ManusSlate950 else com.example.ui.theme.EngineerLightCanvas)
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
                                ManusAgentView(viewModel = viewModel)
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
            if (isUserProfileDialogOpen) {
                UserProfileDialog(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeUserProfileDialog() }
                )
            }

            // Authentication Modal Dialog
            AuthDialog(viewModel = viewModel)

            // GitHub Authentication Modal Dialog
            GitHubAuthDialog(viewModel = viewModel)

            // Multi-Platform Terminal CLI Installer Modal
            CliInstallerDialog(viewModel = viewModel)

            // Secret Credential Vault Modal Dialog
            SecretCredentialBoxDialog(viewModel = viewModel)

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

