package com.example.manus.data.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class ModelRouterEngine {

    private val _selectedModel = MutableStateFlow(AiModelTier.AUTO_ROUTER)
    val selectedModel: StateFlow<AiModelTier> = _selectedModel.asStateFlow()

    private val _activeChatSessions = MutableStateFlow<List<ChatSession>>(emptyList())
    val activeChatSessions: StateFlow<List<ChatSession>> = _activeChatSessions.asStateFlow()

    private val _currentSessionId = MutableStateFlow<String>("")
    val currentSessionId: StateFlow<String> = _currentSessionId.asStateFlow()

    private val _pendingAttachments = MutableStateFlow<List<PromptAttachment>>(emptyList())
    val pendingAttachments: StateFlow<List<PromptAttachment>> = _pendingAttachments.asStateFlow()

    init {
        createNewChatSession("Unreal Engine & Open World Project")
    }

    fun selectModel(model: AiModelTier) {
        _selectedModel.value = model
    }

    fun createNewChatSession(title: String = "New Autonomous Session"): String {
        val newSession = ChatSession(
            id = UUID.randomUUID().toString(),
            title = title,
            currentModel = _selectedModel.value,
            messages = listOf(
                ChatMessage(
                    role = "assistant",
                    content = "👋 **VirgoYT AI Autonomous Cloud Supercomputer** online.\n" +
                            "- 🎮 **Unreal Engine 5.4 Game Studio**: Nanite & Lumen terrain, Blueprints & C++\n" +
                            "- 🧊 **3D Model Maker**: Procedural GLB/GLTF mesh generation & PBR shaders\n" +
                            "- 🌍 **Google Earth 3D GIS**: Real-world satellite heightmaps & biomes\n" +
                            "- ⚡ **Multi-Model Router**: Auto, NVIDIA Nemotron 70B, Claude 3.5, GPT-4o, DeepSeek-R1\n" +
                            "- 💻 **Full Cloud Desktop**: Interactive remote control & terminal sandbox",
                    modelUsed = _selectedModel.value
                )
            )
        )

        _activeChatSessions.value = listOf(newSession) + _activeChatSessions.value
        _currentSessionId.value = newSession.id
        return newSession.id
    }

    fun switchChatSession(sessionId: String) {
        val session = _activeChatSessions.value.find { it.id == sessionId }
        if (session != null) {
            _currentSessionId.value = sessionId
            _selectedModel.value = session.currentModel
        }
    }

    fun deleteChatSession(sessionId: String) {
        val remaining = _activeChatSessions.value.filter { it.id != sessionId }
        _activeChatSessions.value = remaining
        if (_currentSessionId.value == sessionId) {
            if (remaining.isNotEmpty()) {
                _currentSessionId.value = remaining.first().id
            } else {
                createNewChatSession()
            }
        }
    }

    fun getCurrentSession(): ChatSession? {
        return _activeChatSessions.value.find { it.id == _currentSessionId.value }
    }

    fun addAttachment(attachment: PromptAttachment) {
        _pendingAttachments.value = _pendingAttachments.value + attachment
    }

    fun removeAttachment(attachmentId: String) {
        _pendingAttachments.value = _pendingAttachments.value.filter { it.id != attachmentId }
    }

    fun clearPendingAttachments() {
        _pendingAttachments.value = emptyList()
    }

    fun resolveEffectiveModelForPrompt(promptText: String, attachments: List<PromptAttachment>): Pair<AiModelTier, String> {
        val currentSetting = _selectedModel.value
        if (currentSetting != AiModelTier.AUTO_ROUTER) {
            return Pair(currentSetting, "Manual Override: Using ${currentSetting.displayName}")
        }

        val lower = promptText.lowercase()
        val has3dAttachment = attachments.any { it.type == AttachmentType.MODEL_3D || it.type == AttachmentType.GIS_COORDINATES }
        val hasImageAttachment = attachments.any { it.type == AttachmentType.IMAGE || it.type == AttachmentType.VIDEO }

        return when {
            lower.contains("unreal") || lower.contains("ue5") || lower.contains("physics") || lower.contains("ark") || lower.contains("bgmi") || lower.contains("palworld") -> {
                Pair(AiModelTier.NVIDIA_NEMOTRON_70B, "⚡ Auto-routed to NVIDIA Nemotron 70B (Specialized for Unreal Engine 5 & Game Physics)")
            }
            hasImageAttachment || lower.contains("vision") || lower.contains("satellite") || lower.contains("photogrammetry") || lower.contains("video") -> {
                Pair(AiModelTier.OPENROUTER_GPT_4O, "⚡ Auto-routed to OpenAI GPT-4o (Specialized for Multimodal Vision & Satellite GIS)")
            }
            lower.contains("earth") || lower.contains("gis") || lower.contains("map") || lower.contains("terrain") || lower.contains("dem") -> {
                Pair(AiModelTier.GEMINI_2_5_PRO, "⚡ Auto-routed to Gemini 2.5 Pro (Specialized for 1M Spatial GIS & Google Earth)")
            }
            lower.contains("math") || lower.contains("algorithm") || lower.contains("shader") || lower.contains("matrix") || lower.contains("deepseek") -> {
                Pair(AiModelTier.OPENROUTER_DEEPSEEK_R1, "⚡ Auto-routed to DeepSeek-R1 (Specialized for Mathematical Reasoning & Shaders)")
            }
            lower.contains("c++") || lower.contains("blueprint") || lower.contains("architecture") || lower.contains("refactor") -> {
                Pair(AiModelTier.OPENROUTER_CLAUDE_3_5_SONNET, "⚡ Auto-routed to Claude 3.5 Sonnet (Specialized for C++ & Software Architecture)")
            }
            else -> {
                Pair(AiModelTier.NVIDIA_LLAMA_3_3_70B, "⚡ Auto-routed to NVIDIA Llama 3.3 70B (Ultra-fast execution & code generation)")
            }
        }
    }

    fun appendMessageToCurrentSession(message: ChatMessage) {
        val currentId = _currentSessionId.value
        _activeChatSessions.value = _activeChatSessions.value.map { session ->
            if (session.id == currentId) {
                session.copy(messages = session.messages + message)
            } else {
                session
            }
        }
    }
}
