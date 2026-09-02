package com.example.virgoyt.data.agent

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class AgentRole(val title: String, val specialty: String, val badgeColorHex: Long, val iconEmoji: String) {
    ARCHITECT("Lead System Architect", "System decomposition, high-level schema design, and modular boundaries", 0xFF6366F1, "🏛️"),
    FULLSTACK_CODER("Senior Full-Stack Engineer", "Implementation of frontend, backend, APIs, algorithms, and VFS files", 0xFF06B6D4, "💻"),
    CODE_REVIEWER("Security & Quality Auditor", "AST diff verification, vulnerability scanning, and performance profiling", 0xFFEAB308, "🛡️"),
    QA_AUTOMATION("Automated Test Engineer", "Unit test creation, end-to-end testing, and mock assertion suites", 0xFF10B981, "🧪"),
    DEVOPS_ENGINEER("Cloud Infrastructure Lead", "Docker containers, CI/CD pipelines, Kubernetes, and telemetry setup", 0xFFA855F7, "☁️")
}

data class AgentMember(
    val role: AgentRole,
    val name: String,
    val currentThought: String = "Standing by for instructions",
    val isActive: Boolean = false,
    val completedSubtasks: Int = 0
)

data class TeamCommunicationMessage(
    val id: String = UUID.randomUUID().toString(),
    val fromRole: AgentRole,
    val toRole: AgentRole,
    val messageContent: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PipelineStep(
    val id: String = UUID.randomUUID().toString(),
    val role: AgentRole,
    val title: String,
    val description: String,
    val isDone: Boolean = false
)

data class AutonomousTeamPipeline(
    val id: String = UUID.randomUUID().toString(),
    val userObjective: String,
    val steps: List<PipelineStep>,
    val currentStepIndex: Int = 0,
    val isExecuting: Boolean = false
)

class MultiAgentDevelopmentTeam {

    private val _members = MutableStateFlow<List<AgentMember>>(
        listOf(
            AgentMember(AgentRole.ARCHITECT, "Atlas-9", currentThought = "Decomposing user specification into technical DAG"),
            AgentMember(AgentRole.FULLSTACK_CODER, "Nexus-Coder", currentThought = "Synthesizing reactive components and API endpoints"),
            AgentMember(AgentRole.CODE_REVIEWER, "Aegis-Reviewer", currentThought = "Verifying type-safety and OWASP constraints"),
            AgentMember(AgentRole.QA_AUTOMATION, "Sentinel-QA", currentThought = "Generating automated regression tests"),
            AgentMember(AgentRole.DEVOPS_ENGINEER, "Prometheus-Cloud", currentThought = "Configuring container sandbox runtime")
        )
    )
    val members: StateFlow<List<AgentMember>> = _members.asStateFlow()

    private val _teamLogs = MutableStateFlow<List<TeamCommunicationMessage>>(emptyList())
    val teamLogs: StateFlow<List<TeamCommunicationMessage>> = _teamLogs.asStateFlow()

    private val _pipeline = MutableStateFlow<AutonomousTeamPipeline?>(null)
    val pipeline: StateFlow<AutonomousTeamPipeline?> = _pipeline.asStateFlow()

    fun broadcast(from: AgentRole, to: AgentRole, message: String) {
        val msg = TeamCommunicationMessage(fromRole = from, toRole = to, messageContent = message)
        _teamLogs.value = _teamLogs.value + msg
    }
}
