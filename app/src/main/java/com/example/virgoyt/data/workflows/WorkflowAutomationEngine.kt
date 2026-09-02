package com.example.virgoyt.data.workflows

import com.example.virgoyt.data.model.TaskStatus
import com.example.virgoyt.data.model.WorkflowActionType
import com.example.virgoyt.data.model.WorkflowPipeline
import com.example.virgoyt.data.model.WorkflowRunLog
import com.example.virgoyt.data.model.WorkflowStep
import com.example.virgoyt.data.model.WorkflowTriggerType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class WorkflowAutomationEngine {

    private val _pipelines = MutableStateFlow<List<WorkflowPipeline>>(
        listOf(
            WorkflowPipeline(
                title = "Automated CI/CD & Test Pipeline",
                description = "Runs Vitest on file change, audits security, and deploys to cloud sandbox",
                triggerType = WorkflowTriggerType.ON_FILE_CHANGE,
                triggerConfig = "/workspace/src/*",
                steps = listOf(
                    WorkflowStep(name = "Run Linter & Tests", actionType = WorkflowActionType.RUN_SHELL, configPayload = "npm run test"),
                    WorkflowStep(name = "AI Code Review", actionType = WorkflowActionType.CALL_AI_MODEL, configPayload = "Review modified AST for memory leaks"),
                    WorkflowStep(name = "Trigger Webhook", actionType = WorkflowActionType.HTTP_WEBHOOK, configPayload = "https://ci.virgoyt.ai/webhook/deploy")
                )
            ),
            WorkflowPipeline(
                title = "Daily Git Sync & Database Snapshot",
                description = "Nightly backup of VFS tables and commit history to remote git",
                triggerType = WorkflowTriggerType.SCHEDULED_CRON,
                triggerConfig = "0 0 * * *",
                steps = listOf(
                    WorkflowStep(name = "Export DB Schema", actionType = WorkflowActionType.DATABASE_QUERY, configPayload = "SELECT * FROM users"),
                    WorkflowStep(name = "Git Commit & Push", actionType = WorkflowActionType.GIT_COMMIT, configPayload = "Automated nightly sync")
                )
            )
        )
    )
    val pipelines: StateFlow<List<WorkflowPipeline>> = _pipelines.asStateFlow()

    private val _runLogs = MutableStateFlow<List<WorkflowRunLog>>(emptyList())
    val runLogs: StateFlow<List<WorkflowRunLog>> = _runLogs.asStateFlow()

    fun triggerPipeline(pipelineId: String) {
        val pipeline = _pipelines.value.find { it.id == pipelineId } ?: return
        val log = WorkflowRunLog(
            pipelineId = pipelineId,
            pipelineTitle = pipeline.title,
            status = TaskStatus.COMPLETED,
            durationMs = 420L,
            logs = listOf(
                "Triggered: ${pipeline.triggerType.label}",
                "Step 1: ${pipeline.steps.firstOrNull()?.name ?: "Init"} -> Success",
                "Execution finished in 420ms."
            )
        )
        _runLogs.value = listOf(log) + _runLogs.value
    }

    fun togglePipeline(pipelineId: String) {
        _pipelines.value = _pipelines.value.map {
            if (it.id == pipelineId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun createPipeline(title: String, description: String, triggerType: WorkflowTriggerType): WorkflowPipeline {
        val p = WorkflowPipeline(
            title = title,
            description = description,
            triggerType = triggerType,
            steps = listOf(
                WorkflowStep(name = "Execute Shell Action", actionType = WorkflowActionType.RUN_SHELL, configPayload = "echo 'running pipeline'")
            )
        )
        _pipelines.value = _pipelines.value + p
        return p
    }
}
