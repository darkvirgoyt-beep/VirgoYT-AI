package com.example.manus.data.model

import java.util.UUID

enum class ActiveWorkspaceTab(val label: String, val iconEmoji: String) {
    AGENT("AI Swarm", "⚡"),
    VOICE_ASSISTANT("Voice AI", "🎙️"),
    WEB_DASHBOARD("Web App", "🌐"),
    APP_GEN("App Studio", "🚀"),
    PLUGINS_TOOLS("Plugins & Tools", "🧩"),
    WORKFLOWS("Workflows", "🔄"),
    PROJECT_SCAN("Project AI", "🏛️"),
    DATABASE_AI("Database AI", "🗄️"),
    MEMORY_RAG("Memory RAG", "🧠"),
    CLOUD_STORAGE("Cloud Storage", "☁️"),
    LIVE_COMPUTER("Cloud PC Live", "🖥️"),
    GAME_STUDIO("Unreal & 3D", "🎮"),
    EDITOR("AI Editor", "💻"),
    TERMINAL("Terminal", "⌨️"),
    FILES("Files", "📁"),
    BROWSER("Sandbox", "🌐"),
    MONITOR("Telemetry", "📊")
}

enum class AiModelTier(
    val id: String,
    val displayName: String,
    val provider: String,
    val iconEmoji: String,
    val specialty: String,
    val maxTokens: Int = 128000,
    val baseUrl: String = "",
    val defaultApiKey: String = "",
    val authHeaderFormat: String = "Authorization: Bearer <API_KEY>",
    val sdkType: String = "OpenAI Compatible / LangChain"
) {
    AUTO_ROUTER(
        id = "auto",
        displayName = "Auto Intelligent Router",
        provider = "VirgoYT Omni",
        iconEmoji = "⚡",
        specialty = "Dynamically routes each task across BazaarLink, OpenRouter, Groq, Kie.ai, and NVIDIA",
        baseUrl = "https://api.bazaarlink.ai/v1",
        defaultApiKey = ""
    ),
    BAZAARLINK_AI(
        id = "bazaarlink/openai-compatible",
        displayName = "BazaarLink AI (v1)",
        provider = "BazaarLink.ai / OpenAI SDK",
        iconEmoji = "🌐",
        specialty = "High-speed OpenAI-compatible gateway (https://api.bazaarlink.ai/v1)",
        baseUrl = "https://api.bazaarlink.ai/v1",
        defaultApiKey = "",
        authHeaderFormat = "Authorization: Bearer sk-bl-your-key-here",
        sdkType = "openai (base_url=) | langchain | any OpenAI-compatible"
    ),
    OPENROUTER_HUB(
        id = "openrouter/unified-hub",
        displayName = "OpenRouter Hub",
        provider = "OpenRouter.ai",
        iconEmoji = "🔀",
        specialty = "Unified multi-model aggregator with direct OpenRouter API key routing",
        baseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = "",
        authHeaderFormat = "Authorization: Bearer sk-or-v1-your-key-here",
        sdkType = "OpenRouter SDK / OpenAI Compatible"
    ),
    KIE_AI_GATEWAY(
        id = "kie/openai-compatible",
        displayName = "Kie.ai Gateway",
        provider = "Kie.ai / OpenAI Compatible",
        iconEmoji = "⚡",
        specialty = "Fast inference endpoint (https://api.kie.ai/v1) with Bearer token authentication",
        baseUrl = "https://api.kie.ai/v1",
        defaultApiKey = "",
        authHeaderFormat = "Authorization: Bearer your-kie-api-key",
        sdkType = "openai (base_url=https://api.kie.ai/v1)"
    ),
    GROQ_LLAMA_3_3_70B(
        id = "groq/llama-3.3-70b-versatile",
        displayName = "Groq LPU (Llama 3.3 70B)",
        provider = "Groq Cloud LPUs",
        iconEmoji = "🚀",
        specialty = "Ultra-low latency LPU engine (500+ tok/s) via Groq API (gsk_...)",
        baseUrl = "https://api.groq.com/openai/v1",
        defaultApiKey = "",
        authHeaderFormat = "Authorization: Bearer gsk_your_key_here",
        sdkType = "Groq SDK / OpenAI Compatible"
    ),
    NVIDIA_NEMOTRON_70B(
        id = "nvidia/llama-3.1-nemotron-70b-instruct",
        displayName = "NVIDIA Nemotron 70B",
        provider = "NVIDIA NIM",
        iconEmoji = "🟢",
        specialty = "Unreal Engine 5 physics, game math, complex simulation architectures",
        baseUrl = "https://integrate.api.nvidia.com/v1"
    ),
    NVIDIA_LLAMA_3_3_70B(
        id = "meta/llama-3.3-70b-instruct",
        displayName = "NVIDIA Llama 3.3 70B",
        provider = "NVIDIA NIM",
        iconEmoji = "⚡",
        specialty = "Ultra-fast low-latency code execution and live tool control",
        baseUrl = "https://integrate.api.nvidia.com/v1"
    ),
    OPENROUTER_CLAUDE_3_5_SONNET(
        id = "anthropic/claude-3.5-sonnet",
        displayName = "Claude 3.5 Sonnet",
        provider = "OpenRouter / Anthropic",
        iconEmoji = "🧠",
        specialty = "Highest quality C++, Blueprint logic, and complex system engineering",
        baseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = ""
    ),
    OPENROUTER_GPT_4O(
        id = "openai/gpt-4o",
        displayName = "OpenAI GPT-4o Omni",
        provider = "OpenRouter / OpenAI",
        iconEmoji = "👁️",
        specialty = "Multimodal vision, satellite photogrammetry, audio & video analysis",
        baseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = ""
    ),
    OPENROUTER_DEEPSEEK_R1(
        id = "deepseek/deepseek-r1",
        displayName = "DeepSeek-R1 Thinking",
        provider = "OpenRouter / DeepSeek",
        iconEmoji = "🔬",
        specialty = "Deep step-by-step mathematical reasoning & shader computation",
        baseUrl = "https://openrouter.ai/api/v1",
        defaultApiKey = ""
    ),
    MOONSHOT_AI_V1(
        id = "moonshot-v1-auto",
        displayName = "Moonshot AI / Kimi (v1)",
        provider = "Moonshot AI",
        iconEmoji = "🌙",
        specialty = "High-speed reasoning, long context & tool use (https://api.moonshot.cn/v1)",
        baseUrl = "https://api.moonshot.cn/v1",
        defaultApiKey = "sk-8dUtUs9SmRA981GISiV3JXHW9XNr7Z0xlcLwcnJ7MQmEFA30",
        authHeaderFormat = "Authorization: Bearer sk-8dUtUs9SmRA981GISiV3JXHW9XNr7Z0xlcLwcnJ7MQmEFA30",
        sdkType = "OpenAI Compatible / Moonshot SDK (base_url=https://api.moonshot.cn/v1)"
    ),
    MOONSHOT_V1_128K(
        id = "moonshot-v1-128k",
        displayName = "Moonshot Kimi 128k",
        provider = "Moonshot AI",
        iconEmoji = "🌕",
        specialty = "Ultra-long 128k token context window for massive documents and codebase synthesis",
        baseUrl = "https://api.moonshot.cn/v1",
        defaultApiKey = "sk-8dUtUs9SmRA981GISiV3JXHW9XNr7Z0xlcLwcnJ7MQmEFA30",
        authHeaderFormat = "Authorization: Bearer sk-8dUtUs9SmRA981GISiV3JXHW9XNr7Z0xlcLwcnJ7MQmEFA30",
        sdkType = "OpenAI Compatible / Moonshot SDK (base_url=https://api.moonshot.cn/v1)"
    ),
    GEMINI_2_5_PRO(
        id = "google/gemini-2.5-pro",
        displayName = "Gemini 2.5 Pro",
        provider = "Google AI / DeepMind",
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

data class SmartAiOption(
    val id: String = UUID.randomUUID().toString(),
    val tag: String, // e.g. "OPTION A (Recommended)"
    val title: String, // e.g. "Next-Gen Reactive Glassmorphism App"
    val description: String,
    val performanceImpact: String = "⚡ 120 FPS / Instant Load",
    val actionCommand: String = "" // e.g. "build pwa"
)

data class QuickActionChip(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val iconEmoji: String = "⚡",
    val actionType: String = "EXECUTE_COMMAND", // SWITCH_TAB, EXECUTE_COMMAND, RUN_GOAL, TRIGGER_WORKFLOW
    val payload: String = ""
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "system"
    val content: String,
    val modelUsed: AiModelTier = AiModelTier.AUTO_ROUTER,
    val attachments: List<PromptAttachment> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val toolExecutions: List<String> = emptyList(),
    val emotionalResonance: String? = null,
    val detectedIntent: String? = null,
    val recommendedOptions: List<SmartAiOption> = emptyList(),
    val quickActionChips: List<QuickActionChip> = emptyList(),
    val realtimeTelemetrySnapshot: String? = null,
    val confidenceScore: Float = 0.999f
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
    val displayName: String = username,
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
            "ts", "tsx" -> "typescript"
            "css", "scss" -> "css"
            "py" -> "python"
            "cs" -> "csharp"
            "cpp", "cxx", "cc", "hpp" -> "cpp"
            "c", "h" -> "c"
            "rs" -> "rust"
            "go" -> "go"
            "java" -> "java"
            "kt", "kts" -> "kotlin"
            "json" -> "json"
            "xml" -> "xml"
            "yaml", "yml" -> "yaml"
            "sh", "bash", "zsh" -> "shell"
            "md" -> "markdown"
            "sql" -> "sql"
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

// ==========================================
// VOICE ASSISTANT DATA MODELS
// ==========================================
enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

data class VoiceProfile(
    val id: String = "virgo_neural_aura",
    val name: String = "Aura (Neural Hologram)",
    val gender: String = "Female",
    val pitch: Float = 1.05f,
    val speed: Float = 1.0f,
    val description: String = "Futuristic, warm, and highly analytical AI voice"
)

// ==========================================
// TOOL CALLING & PLUGIN FRAMEWORK MODELS
// ==========================================
enum class ToolCategory(val label: String, val iconEmoji: String) {
    SYSTEM("System Execution", "⚙️"),
    SEARCH("Search & Web Crawl", "🔍"),
    CODE("Code & Compiler", "💻"),
    STORAGE("Cloud Storage", "☁️"),
    DATABASE("Database Ops", "🗄️"),
    MULTIMODAL("Vision & Media", "🎨"),
    CUSTOM("Community Plugins", "🔌")
}

data class AiToolDefinition(
    val id: String,
    val name: String,
    val description: String,
    val category: ToolCategory,
    val isEnabled: Boolean = true,
    val parametersSchema: String = "{}",
    val sampleUsage: String = ""
)

data class PluginManifest(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val version: String = "1.0.0",
    val author: String = "VirgoYT Ecosystem",
    val description: String,
    val iconEmoji: String,
    val category: ToolCategory,
    val isInstalled: Boolean = true,
    val toolsProvided: List<String> = emptyList(),
    val apiEndpoint: String? = null
)

data class ToolCallRecord(
    val id: String = UUID.randomUUID().toString(),
    val toolName: String,
    val arguments: String,
    val output: String,
    val executionTimeMs: Long,
    val status: TaskStatus = TaskStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// AUTOMATION WORKFLOW MODELS
// ==========================================
enum class WorkflowTriggerType(val label: String, val iconEmoji: String) {
    CRON_SCHEDULE("Scheduled Cron Job", "⏰"),
    WEBHOOK("Inbound Webhook HTTP", "🪝"),
    FILE_CHANGE("File Watcher Event", "📁"),
    GIT_PUSH("Git Commit / Push", "🐙"),
    MANUAL("Manual Trigger", "▶️")
}

enum class WorkflowActionType(val label: String, val iconEmoji: String) {
    AI_AGENT_EXECUTE("Run Autonomous Agent Swarm", "⚡"),
    EXECUTE_SHELL("Run Sandboxed Bash Command", "⌨️"),
    DATABASE_BACKUP("Snapshot Database & Vector Table", "🗄️"),
    BUILD_AND_TEST("Compile Project & Run Unit Tests", "🏗️"),
    SYNC_CLOUD_BUCKET("Sync to S3/GCS Cloud Storage", "☁️"),
    DISPATCH_NOTIFICATION("Send Webhook / Slack / Push", "📢")
}

data class WorkflowStep(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val actionType: WorkflowActionType,
    val configPayload: String,
    val isEnabled: Boolean = true
)

data class WorkflowPipeline(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val triggerType: WorkflowTriggerType,
    val cronSchedule: String = "0 */6 * * *", // every 6 hours default
    val steps: List<WorkflowStep> = emptyList(),
    val isActive: Boolean = true,
    val lastRunTimestamp: Long? = null,
    val lastRunStatus: TaskStatus? = null,
    val totalRuns: Int = 0
)

data class WorkflowRunLog(
    val id: String = UUID.randomUUID().toString(),
    val workflowName: String,
    val triggerSource: String,
    val status: TaskStatus,
    val executionTimeMs: Long,
    val logOutput: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// CLOUD STORAGE DATA MODELS
// ==========================================
enum class StorageProviderType(val label: String, val icon: String) {
    S3_COMPATIBLE("AWS S3 / Cloudflare R2", "🪣"),
    GOOGLE_CLOUD_STORAGE("Google Cloud Storage (GCS)", "☁️"),
    AZURE_BLOB("Azure Blob Storage", "🔷"),
    VIRGO_LOCAL_CLUSTER("VirgoYT NVMe Fast Storage", "⚡")
}

data class CloudStorageObject(
    val key: String,
    val sizeBytes: Long,
    val mimeType: String,
    val lastModified: Long = System.currentTimeMillis(),
    val etag: String = UUID.randomUUID().toString().substring(0, 12)
)

data class CloudStorageBucket(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val provider: StorageProviderType,
    val region: String = "us-central1",
    val objectCount: Int = 14,
    val totalSizeBytes: Long = 859420800L, // ~820 MB
    val objects: List<CloudStorageObject> = emptyList(),
    val isPublic: Boolean = false
)

// ==========================================
// USER PREFERENCE LEARNING & PROFILE MODELS
// ==========================================
enum class AiTone(val label: String) {
    ULTRA_CONCISE("Ultra-Concise & Surgical"),
    DETAILED_ARCHITECT("In-Depth System Architect"),
    CREATIVE_GAME_DEV("Creative Game & 3D Designer"),
    ACADEMIC_SCIENTIST("Academic & Mathematical")
}

enum class CodeStylePreference(val label: String) {
    CLEAN_MODULAR("Modern Clean Architecture & Functional"),
    MINIMAL_BOILERPLATE("High Performance & Zero Overhead"),
    VERBOSE_DOCS("Extensively Documented & Typed")
}

enum class AppThemeMode(
    val id: String,
    val displayName: String,
    val shortName: String,
    val iconEmoji: String,
    val description: String
) {
    HOLOGRAPHIC_DARK(
        id = "holographic_dark",
        displayName = "Holographic Dark",
        shortName = "Dark",
        iconEmoji = "🌙",
        description = "Obsidian cybernetic canvas with neon cyan and indigo holographic accents"
    ),
    ENGINEER_LIGHT(
        id = "engineer_light",
        displayName = "Engineer Mode (Light)",
        shortName = "Light",
        iconEmoji = "☀️",
        description = "High-contrast daylight workbench with slate-100 paper and blueprint blue accents"
    );

    val isDark: Boolean get() = this == HOLOGRAPHIC_DARK
}

data class UserPreferences(
    val tone: AiTone = AiTone.ULTRA_CONCISE,
    val codeStyle: CodeStylePreference = CodeStylePreference.CLEAN_MODULAR,
    val defaultModel: AiModelTier = AiModelTier.BAZAARLINK_AI,
    val themeMode: AppThemeMode = AppThemeMode.HOLOGRAPHIC_DARK,
    val autoRunSafeTools: Boolean = true,
    val streamTypingSpeedMs: Long = 18L,
    val voiceEnabled: Boolean = true,
    val activeVoice: VoiceProfile = VoiceProfile(),
    val customSystemPrompt: String = "You are VirgoYT AI, an autonomous supercomputer assistant. Provide clean, production-ready solutions."
)


