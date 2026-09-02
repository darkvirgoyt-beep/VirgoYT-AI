package com.example.virgoyt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.virgoyt.data.agent.CursorAiAssistantService
import com.example.virgoyt.data.agent.MultiAgentDevelopmentTeam
import com.example.virgoyt.data.agent.VirgoAgentService
import com.example.virgoyt.data.appgen.ApplicationGenerationEngine
import com.example.virgoyt.data.auth.AuthManager
import com.example.virgoyt.data.cloud.CloudStorageEngine
import com.example.virgoyt.data.database.DatabaseAiEngine
import com.example.virgoyt.data.game.GameStudioEngine
import com.example.virgoyt.data.github.GitHubAuthManager
import com.example.virgoyt.data.model.ActiveWorkspaceTab
import com.example.virgoyt.data.model.AppThemeMode
import com.example.virgoyt.data.model.PromptAttachment
import com.example.virgoyt.data.model.SystemStats
import com.example.virgoyt.data.plugins.PluginManager
import com.example.virgoyt.data.preferences.UserPreferenceEngine
import com.example.virgoyt.data.project.ProjectUnderstandingEngine
import com.example.virgoyt.data.rag.MemoryAndRagEngine
import com.example.virgoyt.data.terminal.TerminalEngine
import com.example.virgoyt.data.vfs.VirtualFileSystem
import com.example.virgoyt.data.voice.VoiceAssistantEngine
import com.example.virgoyt.data.workflows.WorkflowAutomationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VirgoCloudViewModel : ViewModel() {

    val vfs = VirtualFileSystem()
    val authManager = AuthManager(vfs)
    val githubManager = GitHubAuthManager()
    val terminalEngine = TerminalEngine(vfs, authManager, githubManager)
    val preferenceEngine = UserPreferenceEngine()
    val ragEngine = MemoryAndRagEngine()
    val pluginManager = PluginManager()
    val voiceEngine = VoiceAssistantEngine()
    val dbEngine = DatabaseAiEngine()
    val workflowEngine = WorkflowAutomationEngine()
    val projectEngine = ProjectUnderstandingEngine()
    val gameEngine = GameStudioEngine()
    val cloudStorageEngine = CloudStorageEngine()
    val appGenEngine = ApplicationGenerationEngine()
    val multiAgentTeam = MultiAgentDevelopmentTeam()
    val cursorAiService = CursorAiAssistantService()

    val agentService = VirgoAgentService(
        vfs = vfs,
        multiAgentTeam = multiAgentTeam,
        cursorAiService = cursorAiService
    )
    val routerEngine = agentService.routerEngine

    val themeMode: StateFlow<AppThemeMode> = preferenceEngine.themeMode

    private val _activeTab = MutableStateFlow(ActiveWorkspaceTab.AGENT)
    val activeTab: StateFlow<ActiveWorkspaceTab> = _activeTab.asStateFlow()

    private val _systemStats = MutableStateFlow(SystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    private val _statusToast = MutableStateFlow<String?>(null)
    val statusToast: StateFlow<String?> = _statusToast.asStateFlow()

    // Dialog visibility states
    private val _isUserProfileDialogOpen = MutableStateFlow(false)
    val isUserProfileDialogOpen: StateFlow<Boolean> = _isUserProfileDialogOpen.asStateFlow()

    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    private val _isGitHubAuthDialogOpen = MutableStateFlow(false)
    val isGitHubAuthDialogOpen: StateFlow<Boolean> = _isGitHubAuthDialogOpen.asStateFlow()

    private val _isCliDialogOpen = MutableStateFlow(false)
    val isCliDialogOpen: StateFlow<Boolean> = _isCliDialogOpen.asStateFlow()

    private val _isSecretBoxDialogOpen = MutableStateFlow(false)
    val isSecretBoxDialogOpen: StateFlow<Boolean> = _isSecretBoxDialogOpen.asStateFlow()

    fun selectTab(tab: ActiveWorkspaceTab) {
        _activeTab.value = tab
    }

    fun runTerminalCommand(command: String) {
        viewModelScope.launch {
            terminalEngine.executeCommand(command)
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _statusToast.value = message
            delay(3000)
            if (_statusToast.value == message) {
                _statusToast.value = null
            }
        }
    }

    fun openUserProfileDialog() { _isUserProfileDialogOpen.value = true }
    fun closeUserProfileDialog() { _isUserProfileDialogOpen.value = false }

    fun openAuthDialog() { _isAuthDialogOpen.value = true }
    fun closeAuthDialog() { _isAuthDialogOpen.value = false }

    fun openGitHubAuthDialog() { _isGitHubAuthDialogOpen.value = true }
    fun closeGitHubAuthDialog() { _isGitHubAuthDialogOpen.value = false }

    fun openCliDialog() { _isCliDialogOpen.value = true }
    fun closeCliDialog() { _isCliDialogOpen.value = false }

    fun openSecretBoxDialog() { _isSecretBoxDialogOpen.value = true }
    fun closeSecretBoxDialog() { _isSecretBoxDialogOpen.value = false }

    fun executePrompt(prompt: String, attachments: List<PromptAttachment> = emptyList()) {
        agentService.runGoal(prompt, attachments)
    }
}
