package com.example.virgoyt.data.appgen

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class AppCategory(val title: String, val iconEmoji: String) {
    SAAS_PLATFORM("SaaS Platform", "💼"),
    AI_APPLICATION("AI Agent App", "🤖"),
    E_COMMERCE("E-Commerce Store", "🛍️"),
    THREE_D_EXPERIENCE("3D & Game Web", "🎮"),
    MOBILE_HYBRID("Mobile Hybrid App", "📱")
}

data class AppTemplate(
    val id: String,
    val title: String,
    val category: AppCategory,
    val description: String,
    val iconEmoji: String,
    val techStack: List<String>,
    val previewThumbnailUrl: String = ""
)

data class GeneratedAppInstance(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val templateId: String,
    val status: String = "Ready",
    val previewUrl: String = "https://sandbox.virgoyt.ai/app/${UUID.randomUUID().toString().take(8)}",
    val createdAt: Long = System.currentTimeMillis()
)

class ApplicationGenerationEngine {

    private val _templates = MutableStateFlow<List<AppTemplate>>(
        listOf(
            AppTemplate(
                id = "nextjs-saas-starter",
                title = "Next.js 15 AI SaaS Starter",
                category = AppCategory.SAAS_PLATFORM,
                description = "Full authentication, Stripe billing, Tailwind CSS UI, and Gemini AI integration",
                iconEmoji = "▲",
                techStack = listOf("Next.js 15", "TypeScript", "Tailwind", "PostgreSQL")
            ),
            AppTemplate(
                id = "fastapi-agent-backend",
                title = "FastAPI Multi-Agent Backend",
                category = AppCategory.AI_APPLICATION,
                description = "High-performance async Python backend with WebSockets and autonomous tool calling",
                iconEmoji = "⚡",
                techStack = listOf("FastAPI", "Python 3.12", "Pydantic", "Redis")
            ),
            AppTemplate(
                id = "compose-android-suite",
                title = "Jetpack Compose Android App",
                category = AppCategory.MOBILE_HYBRID,
                description = "Material 3 dynamic theming, Room database, Coroutines Flow, and offline sync",
                iconEmoji = "📱",
                techStack = listOf("Kotlin", "Jetpack Compose", "Room", "Coroutines")
            )
        )
    )
    val templates: StateFlow<List<AppTemplate>> = _templates.asStateFlow()

    private val _generatedApps = MutableStateFlow<List<GeneratedAppInstance>>(
        listOf(
            GeneratedAppInstance(name = "VirgoYT Cloud SaaS", templateId = "nextjs-saas-starter")
        )
    )
    val generatedApps: StateFlow<List<GeneratedAppInstance>> = _generatedApps.asStateFlow()

    fun generateAppFromTemplate(template: AppTemplate, appName: String): GeneratedAppInstance {
        val app = GeneratedAppInstance(name = appName, templateId = template.id)
        _generatedApps.value = _generatedApps.value + app
        return app
    }
}
