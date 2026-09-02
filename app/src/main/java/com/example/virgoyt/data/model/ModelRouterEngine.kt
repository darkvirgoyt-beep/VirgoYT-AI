package com.example.virgoyt.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ModelRouterEngine {

    private val _selectedModel = MutableStateFlow(AiModelTier.AUTO_ROUTER)
    val selectedModel: StateFlow<AiModelTier> = _selectedModel.asStateFlow()

    private val _activeChatSessions = MutableStateFlow<List<ChatSession>>(
        listOf(
            ChatSession(
                title = "Cloud Infrastructure & Swarm Setup",
                messages = listOf(
                    ChatMessage(
                        role = "assistant",
                        content = "VirgoYT Cloud AI cluster initialized. Autonomous agents and sandbox environments ready. How can I assist with your deployment or software development today?",
                        modelUsed = AiModelTier.GEMINI_2_5_PRO,
                        quickActionChips = listOf(
                            QuickActionChip(label = "Generate FullStack App", actionCommand = "Create a Next.js 15 + FastAPI SaaS application", iconEmoji = "🚀"),
                            QuickActionChip(label = "Scan Workspace", actionCommand = "Analyze repository dependencies and API routes", iconEmoji = "🏛️"),
                            QuickActionChip(label = "Launch 3D World", actionCommand = "Build interactive Three.js procedural city", iconEmoji = "🎮"),
                            QuickActionChip(label = "Run Tests", actionCommand = "Run unit and screenshot regression tests", iconEmoji = "🧪")
                        )
                    )
                )
            )
        )
    )
    val activeChatSessions: StateFlow<List<ChatSession>> = _activeChatSessions.asStateFlow()

    private val _currentSessionId = MutableStateFlow(_activeChatSessions.value.first().id)
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    private val _pendingAttachments = MutableStateFlow<List<PromptAttachment>>(emptyList())
    val pendingAttachments: StateFlow<List<PromptAttachment>> = _pendingAttachments.asStateFlow()

    fun selectModel(model: AiModelTier) {
        _selectedModel.value = model
    }

    fun createNewChatSession(title: String = "New Architecture Session"): String {
        val newSession = ChatSession(title = title)
        _activeChatSessions.value = _activeChatSessions.value + newSession
        _currentSessionId.value = newSession.id
        return newSession.id
    }

    fun switchChatSession(sessionId: String) {
        if (_activeChatSessions.value.any { it.id == sessionId }) {
            _currentSessionId.value = sessionId
        }
    }

    fun deleteChatSession(sessionId: String) {
        val updated = _activeChatSessions.value.filterNot { it.id == sessionId }
        if (updated.isNotEmpty()) {
            _activeChatSessions.value = updated
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = updated.first().id
            }
        } else {
            val fresh = ChatSession(title = "General AI Workspace")
            _activeChatSessions.value = listOf(fresh)
            _currentSessionId.value = fresh.id
        }
    }

    fun getCurrentSession(): ChatSession? {
        return _activeChatSessions.value.find { it.id == _currentSessionId.value }
    }

    fun addAttachment(attachment: PromptAttachment) {
        _pendingAttachments.value = _pendingAttachments.value + attachment
    }

    fun removeAttachment(attachmentId: String) {
        _pendingAttachments.value = _pendingAttachments.value.filterNot { it.id == attachmentId }
    }

    fun clearPendingAttachments() {
        _pendingAttachments.value = emptyList()
    }

    fun resolveEffectiveModelForPrompt(
        promptText: String,
        attachments: List<PromptAttachment>
    ): Pair<AiModelTier, String> {
        val selected = _selectedModel.value
        if (selected != AiModelTier.AUTO_ROUTER) {
            return selected to "Explicitly selected model: ${selected.displayName}"
        }

        val promptLower = promptText.lowercase()
        return when {
            attachments.any { it.type == AttachmentType.IMAGE } -> {
                AiModelTier.GEMINI_2_5_PRO to "Routed to Gemini 2.5 Pro for multimodal visual analysis"
            }
            promptLower.contains("math") || promptLower.contains("algorithm") || promptLower.contains("proof") || promptLower.contains("complex logic") -> {
                AiModelTier.KIE_DEEPSEEK_R1 to "Routed to DeepSeek R1 for deep chain-of-thought mathematical reasoning"
            }
            promptLower.contains("speed") || promptLower.contains("fast") || promptLower.contains("summarize") -> {
                AiModelTier.GROQ_LLAMA_3_3_70B to "Routed to Groq LPU Llama 3.3 for sub-second inference"
            }
            promptLower.contains("unreal") || promptLower.contains("three.js") || promptLower.contains("game") -> {
                AiModelTier.CLAUDE_3_7_SONNET to "Routed to Claude 3.7 Sonnet for complex 3D engine coding"
            }
            else -> {
                AiModelTier.GEMINI_2_5_FLASH to "Auto-routed to Gemini 2.5 Flash for high efficiency"
            }
        }
    }

    fun appendMessageToCurrentSession(message: ChatMessage) {
        val currId = _currentSessionId.value
        _activeChatSessions.value = _activeChatSessions.value.map { session ->
            if (session.id == currId) {
                session.copy(messages = session.messages + message)
            } else {
                session
            }
        }
    }
}
