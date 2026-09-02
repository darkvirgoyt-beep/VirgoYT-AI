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
                        content = "Hey! Welcome to **VirgoYT Cloud AI** ⚡ I'm your autonomous multi-agent co-developer and 3D studio copilot!\n\n" +
                                "What crazy or epic project are we conquering today?\n" +
                                "• 🌐 **Full-Stack SaaS**: Next.js 15 (App Router), FastAPI async backend, Database & Auth\n" +
                                "• 🎮 **3D Game Studio**: Three.js / Unreal Engine scene & cybernetic shader pipeline\n" +
                                "• 📱 **Android Native**: Jetpack Compose, Kotlin Flow, Room DB & release APK packaging\n" +
                                "• ⌨️ **Terminal & Cloud PC**: Direct bash commands, test suites & deployments\n\n" +
                                "You don't need to switch between tabs — code generation, interactive diffs, and terminal commands all run right here in this unified chat automatically!\n\n" +
                                "What crazy idea or project would you like to build right now?",
                        modelUsed = AiModelTier.GEMINI_2_5_PRO,
                        quickActionChips = listOf(
                            QuickActionChip(label = "Generate FullStack App", actionCommand = "Create a Next.js 15 + FastAPI SaaS application", iconEmoji = "🚀"),
                            QuickActionChip(label = "Launch 3D World", actionCommand = "Build interactive Three.js procedural city", iconEmoji = "🎮"),
                            QuickActionChip(label = "Run Unit Tests", actionCommand = "gradle :app:testDebugUnitTest", iconEmoji = "🧪"),
                            QuickActionChip(label = "Download Release APK", actionCommand = "curl -sLO release.apk", iconEmoji = "📦")
                        ),
                        followUpQuestions = listOf(
                            "What crazy project are you dreaming of building today?",
                            "Should we scaffold a Next.js 15 + FastAPI SaaS?",
                            "Would you like an interactive 3D WebGL scene?"
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
