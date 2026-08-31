package com.example.manus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manus.data.agent.CursorAiAssistantService
import com.example.manus.data.agent.CursorAiResult
import com.example.manus.data.agent.ManusAgentService
import com.example.manus.data.agent.MultiAgentDevelopmentTeam
import com.example.manus.data.appgen.ApplicationGenerationEngine
import com.example.manus.data.auth.AuthManager
import com.example.manus.data.database.DatabaseAiEngine
import com.example.manus.data.game.GameStudioEngine
import com.example.manus.data.github.GitHubAuthManager
import com.example.manus.data.github.GitHubDeviceAuth
import com.example.manus.data.github.GitHubRepo
import com.example.manus.data.github.GitHubUser
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.AiModelTier
import com.example.manus.data.model.AttachmentType
import com.example.manus.data.model.AuthSession
import com.example.manus.data.model.BrowserConsoleMessage
import com.example.manus.data.model.ChatMessage
import com.example.manus.data.model.GameEngineType
import com.example.manus.data.model.ProcessInfo
import com.example.manus.data.model.PromptAttachment
import com.example.manus.data.model.SecretCredentialPrompt
import com.example.manus.data.model.SystemStats
import com.example.manus.data.model.TerminalMode
import com.example.manus.data.model.User
import com.example.manus.data.model.VirtualFile
import com.example.manus.data.model.ModelRouterEngine
import com.example.manus.data.project.ProjectUnderstandingEngine
import com.example.manus.data.rag.MemoryAndRagEngine
import com.example.manus.data.terminal.TerminalEngine
import com.example.manus.data.vfs.VirtualFileSystem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManusCloudViewModel : ViewModel() {

    val authManager = AuthManager()
    val vfs = VirtualFileSystem()
    val githubManager = GitHubAuthManager()
    val terminal = TerminalEngine(vfs, authManager, githubManager)
    val agentService = ManusAgentService(vfs, terminal)
    val cursorService = CursorAiAssistantService(vfs)
    val gameStudioEngine = GameStudioEngine(vfs)
    val modelRouterEngine = ModelRouterEngine()
    val multiAgentTeam = MultiAgentDevelopmentTeam(vfs, viewModelScope)
    val memoryAndRagEngine = MemoryAndRagEngine()
    val projectUnderstandingEngine = ProjectUnderstandingEngine(vfs)
    val databaseAiEngine = DatabaseAiEngine()
    val applicationGenerationEngine = ApplicationGenerationEngine(vfs)

    // Current active view tab
    private val _activeTab = MutableStateFlow(ActiveWorkspaceTab.AGENT)
    val activeTab: StateFlow<ActiveWorkspaceTab> = _activeTab.asStateFlow()

    // Auth Dialog state
    private val _isAuthDialogOpen = MutableStateFlow(false)
    val isAuthDialogOpen: StateFlow<Boolean> = _isAuthDialogOpen.asStateFlow()

    val currentSession: StateFlow<AuthSession?> = authManager.currentSession

    // GitHub & CLI Installer Dialog States
    private val _isGitHubAuthDialogOpen = MutableStateFlow(false)
    val isGitHubAuthDialogOpen: StateFlow<Boolean> = _isGitHubAuthDialogOpen.asStateFlow()

    private val _isCliInstallerDialogOpen = MutableStateFlow(false)
    val isCliInstallerDialogOpen: StateFlow<Boolean> = _isCliInstallerDialogOpen.asStateFlow()

    // Secret Credential Box State
    private val _secretPromptState = MutableStateFlow<SecretCredentialPrompt?>(null)
    val secretPromptState: StateFlow<SecretCredentialPrompt?> = _secretPromptState.asStateFlow()

    val terminalMode: StateFlow<TerminalMode> = terminal.terminalMode
    val isGitHubConnected: StateFlow<Boolean> = githubManager.isConnected
    val gitHubUser: StateFlow<GitHubUser?> = githubManager.currentUser
    val gitHubPendingAuth: StateFlow<GitHubDeviceAuth?> = githubManager.pendingDeviceAuth
    val gitHubRepos: StateFlow<List<GitHubRepo>> = githubManager.userRepos

    // Cursor AI Editor States
    private val _isCursorAiEditorOpen = MutableStateFlow(false)
    val isCursorAiEditorOpen: StateFlow<Boolean> = _isCursorAiEditorOpen.asStateFlow()

    private val _isCursorAiGenerating = MutableStateFlow(false)
    val isCursorAiGenerating: StateFlow<Boolean> = _isCursorAiGenerating.asStateFlow()

    private val _cursorAiPendingResult = MutableStateFlow<CursorAiResult?>(null)
    val cursorAiPendingResult: StateFlow<CursorAiResult?> = _cursorAiPendingResult.asStateFlow()

    private val _cursorAiExplanationDialog = MutableStateFlow<String?>(null)
    val cursorAiExplanationDialog: StateFlow<String?> = _cursorAiExplanationDialog.asStateFlow()

    // Cursor AI Terminal States
    private val _isCursorTerminalAiOpen = MutableStateFlow(false)
    val isCursorTerminalAiOpen: StateFlow<Boolean> = _isCursorTerminalAiOpen.asStateFlow()

    private val _cursorTerminalSuggestedCommand = MutableStateFlow<String?>(null)
    val cursorTerminalSuggestedCommand: StateFlow<String?> = _cursorTerminalSuggestedCommand.asStateFlow()

    // Global Cursor AI Composer Dialog
    private val _isGlobalCursorComposerOpen = MutableStateFlow(false)
    val isGlobalCursorComposerOpen: StateFlow<Boolean> = _isGlobalCursorComposerOpen.asStateFlow()

    // File Manager Explorer Directory & Filter State
    private val _explorerCurrentDir = MutableStateFlow("/workspace")
    val explorerCurrentDir: StateFlow<String> = _explorerCurrentDir.asStateFlow()

    private val _fileSearchQuery = MutableStateFlow("")
    val fileSearchQuery: StateFlow<String> = _fileSearchQuery.asStateFlow()

    // Active file being edited in Code Editor
    private val _selectedFile = MutableStateFlow<VirtualFile?>(null)
    val selectedFile: StateFlow<VirtualFile?> = _selectedFile.asStateFlow()

    // Current editor text content
    private val _editorContent = MutableStateFlow("")
    val editorContent: StateFlow<String> = _editorContent.asStateFlow()

    private val _isEditorDirty = MutableStateFlow(false)
    val isEditorDirty: StateFlow<Boolean> = _isEditorDirty.asStateFlow()

    // Browser Sandbox Reload Counter (triggers WebView reload)
    private val _browserReloadTrigger = MutableStateFlow(0)
    val browserReloadTrigger: StateFlow<Int> = _browserReloadTrigger.asStateFlow()

    // Browser console messages
    private val _browserConsoleLogs = MutableStateFlow<List<BrowserConsoleMessage>>(emptyList())
    val browserConsoleLogs: StateFlow<List<BrowserConsoleMessage>> = _browserConsoleLogs.asStateFlow()

    // Live System Metrics
    private val _systemStats = MutableStateFlow(SystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    // Active Process Table
    private val _processes = MutableStateFlow(
        listOf(
            ProcessInfo(1042, "developer", 4.8f, 1.1f, "virgoyt-agent-engine"),
            ProcessInfo(1289, "developer", 24.2f, 4.5f, "unreal_engine_5.4-headless"),
            ProcessInfo(1350, "developer", 12.9f, 2.3f, "blender_4.2_render_worker"),
            ProcessInfo(1410, "developer", 3.4f, 0.8f, "google-earth-gis-dem-daemon")
        )
    )
    val processes: StateFlow<List<ProcessInfo>> = _processes.asStateFlow()

    // Terminal command input buffer
    private val _terminalInput = MutableStateFlow("")
    val terminalInput: StateFlow<String> = _terminalInput.asStateFlow()

    // Agent goal input buffer
    private val _agentInputGoal = MutableStateFlow("")
    val agentInputGoal: StateFlow<String> = _agentInputGoal.asStateFlow()

    // Toast / notification banner
    private val _statusToast = MutableStateFlow<String?>(null)
    val statusToast: StateFlow<String?> = _statusToast.asStateFlow()

    init {
        // Select initial file
        val defaultFile = vfs.getFile("/workspace/index.html")
        _selectedFile.value = defaultFile
        _editorContent.value = defaultFile?.content ?: ""

        // Start periodic hardware metric simulation
        startHardwareMonitorLoop()
    }

    private fun startHardwareMonitorLoop() {
        viewModelScope.launch {
            var uptime = 4820L
            while (true) {
                delay(2000)
                uptime += 2
                val isBusy = agentService.isAgentBusy.value || terminal.isExecuting.value || gameStudioEngine.isBuildingShaders.value
                val baseCpu = if (isBusy) (55..88).random().toFloat() else (12..24).random().toFloat()
                val baseGpu = if (isBusy) (72..96).random().toFloat() else (35..50).random().toFloat()
                val baseMem = if (isBusy) (4200..6800).random() else (3100..3600).random()
                _systemStats.value = _systemStats.value.copy(
                    cpuUsagePercent = baseCpu,
                    gpuUsagePercent = baseGpu,
                    memoryUsedMb = baseMem,
                    uptimeSeconds = uptime
                )
            }
        }
    }

    fun selectTab(tab: ActiveWorkspaceTab) {
        _activeTab.value = tab
    }

    fun openSecretBox(serviceName: String, reason: String) {
        _secretPromptState.value = SecretCredentialPrompt(
            serviceName = serviceName,
            promptReason = reason
        )
    }

    fun closeSecretBox() {
        _secretPromptState.value = null
    }

    fun submitSecretCredentials(serviceName: String, username: String, tokenOrPass: String) {
        val user = authManager.currentUser?.username ?: "developer"
        if (serviceName.contains("GitHub", ignoreCase = true)) {
            githubManager.authorizeWithTokenOrCode(tokenOrPass, user)
        }
        vfs.addFile("/home/$user/.credentials/${serviceName.lowercase().replace(" ", "_")}.enc", "ENCRYPTED_VAULT_RECORD: [User: $username, Token: SHA256_HASHED]", user)
        closeSecretBox()
        showToast("✓ $serviceName credentials saved to encrypted RAM vault")
    }

    fun startNewChatSession(title: String) {
        modelRouterEngine.createNewChatSession(title)
    }

    // Universal Autonomous Multi-Modal Prompt Dispatcher
    fun dispatchUniversalAutonomousPrompt(promptText: String, attachments: List<PromptAttachment>) {
        if (promptText.isBlank() && attachments.isEmpty()) return

        val (effectiveModel, routingExplanation) = modelRouterEngine.resolveEffectiveModelForPrompt(promptText, attachments)

        // 1. Append User Message
        val userMsg = ChatMessage(
            role = "user",
            content = promptText.ifBlank { "Execute task with attached media files" },
            modelUsed = effectiveModel,
            attachments = attachments
        )
        modelRouterEngine.appendMessageToCurrentSession(userMsg)

        // 2. Dispatch Task to Autonomous Subsystems
        viewModelScope.launch {
            showToast(routingExplanation)
            delay(400)

            val lower = promptText.lowercase()
            val has3d = attachments.any { it.type == AttachmentType.MODEL_3D }
            val hasGis = attachments.any { it.type == AttachmentType.GIS_COORDINATES }
            val hasVideo = attachments.any { it.type == AttachmentType.VIDEO }

            if (lower.contains("game") || lower.contains("unreal") || lower.contains("ue5") || lower.contains("ark") || lower.contains("bgmi") || lower.contains("palworld") || lower.contains("map") || has3d || hasGis) {
                // Game Studio & 3D / GIS Orchestration
                selectTab(ActiveWorkspaceTab.GAME_STUDIO)
                
                if (lower.contains("dinosaur") || lower.contains("ark")) {
                    gameStudioEngine.addGenerated3DModel(
                        modelName = "Spinosaurus_Predator_${(1..99).random()}",
                        category = "Apex Creature / Dinosaur",
                        format = "GLB",
                        description = "High-poly semi-aquatic predator with Nanite scales and procedural combat AI."
                    )
                } else if (lower.contains("palworld") || lower.contains("companion")) {
                    gameStudioEngine.addGenerated3DModel(
                        modelName = "Frost_Gryphon_Mount",
                        category = "Flight Companion",
                        format = "GLB",
                        description = "Mountable elemental companion with dynamic wing flap physics and stamina gliding."
                    )
                } else {
                    gameStudioEngine.addGenerated3DModel(
                        modelName = "Tactical_Bunker_Complex",
                        category = "Modular Architecture",
                        format = "GLB",
                        description = "Multi-level tactical stronghold with destructible barriers and PBR textures."
                    )
                }

                // Generate Google Earth elevation terrain if requested
                if (lower.contains("earth") || lower.contains("mountain") || lower.contains("snow") || lower.contains("desert") || hasGis) {
                    gameStudioEngine.generateNewTerrainFromGoogleEarth(
                        locationName = "Pochinki Highlands & Volcanic Glade",
                        lat = 36.1069,
                        lon = -112.1129,
                        biome = if (lower.contains("snow")) "Snow Peak & Pine Glades" else "Volcanic Jungle & Strongholds",
                        inspiration = "BGMI / Ark Survival / Palworld"
                    )
                }

                val assistantMsg = ChatMessage(
                    role = "assistant",
                    content = "🎮 **Unreal Engine 5 & 3D Game Studio Initialized**\n" +
                            "- **Engine**: Unreal Engine 5.4 with Nanite & Lumen Dynamic Lighting\n" +
                            "- **3D Mesh**: Generated GLB model assets with PBR shaders\n" +
                            "- **Google Earth GIS**: 0.25m LiDAR DEM terrain heightmap built\n" +
                            "- **C++ Game Logic**: `AVirgoYTCharacter` companion and movement mechanics compiled\n" +
                            "- **Executable**: Ready to launch in Windows / Linux Sandbox (`wine ./VirgoYTGame.exe`)",
                    modelUsed = effectiveModel
                )
                modelRouterEngine.appendMessageToCurrentSession(assistantMsg)
            } else if (lower.contains("diff") || lower.contains("computer") || lower.contains("editor") || lower.contains("code") || lower.contains("fix")) {
                // Switch to Live Cloud Computer Diff Viewer (matches screenshot)
                selectTab(ActiveWorkspaceTab.LIVE_COMPUTER)
                val assistantMsg = ChatMessage(
                    role = "assistant",
                    content = "💻 **VirgoYT AI Cloud Computer Code Changes Applied**\n" +
                            "- **File**: `server/_core/llm.ts`\n" +
                            "- **Diff Status**: Live preview ready. You can inspect Diff, Original, or Modified views, scrub timeline, or take Remote Desktop Control.",
                    modelUsed = effectiveModel
                )
                modelRouterEngine.appendMessageToCurrentSession(assistantMsg)
            } else {
                // Standard autonomous goal execution
                runAgentGoal(promptText)
                val assistantMsg = ChatMessage(
                    role = "assistant",
                    content = "🤖 **Autonomous Execution Completed** using ${effectiveModel.displayName}.\nAll sandbox processes synchronized.",
                    modelUsed = effectiveModel
                )
                modelRouterEngine.appendMessageToCurrentSession(assistantMsg)
            }
        }
    }

    fun openAuthDialog() {
        _isAuthDialogOpen.value = true
    }

    fun closeAuthDialog() {
        _isAuthDialogOpen.value = false
    }

    fun login(username: String, password: String) {
        val result = authManager.login(username, password)
        if (result.isSuccess) {
            val user = result.getOrNull()!!
            _explorerCurrentDir.value = user.homeDir
            showToast("✓ Welcome back, ${user.username}!")
            closeAuthDialog()
        } else {
            showToast("Login failed: ${result.exceptionOrNull()?.message}")
        }
    }

    fun signup(username: String, email: String, password: String) {
        val result = authManager.signup(username, email, password)
        if (result.isSuccess) {
            val user = result.getOrNull()!!
            vfs.createDirectory(user.homeDir, user.username)
            vfs.addFile("${user.homeDir}/welcome.txt", "Welcome ${user.username}!\nIsolated user environment initialized.\n", user.username)
            _explorerCurrentDir.value = user.homeDir
            showToast("✓ Account created for ${user.username}!")
            closeAuthDialog()
        } else {
            showToast("Registration failed: ${result.exceptionOrNull()?.message}")
        }
    }

    fun logout() {
        authManager.logout()
        _explorerCurrentDir.value = "/home/guest"
        showToast("Logged out. Switched to Guest sandbox.")
        closeAuthDialog()
    }

    fun switchUser(username: String) {
        if (authManager.switchUser(username)) {
            val user = authManager.currentUser
            _explorerCurrentDir.value = user?.homeDir ?: "/workspace"
            showToast("Switched user to $username")
            closeAuthDialog()
        }
    }

    fun setExplorerDir(path: String) {
        _explorerCurrentDir.value = path
    }

    fun setFileSearchQuery(query: String) {
        _fileSearchQuery.value = query
    }

    fun setTerminalInput(input: String) {
        _terminalInput.value = input
    }

    fun setAgentInputGoal(goal: String) {
        _agentInputGoal.value = goal
    }

    fun executeTerminalCommand(cmd: String? = null) {
        val commandToRun = cmd ?: _terminalInput.value
        if (commandToRun.isBlank()) return

        _terminalInput.value = ""
        viewModelScope.launch {
            terminal.executeCommand(commandToRun)
            if (commandToRun.contains("run.sh") || commandToRun.contains("npm") || commandToRun.contains("echo") || commandToRun.contains("cp") || commandToRun.contains("mv")) {
                reloadBrowserSandbox()
            }
        }
    }

    fun runAgentGoal(goalText: String? = null) {
        val goal = goalText ?: _agentInputGoal.value
        if (goal.isBlank()) return

        _agentInputGoal.value = ""
        viewModelScope.launch {
            agentService.executeUserGoal(goal) {
                reloadBrowserSandbox()
            }
        }
    }

    fun selectFile(file: VirtualFile) {
        if (file.isDirectory) {
            _explorerCurrentDir.value = file.path
            return
        }
        _selectedFile.value = file
        _editorContent.value = file.content
        _isEditorDirty.value = false
    }

    fun updateEditorContent(newContent: String) {
        _editorContent.value = newContent
        _isEditorDirty.value = true
    }

    fun saveCurrentFile() {
        val file = _selectedFile.value ?: return
        val activeUser = authManager.currentUser?.username ?: "developer"
        vfs.writeFile(file.path, _editorContent.value, activeUser)
        _selectedFile.value = vfs.getFile(file.path)
        _isEditorDirty.value = false
        showToast("✓ Saved ${file.name}")
        reloadBrowserSandbox()
    }

    fun createNewFile(path: String, content: String = "") {
        val activeUser = authManager.currentUser?.username ?: "developer"
        val fullPath = if (path.startsWith("/")) path else "${_explorerCurrentDir.value}/$path"
        vfs.writeFile(fullPath, content, activeUser)
        val newFile = vfs.getFile(fullPath)
        if (newFile != null) {
            selectFile(newFile)
            showToast("✓ Created ${newFile.name}")
        }
    }

    fun createNewDirectory(dirName: String) {
        val activeUser = authManager.currentUser?.username ?: "developer"
        val fullPath = if (dirName.startsWith("/")) dirName else "${_explorerCurrentDir.value}/$dirName"
        vfs.createDirectory(fullPath, activeUser)
        showToast("✓ Created folder $dirName")
    }

    fun deleteFileOrDirectory(path: String) {
        val name = path.substringAfterLast('/')
        vfs.deleteFile(path)
        if (_selectedFile.value?.path == path) {
            val nextFile = vfs.getAllFiles().firstOrNull { !it.isDirectory }
            if (nextFile != null) {
                selectFile(nextFile)
            } else {
                _selectedFile.value = null
                _editorContent.value = ""
            }
        }
        showToast("Removed $name")
        reloadBrowserSandbox()
    }

    fun renameFileOrDirectory(path: String, newName: String) {
        val success = vfs.renameFile(path, newName)
        if (success) {
            showToast("✓ Renamed to $newName")
            val updated = vfs.getFile(path.substringBeforeLast('/') + "/$newName")
            if (updated != null && !updated.isDirectory) {
                selectFile(updated)
            }
        } else {
            showToast("Rename failed")
        }
    }

    fun copyFileOrDirectory(srcPath: String, destDir: String) {
        val activeUser = authManager.currentUser?.username ?: "developer"
        val success = vfs.copyFile(srcPath, destDir, activeUser)
        if (success) {
            showToast("✓ Copied to $destDir")
        } else {
            showToast("Copy failed")
        }
    }

    fun moveFileOrDirectory(srcPath: String, destDir: String) {
        val success = vfs.moveFile(srcPath, destDir)
        if (success) {
            showToast("✓ Moved to $destDir")
        } else {
            showToast("Move failed")
        }
    }

    fun importUploadedFile(fileName: String, content: String) {
        val activeUser = authManager.currentUser?.username ?: "developer"
        val targetPath = "${_explorerCurrentDir.value}/$fileName"
        vfs.writeFile(targetPath, content, activeUser)
        val imported = vfs.getFile(targetPath)
        if (imported != null) {
            selectFile(imported)
        }
        showToast("✓ Uploaded $fileName (${content.length} bytes)")
    }

    fun deleteCurrentFile() {
        val file = _selectedFile.value ?: return
        deleteFileOrDirectory(file.path)
    }

    fun runCurrentFileInTerminal() {
        val file = _selectedFile.value ?: return
        saveCurrentFile()
        selectTab(ActiveWorkspaceTab.TERMINAL)

        val cmd = when (file.language) {
            "python" -> "python3 ${file.name}"
            "javascript" -> "node ${file.name}"
            "c" -> "gcc ${file.name} -o run_bin && ./run_bin"
            "shell" -> "bash ${file.name}"
            "html" -> {
                selectTab(ActiveWorkspaceTab.BROWSER)
                return
            }
            else -> "cat ${file.name}"
        }
        executeTerminalCommand(cmd)
    }

    fun reloadBrowserSandbox() {
        _browserReloadTrigger.value = _browserReloadTrigger.value + 1
    }

    fun addBrowserConsoleLog(level: String, message: String, source: String? = null, lineNumber: Int = 0) {
        val log = BrowserConsoleMessage(
            level = level,
            message = message,
            source = source,
            lineNumber = lineNumber
        )
        _browserConsoleLogs.value = (_browserConsoleLogs.value + log).takeLast(100)
    }

    fun clearBrowserConsole() {
        _browserConsoleLogs.value = emptyList()
    }

    fun resetCloudPcSnapshot() {
        vfs.resetToDefaults()
        terminal.clear()
        agentService.resetTask()
        val defaultFile = vfs.getFile("/workspace/index.html")
        _selectedFile.value = defaultFile
        _editorContent.value = defaultFile?.content ?: ""
        _isEditorDirty.value = false
        reloadBrowserSandbox()
        showToast("⚡ Cloud Computer Snapshot Reset to Factory State")
    }

    fun showToast(msg: String) {
        _statusToast.value = msg
        viewModelScope.launch {
            delay(3000)
            if (_statusToast.value == msg) {
                _statusToast.value = null
            }
        }
    }

    // Cursor AI Actions & Integrations
    fun toggleCursorAiEditor() {
        _isCursorAiEditorOpen.value = !_isCursorAiEditorOpen.value
    }

    fun openCursorAiEditor() {
        _isCursorAiEditorOpen.value = true
    }

    fun closeCursorAiEditor() {
        _isCursorAiEditorOpen.value = false
        _cursorAiPendingResult.value = null
    }

    fun generateCursorCode(instruction: String) {
        val file = _selectedFile.value ?: return
        if (instruction.isBlank()) return

        viewModelScope.launch {
            _isCursorAiGenerating.value = true
            val result = cursorService.generateOrEditCode(
                instruction = instruction,
                currentCode = _editorContent.value,
                filePath = file.path
            )
            _cursorAiPendingResult.value = result
            _isCursorAiGenerating.value = false
            showToast("✨ Cursor AI: ${result.diffSummary.ifBlank { "Generated changes" }}")
        }
    }

    fun acceptCursorAiCode() {
        val pending = _cursorAiPendingResult.value ?: return
        val newCode = pending.generatedCode ?: return
        _editorContent.value = newCode
        _isEditorDirty.value = true
        _cursorAiPendingResult.value = null
        _isCursorAiEditorOpen.value = false
        showToast("✓ Cursor AI changes accepted")
        saveCurrentFile()
    }

    fun rejectCursorAiCode() {
        _cursorAiPendingResult.value = null
        showToast("Cursor AI changes discarded")
    }

    fun explainCurrentCodeWithCursor() {
        val file = _selectedFile.value ?: return
        viewModelScope.launch {
            showToast("🔍 Cursor AI analyzing ${file.name}...")
            val explanation = cursorService.explainCode(_editorContent.value, file.path)
            _cursorAiExplanationDialog.value = explanation
        }
    }

    fun closeCursorExplanationDialog() {
        _cursorAiExplanationDialog.value = null
    }

    fun toggleCursorTerminalAi() {
        _isCursorTerminalAiOpen.value = !_isCursorTerminalAiOpen.value
    }

    fun closeCursorTerminalAi() {
        _isCursorTerminalAiOpen.value = false
        _cursorTerminalSuggestedCommand.value = null
    }

    fun translateNaturalLanguageToTerminal(query: String) {
        if (query.isBlank()) return
        val user = authManager.currentUser?.username ?: "developer"
        val dir = terminal.getCurrentDir()

        viewModelScope.launch {
            val result = cursorService.translateNaturalLanguageToCommand(query, dir, user)
            _cursorTerminalSuggestedCommand.value = result.suggestedCommand
            if (result.suggestedCommand != null) {
                _terminalInput.value = result.suggestedCommand
                showToast("🪄 Cursor: ${result.suggestedCommand}")
            }
        }
    }

    fun autoFixTerminalError(failedCmd: String, errorText: String) {
        viewModelScope.launch {
            showToast("🪄 Cursor AI diagnosing error...")
            val result = cursorService.diagnoseTerminalError(failedCmd, errorText)
            if (result.suggestedCommand != null) {
                _terminalInput.value = result.suggestedCommand
                _cursorTerminalSuggestedCommand.value = result.suggestedCommand
                showToast("🪄 Cursor Suggested Fix: ${result.suggestedCommand}")
            }
        }
    }

    fun openGlobalCursorComposer() {
        _isGlobalCursorComposerOpen.value = true
    }

    fun closeGlobalCursorComposer() {
        _isGlobalCursorComposerOpen.value = false
    }

    // Terminal Mode Actions
    fun switchTerminalMode(mode: TerminalMode) {
        terminal.setTerminalMode(mode)
        showToast("Switched to ${mode.label}")
    }

    // GitHub Authentication Actions
    fun openGitHubAuthDialog() {
        if (gitHubPendingAuth.value == null && !isGitHubConnected.value) {
            githubManager.createDeviceAuth()
        }
        _isGitHubAuthDialogOpen.value = true
    }

    fun closeGitHubAuthDialog() {
        _isGitHubAuthDialogOpen.value = false
    }

    fun startGitHubDeviceAuth(): GitHubDeviceAuth {
        return githubManager.createDeviceAuth()
    }

    fun verifyGitHubAuthCode(code: String): Boolean {
        val user = authManager.currentUser?.username ?: "developer"
        val success = githubManager.authorizeWithTokenOrCode(code, user)
        if (success) {
            val ghUser = githubManager.currentUser.value?.username ?: user
            showToast("✓ Connected to GitHub as @$ghUser")
            viewModelScope.launch {
                terminal.executeCommand("gh auth status")
            }
        } else {
            showToast("⚠️ Invalid GitHub code or token")
        }
        return success
    }

    fun connectGitHubDirectWeb() {
        val user = authManager.currentUser?.username ?: "developer"
        githubManager.connectDirectWeb(user)
        showToast("✓ Connected to GitHub via Direct Web OAuth")
        viewModelScope.launch {
            terminal.executeCommand("gh auth status")
        }
    }

    fun disconnectGitHub() {
        githubManager.disconnect()
        showToast("Disconnected from GitHub")
    }

    // CLI Installer Dialog
    fun openCliInstallerDialog() {
        _isCliInstallerDialogOpen.value = true
    }

    fun closeCliInstallerDialog() {
        _isCliInstallerDialogOpen.value = false
    }
}

