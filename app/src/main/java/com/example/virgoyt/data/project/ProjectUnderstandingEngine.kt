package com.example.virgoyt.data.project

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SupportedFramework(val title: String, val iconEmoji: String) {
    NEXTJS("Next.js 15 (Turbopack)", "▲"),
    FASTAPI("FastAPI (Python 3.12)", "⚡"),
    JETPACK_COMPOSE("Jetpack Compose M3", "📱"),
    THREE_JS("Three.js WebGL", "🌐"),
    UNREAL_ENGINE("Unreal Engine 5", "🎮")
}

enum class SupportedLanguage(val title: String, val extension: String) {
    KOTLIN("Kotlin", ".kt"),
    TYPESCRIPT("TypeScript", ".tsx"),
    PYTHON("Python", ".py"),
    SQL("SQL", ".sql"),
    RUST("Rust", ".rs")
}

data class DependencyItem(
    val name: String,
    val version: String,
    val type: String = "production",
    val vulnerabilityStatus: String = "Passed (0 CVEs)"
)

data class ApiEndpointInfo(
    val method: String,
    val path: String,
    val handlerFile: String,
    val description: String
)

data class CodeRelationship(
    val sourceFile: String,
    val targetFile: String,
    val relationshipType: String,
    val confidence: Float = 0.98f
)

data class ProjectScanSummary(
    val totalFiles: Int = 18,
    val linesOfCode: Int = 4520,
    val frameworks: List<SupportedFramework> = listOf(SupportedFramework.NEXTJS, SupportedFramework.FASTAPI, SupportedFramework.JETPACK_COMPOSE),
    val dependencies: List<DependencyItem> = listOf(
        DependencyItem("next", "^15.1.0"),
        DependencyItem("react", "^19.0.0"),
        DependencyItem("fastapi", "0.115.0"),
        DependencyItem("three", "^0.170.0"),
        DependencyItem("androidx.compose.material3", "1.3.0")
    ),
    val endpoints: List<ApiEndpointInfo> = listOf(
        ApiEndpointInfo("GET", "/api/v1/health", "server.py", "Service health and container telemetry"),
        ApiEndpointInfo("POST", "/api/v1/deploy", "server.py", "Initiates swarm container deployment")
    ),
    val relationships: List<CodeRelationship> = listOf(
        CodeRelationship("App.tsx", "server.py", "Calls API /api/v1/deploy"),
        CodeRelationship("VirtualFileSystem.kt", "Models.kt", "Imports VirtualFile model")
    ),
    val healthScore: Int = 98
)

class ProjectUnderstandingEngine {

    private val _scanSummary = MutableStateFlow(ProjectScanSummary())
    val scanSummary: StateFlow<ProjectScanSummary> = _scanSummary.asStateFlow()

    fun runFullScan() {
        _scanSummary.value = ProjectScanSummary()
    }
}
