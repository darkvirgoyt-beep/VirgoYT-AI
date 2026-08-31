package com.example.manus.data.model

import java.util.UUID

enum class ActiveWorkspaceTab(val label: String) {
    AGENT("AI Swarm"),
    PROJECT_SCAN("Project AI"),
    DATABASE_AI("Database AI"),
    APP_GEN("App Creation"),
    GAME_STUDIO("Unreal & 3D"),
    LIVE_COMPUTER("Cloud PC Live"),
    TERMINAL("Terminal"),
    FILES("Files"),
    EDITOR("AI Editor"),
    MEMORY_RAG("Memory RAG"),
    BROWSER("Sandbox"),
    MONITOR("Monitor")
}

enum class AiModelTier(
    val id: String,
    val displayName: String,
    val provider: String,
    val iconEmoji: String,
    val specialty: String,
    val maxTokens: Int = 128000
) {
    AUTO_ROUTER(
        id = "auto",
        displayName = "Auto Intelligent Router",
        provider = "VirgoYT Omni",
        iconEmoji = "⚡",
        specialty = "Dynamically routes each task (Unreal 5, 3D GLB, C++, Video) to the best model"
    ),
    NVIDIA_NEMOTRON_70B(
        id = "nvidia/llama-3.1-nemotron-70b-instruct",
        displayName = "NVIDIA Nemotron 70B",
        provider = "NVIDIA NIM",
        iconEmoji = "🟢",
        specialty = "Unreal Engine 5 physics, game math, complex simulation architectures"
    ),
    NVIDIA_LLAMA_3_3_70B(
        id = "meta/llama-3.3-70b-instruct",
        displayName = "NVIDIA Llama 3.3 70B",
        provider = "NVIDIA NIM",
        iconEmoji = "⚡",
        specialty = "Ultra-fast low-latency code execution and live tool control"
    ),
    OPENROUTER_CLAUDE_3_5_SONNET(
        id = "anthropic/claude-3.5-sonnet",
        displayName = "Claude 3.5 Sonnet",
        provider = "OpenRouter / Anthropic",
        iconEmoji = "🧠",
        specialty = "Highest quality C++, Blueprint logic, and complex system engineering"
    ),
    OPENROUTER_GPT_4O(
        id = "openai/gpt-4o",
        displayName = "OpenAI GPT-4o Omni",
        provider = "OpenRouter / OpenAI",
        iconEmoji = "👁️",
        specialty = "Multimodal vision, satellite photogrammetry, audio & video analysis"
    ),
    OPENROUTER_DEEPSEEK_R1(
        id = "deepseek/deepseek-r1",
        displayName = "DeepSeek-R1 Thinking",
        provider = "OpenRouter / DeepSeek",
        iconEmoji = "🔬",
        specialty = "Deep step-by-step mathematical reasoning & shader computation"
    ),
    GEMINI_2_5_PRO(
        id = "google/gemini-2.5-pro",
        displayName = "Gemini 2.5 Pro",
        provider = "Google AI Studio",
        iconEmoji = "✨",
        specialty = "1M token context, Google Earth spatial GIS & 3D world synthesis"
    )
}

enum class AttachmentType(val label: String, val icon: String) {
    IMAGE("Photo / Texture", "📸"),
    FILE("Code / Data File", "📁"),
    VIDEO("Video / Cutscene", "🎥"),
    MODEL_3D("3D Mesh / GLB", "🧊"),
    GIS_COORDINATES("Google Earth GIS", "🌍"),
    CONNECTOR("API Connector", "🔌")
}

data class PromptAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AttachmentType,
    val uriOrData: String,
    val sizeBytes: Long = 0L,
    val metaDescription: String = ""
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "system"
    val content: String,
    val modelUsed: AiModelTier = AiModelTier.AUTO_ROUTER,
    val attachments: List<PromptAttachment> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val toolExecutions: List<String> = emptyList()
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList(),
    val currentModel: AiModelTier = AiModelTier.AUTO_ROUTER
)

enum class GameEngineType(val label: String, val badge: String) {
    UNREAL_ENGINE_5("Unreal Engine 5.4", "UE5 - Nanite & Lumen"),
    BLENDER_3D("Blender 4.2 Pro", "3D Modeling & Animation"),
    GOOGLE_EARTH_3D("Google Earth 3D GIS", "Photogrammetry & DEM"),
    GODOT_4("Godot 4.3 Engine", "Lightweight Cross-Platform"),
    NATIVE_WINDOWS_EXE("Wine / Windows Sandbox", "EXE Desktop Apps")
}

data class Game3DModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val format: String = "GLB", // GLB, GLTF, OBJ, FBX
    val category: String = "Terrain & Asset",
    val vertexCount: Int = 18450,
    val polygonCount: Int = 36890,
    val materials: List<String> = listOf("PBR_Nanite_Albedo", "PBR_Normal", "PBR_Roughness", "PBR_Metallic"),
    val description: String = "High-fidelity 3D mesh generated for game scene",
    val wireframeAvailable: Boolean = true
)

data class GoogleEarthGISMap(
    val id: String = UUID.randomUUID().toString(),
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double,
    val terrainResolution: String = "0.5m High-Res DEM",
    val biomeType: String = "Open World Mountain / Forest",
    val gameInspiration: String = "BGMI / Ark Survival Evolved / Downlands / Palworld"
)

data class GameProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Project Valkyrie: Open World Survival",
    val engine: GameEngineType = GameEngineType.UNREAL_ENGINE_5,
    val genre: String = "AAA Open World Survival (Ark / Palworld / BGMI)",
    val graphicPreset: String = "Ultra HD 4K (Lumen GI, Nanite Geometry, Ray Traced Shadows)",
    val targetFps: Int = 120,
    val mapLocation: GoogleEarthGISMap = GoogleEarthGISMap(
        locationName = "Pochinki Highlands & Volcanic Glade",
        latitude = 36.1069,
        longitude = -112.1129,
        elevationMeters = 2450.0,
        biomeType = "Volcanic Jungle & Snow Peaks",
        gameInspiration = "BGMI / Ark Survival / Palworld"
    ),
    val models: List<Game3DModel> = emptyList(),
    val isRendering: Boolean = false,
    val renderProgressPercent: Int = 100,
    val cppSourceFile: String = "Source/VirgoYTGame/PlayerCharacter.cpp",
    val blueprintFile: String = "Content/Blueprints/BP_OpenWorldGameMode.uasset"
)

data class DiffFileSnapshot(
    val filename: String,
    val originalText: String,
    val modifiedText: String,
    val activeDiffLineCount: Int = 12,
    val isLive: Boolean = true,
    val activeToolName: String = "Editor",
    val activeToolDetails: String = "Editing file: jarvis-personal-ai/server/_core/llm.ts",
    val timelineProgressPercent: Float = 0.88f
)

data class SecretCredentialPrompt(
    val id: String = UUID.randomUUID().toString(),
    val serviceName: String, // "GitHub", "Gmail / Google Account", "Epic Games Unreal", "OpenRouter API"
    val iconEmoji: String = "🔐",
    val promptReason: String,
    val requiredFields: List<String> = listOf("Email / Username", "Password / Access Token"),
    val isCompleted: Boolean = false
)

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: String = "Developer",
    val avatarColorHex: Long = 0xFF6366F1,
    val homeDir: String = "/home/$username",
    val createdAt: Long = System.currentTimeMillis()
)

data class AuthSession(
    val user: User,
    val token: String = UUID.randomUUID().toString(),
    val loginTime: Long = System.currentTimeMillis()
)

enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

enum class OutputType {
    COMMAND,
    STDOUT,
    STDERR,
    SYSTEM,
    AGENT_ACTION,
    SUCCESS,
    WARNING
}

data class TerminalEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val type: OutputType = OutputType.STDOUT,
    val timestamp: Long = System.currentTimeMillis()
)

data class VirtualFile(
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val content: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val sizeBytes: Long = content.toByteArray().size.toLong(),
    val owner: String = "developer",
    val permissions: String = if (isDirectory) "drwxr-xr-x" else "-rw-r--r--"
) {
    val extension: String
        get() = if (isDirectory) "" else name.substringAfterLast('.', "")

    val language: String
        get() = when (extension.lowercase()) {
            "html", "htm" -> "html"
            "js", "mjs", "cjs" -> "javascript"
            "ts" -> "typescript"
            "css" -> "css"
            "py" -> "python"
            "json" -> "json"
            "sh", "bash" -> "shell"
            "md" -> "markdown"
            "c", "h", "cpp", "hpp" -> "c"
            "kt" -> "kotlin"
            "uproject", "uasset" -> "json"
            "glb", "gltf", "obj" -> "3d"
            else -> "text"
        }
}

data class AgentSubtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val toolName: String? = null,
    val toolInput: String? = null,
    val toolOutput: String? = null,
    val thought: String? = null,
    val status: TaskStatus = TaskStatus.PENDING
)

data class AgentTask(
    val id: String = UUID.randomUUID().toString(),
    val goal: String,
    val explanation: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val subtasks: List<AgentSubtask> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class SystemStats(
    val cpuUsagePercent: Float = 14.8f,
    val gpuUsagePercent: Float = 68.5f,
    val memoryUsedMb: Int = 3450,
    val memoryTotalMb: Int = 16384,
    val diskUsedMb: Int = 14200,
    val diskTotalMb: Int = 131072,
    val activePid: Int = 1042,
    val activeProcess: String = "virgoyt-agent-engine",
    val uptimeSeconds: Long = 4820L,
    val region: String = "us-central1 (gpu-a100-cloud-node)"
)

data class BrowserConsoleMessage(
    val id: String = UUID.randomUUID().toString(),
    val level: String, // LOG, WARN, ERROR, INFO
    val message: String,
    val source: String? = null,
    val lineNumber: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class ProcessInfo(
    val pid: Int,
    val user: String,
    val cpu: Float,
    val memory: Float,
    val command: String,
    val status: String = "RUNNING"
)

