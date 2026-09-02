package com.example.virgoyt.data.model

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
    MONITOR("Telemetry", "📊");
}

enum class AiModelTier(
    val modelId: String,
    val displayName: String,
    val provider: String,
    val badgeEmoji: String,
    val specialty: String,
    val contextWindowTokens: Int,
    val apiEndpoint: String,
    val defaultModelParam: String,
    val isVisionSupported: Boolean = false,
    val isDeepReasoning: Boolean = false,
    val maxOutputTokens: Int = 8192,
    val inputCostPerMillion: Double = 0.0
) {
    AUTO_ROUTER("auto", "Auto Intelligent Router", "VirgoYT Omni", "⚡", "Dynamically routes each task across BazaarLink, OpenRouter, Groq, Kie.ai, and NVIDIA", 0, "https://api.bazaarlink.ai/v1", "", true, true, 8192, 0.0),
    BAZAARLINK_AI("bazaarlink/openai-compatible", "BazaarLink AI Gateway", "BazaarLink.ai", "🌌", "Direct universal API endpoint to all global frontier models", 1000000, "https://api.bazaarlink.ai/v1", "gpt-4o", true, true, 8192, 0.0),
    GEMINI_2_5_FLASH("gemini-2.5-flash", "Gemini 2.5 Flash", "Google DeepMind", "⚡", "Ultra-fast multimodal reasoning with adaptive thinking", 1000000, "https://generativelanguage.googleapis.com/v1beta", "models/gemini-2.5-flash", true, false, 8192, 0.075),
    GEMINI_2_5_PRO("gemini-2.5-pro", "Gemini 2.5 Pro", "Google DeepMind", "👑", "Frontier reasoning & massive multi-agent project architecture", 2000000, "https://generativelanguage.googleapis.com/v1beta", "models/gemini-2.5-pro", true, true, 8192, 1.25),
    CLAUDE_3_7_SONNET("claude-3-7-sonnet-20250219", "Claude 3.7 Sonnet Hybrid", "Anthropic", "🧠", "State-of-the-art hybrid standard & extended thinking for code", 200000, "https://api.anthropic.com/v1", "claude-3-7-sonnet-20250219", true, true, 8192, 3.0),
    KIE_DEEPSEEK_R1("deepseek-reasoner", "DeepSeek R1 (via Kie.ai)", "DeepSeek / Kie.ai", "🧬", "Open-weights chain-of-thought mathematical reasoning", 128000, "https://api.kie.ai/v1", "deepseek-reasoner", false, true, 8192, 0.55),
    KIE_DEEPSEEK_V3("deepseek-chat", "DeepSeek V3 (via Kie.ai)", "DeepSeek / Kie.ai", "⚡", "Blazing fast 671B MoE architecture general intelligence", 128000, "https://api.kie.ai/v1", "deepseek-chat", false, false, 8192, 0.14),
    OPENROUTER_ROUTER("openrouter/auto", "OpenRouter Multi-Gateway", "OpenRouter", "🌐", "Global routing to 200+ top open and closed AI models", 128000, "https://openrouter.ai/api/v1", "openrouter/auto", true, true, 8192, 0.0),
    GROQ_LLAMA_3_3_70B("llama-3.3-70b-versatile", "Llama 3.3 70B (Groq LPU)", "Meta / Groq", "🚀", "Sub-second 500+ tokens/sec LPUs inference speed", 128000, "https://api.groq.com/openai/v1", "llama-3.3-70b-versatile", false, false, 8192, 0.59),
    NVIDIA_NIM_LLAMA_70B("meta/llama-3.3-70b-instruct", "NVIDIA NIM Cloud", "NVIDIA NIM", "🟢", "Enterprise GPU accelerated inference container", 128000, "https://integrate.api.nvidia.com/v1", "meta/llama-3.3-70b-instruct", false, false, 8192, 0.70),
    QWEN_2_5_CODER("qwen-2.5-coder-32b-instruct", "Qwen 2.5 Coder 32B", "Alibaba Qwen", "💻", "Specialized competitive coding & repository reasoning", 128000, "https://openrouter.ai/api/v1", "qwen/qwen-2.5-coder-32b-instruct", false, false, 8192, 0.18),
    MISTRAL_LARGE_2411("mistral-large-latest", "Mistral Large 2", "Mistral AI", "🇫🇷", "European multilingual flagship reasoning system", 128000, "https://api.mistral.ai/v1", "mistral-large-latest", false, false, 8192, 2.0);
}

enum class AiTone(val label: String, val promptModifier: String) {
    BALANCED("Balanced Engineer", "Provide balanced, production-grade technical answers with clear code and reasoning."),
    CONCISE("Ultra Concise", "Be extremely concise, direct, and focused strictly on the code and commands with minimal prose."),
    CREATIVE("Creative Architect", "Think outside the box, propose bold architectural solutions, high-polish UI ideas and futuristic patterns."),
    STRICT_SECURITY("Security Auditor", "Analyze everything through strict security, OWASP, and enterprise robustness lenses.");
}

enum class AppThemeMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val description: String
) {
    HOLOGRAPHIC_DARK("holographic_dark", "Holographic Dark", "Dark", "🌙", "Obsidian cybernetic canvas with neon cyan and indigo holographic accents"),
    ENGINEER_LIGHT("engineer_light", "Engineer Mode (Light)", "Light", "☀️", "High-contrast daylight workbench with slate-100 paper and blueprint blue accents");

    val isDark: Boolean get() = this == HOLOGRAPHIC_DARK
}

enum class AttachmentType(val iconEmoji: String) {
    CODE_SNIPPET("📄"),
    IMAGE("🖼️"),
    TERMINAL_LOG("⌨️"),
    SCHEMA("🗄️"),
    DIFF("📝")
}

data class PromptAttachment(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AttachmentType,
    val content: String,
    val mimeType: String = "text/plain",
    val sizeBytes: Long = content.length.toLong()
)

data class QuickActionChip(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val actionCommand: String,
    val iconEmoji: String
)

data class SmartAiOption(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val actionPrompt: String,
    val recommendedConfidence: Float = 0.95f
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String,
    val modelUsed: AiModelTier = AiModelTier.GEMINI_2_5_FLASH,
    val attachments: List<PromptAttachment> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val toolExecutions: List<String> = emptyList(),
    val emotionalResonance: String? = null,
    val detectedIntent: String? = null,
    val recommendedOptions: List<SmartAiOption> = emptyList(),
    val quickActionChips: List<QuickActionChip> = emptyList(),
    val realtimeTelemetrySnapshot: String? = null,
    val confidenceScore: Float = 0.98f
)

data class ChatSession(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList(),
    val currentActiveTier: AiModelTier = AiModelTier.AUTO_ROUTER
)

enum class OutputType {
    INPUT,
    STDOUT,
    STDERR,
    SYSTEM,
    SUCCESS,
    WARNING,
    JSON_OUTPUT,
    AI_THOUGHT,
    TOOL_CALL,
    DIFF_VIEW,
    PROGRESS,
    TABLE_OUTPUT,
    METRICS_OUTPUT
}

data class TerminalEntry(
    val id: String = UUID.randomUUID().toString(),
    val type: OutputType,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val command: String? = null,
    val executionTimeMs: Long? = null,
    val exitCode: Int? = null,
    val metadata: Map<String, String> = emptyMap()
)

enum class TerminalMode(
    val label: String,
    val host: String,
    val promptPrefix: String,
    val description: String
) {
    CLOUD_VM("Cloud VM", "asia-east1.cloud-node", "virgoyt-cloud-ai", "Ubuntu 24.04 LTS (Remote Sandbox Container)"),
    LOCALHOST("Localhost :8080", "127.0.0.1:8080", "localhost", "Local Machine Bridge (Linux/Mac/Win/Termux)");
}

enum class TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    SKIPPED
}

data class AgentSubtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val toolType: String,
    var status: TaskStatus = TaskStatus.PENDING,
    var outputLog: String = "",
    val executionDurationMs: Long = 0L
)

data class AgentTask(
    val id: String = UUID.randomUUID().toString(),
    val userGoal: String,
    val planSteps: List<AgentSubtask> = emptyList(),
    val status: TaskStatus = TaskStatus.PENDING,
    val currentStepIndex: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val totalToolCalls: Int = 0,
    val executionGraphSummary: String = ""
)

data class VirtualFile(
    val path: String,
    val name: String,
    val isDirectory: Boolean = false,
    val content: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val sizeBytes: Long = content.length.toLong(),
    val owner: String = "root",
    val permissions: String = if (isDirectory) "drwxr-xr-x" else "-rw-r--r--"
)

data class SystemStats(
    val cpuUsagePercent: Int = 14,
    val memoryUsedMb: Long = 642,
    val memoryTotalMb: Long = 8192,
    val diskUsedGb: Double = 4.2,
    val diskTotalGb: Double = 100.0,
    val uptimeSeconds: Long = 18450,
    val networkDownloadKbps: Double = 1240.5,
    val networkUploadKbps: Double = 312.4,
    val activeThreads: Int = 24,
    val activeBackgroundAgents: Int = 3
)

data class ProcessInfo(
    val pid: Int,
    val name: String,
    val cpuPercent: Double,
    val memoryMb: Double,
    val user: String,
    val status: String,
    val command: String
)

data class VoiceProfile(
    val id: String,
    val name: String,
    val locale: String,
    val pitch: Float = 1.0f,
    val rate: Float = 1.0f,
    val gender: String = "Neutral",
    val previewSample: String = "Hello! I am your VirgoYT Cloud AI assistant."
)

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

data class ToolCallRecord(
    val toolName: String,
    val arguments: Map<String, Any?>,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val isSuccess: Boolean = true
)

data class DiffFileSnapshot(
    val filePath: String,
    val originalContent: String,
    val modifiedContent: String,
    val additions: Int = 0,
    val deletions: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

enum class WorkflowActionType(val label: String, val iconEmoji: String) {
    RUN_SHELL("Run Shell Command", "⌨️"),
    CALL_AI_MODEL("Call AI Model", "🧠"),
    EDIT_FILE("Edit / Transform File", "📝"),
    HTTP_WEBHOOK("HTTP Webhook", "🌐"),
    GIT_COMMIT("Git Commit & Push", "📦"),
    DATABASE_QUERY("Execute SQL / DB Query", "🗄️"),
    SEND_NOTIFICATION("System Notification", "🔔"),
    TRIGGER_RAG_INGEST("Trigger RAG Ingest", "📚")
}

enum class WorkflowTriggerType(val label: String, val iconEmoji: String) {
    MANUAL("Manual Trigger", "👆"),
    ON_FILE_CHANGE("On File Saved", "💾"),
    SCHEDULED_CRON("Cron Schedule", "⏰"),
    ON_GIT_PUSH("On Git Push", "🚀"),
    ON_VOICE_COMMAND("On Voice Keyword", "🎙️"),
    ON_AI_COMPLETION("On Agent Task Complete", "✅")
}

data class WorkflowStep(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val actionType: WorkflowActionType,
    val configPayload: String,
    val isEnabled: Boolean = true
)

data class WorkflowPipeline(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val triggerType: WorkflowTriggerType,
    val triggerConfig: String = "",
    val steps: List<WorkflowStep> = emptyList(),
    val isEnabled: Boolean = true,
    val lastRunTimestamp: Long? = null,
    val totalRunsCount: Int = 0
)

data class WorkflowRunLog(
    val id: String = UUID.randomUUID().toString(),
    val pipelineId: String,
    val pipelineTitle: String,
    val startedAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 0L,
    val status: TaskStatus = TaskStatus.COMPLETED,
    val logs: List<String> = emptyList()
)

data class CliPlatformInstall(
    val id: String,
    val platformName: String,
    val iconEmoji: String,
    val installCommand: String,
    val description: String,
    val supportedArches: List<String> = listOf("x86_64", "arm64", "aarch64")
)

data class SecretCredentialPrompt(
    val keyName: String,
    val displayName: String,
    val description: String,
    val serviceProvider: String,
    val isConfigured: Boolean = false,
    val maskedValue: String = ""
)

data class BrowserConsoleMessage(
    val level: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceUrl: String = ""
)

enum class GameEngineType(val title: String, val extension: String, val iconEmoji: String) {
    THREE_JS("Three.js WebGL", ".js", "🌐"),
    UNREAL_CPP("Unreal Engine C++ & Blueprint", ".cpp", "🎮"),
    BABYLON_JS("Babylon.js Engine", ".ts", "🪐"),
    UNITY_CSHARP("Unity C# Script", ".cs", "🕹️"),
    CANVAS_2D("HTML5 2D Canvas Engine", ".html", "🎨")
}

data class Game3DModel(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String,
    val vertexCount: Int,
    val polyCount: Int,
    val previewUrl: String = "",
    val engineScript: String = ""
)

data class GoogleEarthGISMap(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMeters: Double,
    val terrainResolution: String,
    val vegetationDensity: Float,
    val proceduralSeed: Long
)

data class GameProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val engineType: GameEngineType = GameEngineType.THREE_JS,
    val description: String = "",
    val models: List<Game3DModel> = emptyList(),
    val currentSceneCode: String = "",
    val earthGISMap: GoogleEarthGISMap? = null,
    val fpsTarget: Int = 60
)

enum class StorageProviderType(val displayName: String, val iconEmoji: String) {
    LOCAL_VFS("VirgoYT Sandbox Storage", "💾"),
    AWS_S3("Amazon Web Services S3", "🟧"),
    GOOGLE_CLOUD_STORAGE("Google Cloud Storage (GCS)", "🔷"),
    CLOUDFLARE_R2("Cloudflare R2 Object Storage", "🟨"),
    MINIO_SELF_HOSTED("MinIO Distributed Storage", "🔴")
}

data class CloudStorageBucket(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val provider: StorageProviderType,
    val region: String,
    val totalObjects: Int = 0,
    val totalBytes: Long = 0L,
    val isPublic: Boolean = false
)

data class CloudStorageObject(
    val id: String = UUID.randomUUID().toString(),
    val bucketId: String,
    val key: String,
    val sizeBytes: Long,
    val contentType: String,
    val lastModified: Long = System.currentTimeMillis(),
    val storageClass: String = "STANDARD",
    val etag: String = UUID.randomUUID().toString().replace("-", "").substring(0, 16)
)

enum class ToolCategory(val title: String, val iconEmoji: String) {
    CODING("Code Intelligence", "💻"),
    DEVOPS("DevOps & Cloud", "☁️"),
    BROWSER_AUTOMATION("Browser & Crawling", "🌐"),
    DATA_AI("Data & Database AI", "🗄️"),
    GRAPHICS_3D("3D & Game Studio", "🎮"),
    SECURITY("Security & Crypto", "🔒")
}

data class AiToolDefinition(
    val name: String,
    val description: String,
    val category: ToolCategory,
    val parametersSchema: String,
    val isBuiltIn: Boolean = true,
    val isEnabled: Boolean = true,
    val executionCount: Int = 0
)

data class PluginManifest(
    val id: String,
    val name: String,
    val version: String,
    val author: String,
    val description: String,
    val iconEmoji: String,
    val category: ToolCategory,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = true,
    val toolsProvided: List<AiToolDefinition> = emptyList()
)

enum class CodeStylePreference(val label: String) {
    IDLE("Default Modern Standard"),
    FUNCTIONAL("Strict Functional & Immutability"),
    TYPE_SAFE_CONSERVATIVE("Ultra Defensive Type-Safe"),
    CLEAN_CONCISE("Concise & High Readability")
}

data class UserPreferences(
    val codeStyle: CodeStylePreference = CodeStylePreference.IDLE,
    val preferredAiTone: AiTone = AiTone.BALANCED,
    val autoRunTests: Boolean = true,
    val autoFormatOnSave: Boolean = true,
    val streamAudioResponses: Boolean = false,
    val defaultTerminalFont: String = "JetBrains Mono",
    val defaultEditorTheme: String = "Holographic Cyberpunk"
)

data class User(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val fullName: String,
    val role: String = "Cloud Architect",
    val avatarUrl: String = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
    val homeDir: String = "/workspace/users/$username",
    val createdAt: Long = System.currentTimeMillis(),
    val preferences: UserPreferences = UserPreferences()
)

data class AuthSession(
    val token: String = UUID.randomUUID().toString(),
    val user: User,
    val expiresAt: Long = System.currentTimeMillis() + 86400000L
)
