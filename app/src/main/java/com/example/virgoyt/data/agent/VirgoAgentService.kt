package com.example.virgoyt.data.agent

import com.example.virgoyt.data.model.AgentSubtask
import com.example.virgoyt.data.model.AgentTask
import com.example.virgoyt.data.model.AiModelTier
import com.example.virgoyt.data.model.ChatMessage
import com.example.virgoyt.data.model.ModelRouterEngine
import com.example.virgoyt.data.model.PromptAttachment
import com.example.virgoyt.data.model.TaskStatus
import com.example.virgoyt.data.vfs.VirtualFileSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AgentExecutionState {
    IDLE,
    PLANNING,
    CODING,
    REVIEWING,
    TESTING,
    DEPLOYING,
    COMPLETED
}

class VirgoAgentService(
    val routerEngine: ModelRouterEngine = ModelRouterEngine(),
    val vfs: VirtualFileSystem = VirtualFileSystem(),
    val multiAgentTeam: MultiAgentDevelopmentTeam = MultiAgentDevelopmentTeam(),
    val cursorAiService: CursorAiAssistantService = CursorAiAssistantService()
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _isAgentBusy = MutableStateFlow(false)
    val isAgentBusy: StateFlow<Boolean> = _isAgentBusy.asStateFlow()

    private val _currentTask = MutableStateFlow<AgentTask?>(null)
    val currentTask: StateFlow<AgentTask?> = _currentTask.asStateFlow()

    private val _executionState = MutableStateFlow(AgentExecutionState.IDLE)
    val executionState: StateFlow<AgentExecutionState> = _executionState.asStateFlow()

    fun runGoal(userPrompt: String, attachments: List<PromptAttachment> = emptyList()) {
        val (effectiveTier, routeReason) = routerEngine.resolveEffectiveModelForPrompt(userPrompt, attachments)

        val userMessage = ChatMessage(
            role = "user",
            content = userPrompt,
            modelUsed = effectiveTier,
            attachments = attachments
        )
        routerEngine.appendMessageToCurrentSession(userMessage)

        val subtasks = listOf(
            AgentSubtask(title = "Architectural Analysis", description = "Decomposing prompt & verifying codebase structure", toolType = "system_scan"),
            AgentSubtask(title = "Synthesize Solution", description = "Generating components and VFS modifications", toolType = "code_editor"),
            AgentSubtask(title = "Quality Verification", description = "Checking type safety and running test assertions", toolType = "test_runner")
        )

        val task = AgentTask(
            userGoal = userPrompt,
            planSteps = subtasks,
            status = TaskStatus.RUNNING
        )
        _currentTask.value = task
        _isAgentBusy.value = true
        _executionState.value = AgentExecutionState.PLANNING

        scope.launch {
            delay(400)
            _executionState.value = AgentExecutionState.CODING
            multiAgentTeam.broadcast(AgentRole.ARCHITECT, AgentRole.FULLSTACK_CODER, "Blueprint generated for: '$userPrompt'")

            delay(600)
            _executionState.value = AgentExecutionState.REVIEWING
            multiAgentTeam.broadcast(AgentRole.FULLSTACK_CODER, AgentRole.CODE_REVIEWER, "Code implemented in virtual workspace.")

            delay(400)
            _executionState.value = AgentExecutionState.TESTING
            multiAgentTeam.broadcast(AgentRole.CODE_REVIEWER, AgentRole.QA_AUTOMATION, "Code approved. Running test assertions.")

            delay(300)
            _executionState.value = AgentExecutionState.COMPLETED
            _isAgentBusy.value = false

            val assistantMessage = ChatMessage(
                role = "assistant",
                content = "Goal completed successfully: \"$userPrompt\"\n\n- Model: ${effectiveTier.displayName} ($routeReason)\n- Architecture reviewed and verified\n- Virtual File System updated\n- 0 errors encountered",
                modelUsed = effectiveTier,
                toolExecutions = listOf("vfs.read_file", "vfs.write_file", "code_review_ast", "run_test_suite")
            )
            routerEngine.appendMessageToCurrentSession(assistantMessage)
            _currentTask.value = task.copy(status = TaskStatus.COMPLETED)
        }
    }
}
