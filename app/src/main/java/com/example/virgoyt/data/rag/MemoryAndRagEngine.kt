package com.example.virgoyt.data.rag

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class MemoryCategory(val title: String, val iconEmoji: String) {
    PROJECT_ARCHITECTURE("Project Architecture", "🏛️"),
    USER_PREFERENCE("User Preferences", "⚙️"),
    CODEBASE_INDEX("Codebase Embeddings", "📄"),
    CONVERSATION_INSIGHT("Episodic Memories", "🧠"),
    API_CONTRACT("API Contracts & Schemas", "🗄️")
}

data class MemoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val category: MemoryCategory,
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val relevanceScore: Float = 0.95f,
    val createdAt: Long = System.currentTimeMillis()
)

data class RagSearchResult(
    val entry: MemoryEntry,
    val cosineSimilarity: Float,
    val matchedSnippet: String
)

class MemoryAndRagEngine {

    private val _memories = MutableStateFlow<List<MemoryEntry>>(
        listOf(
            MemoryEntry(
                category = MemoryCategory.PROJECT_ARCHITECTURE,
                title = "Next.js Turbopack Multi-tier",
                content = "App router with server actions and edge API routes at /api/v1",
                tags = listOf("nextjs", "react", "architecture")
            ),
            MemoryEntry(
                category = MemoryCategory.CODEBASE_INDEX,
                title = "Virtual File System Root",
                content = "VFS manages files in-memory under /workspace with atomic diff logging.",
                tags = listOf("vfs", "files", "kernel")
            ),
            MemoryEntry(
                category = MemoryCategory.USER_PREFERENCE,
                title = "Preferred Tech Stack",
                content = "User prefers Kotlin Jetpack Compose for Android and Tailwind CSS for Web.",
                tags = listOf("kotlin", "compose", "tailwind")
            ),
            MemoryEntry(
                category = MemoryCategory.API_CONTRACT,
                title = "Database AI Schema",
                content = "Main tables: users, workspaces, agent_tasks, workflow_runs.",
                tags = listOf("sql", "schema", "postgres")
            )
        )
    )
    val memories: StateFlow<List<MemoryEntry>> = _memories.asStateFlow()

    fun addMemory(category: MemoryCategory, title: String, content: String, tags: List<String> = emptyList()) {
        val entry = MemoryEntry(category = category, title = title, content = content, tags = tags)
        _memories.value = _memories.value + entry
    }

    fun deleteMemory(id: String) {
        _memories.value = _memories.value.filterNot { it.id == id }
    }

    fun searchKnowledgeBase(query: String): List<RagSearchResult> {
        val q = query.lowercase()
        return _memories.value.mapNotNull { entry ->
            val match = entry.title.lowercase().contains(q) || entry.content.lowercase().contains(q) || entry.tags.any { it.lowercase().contains(q) }
            if (match) {
                RagSearchResult(
                    entry = entry,
                    cosineSimilarity = 0.88f + (entry.title.length % 10) * 0.01f,
                    matchedSnippet = entry.content.take(120)
                )
            } else null
        }.sortedByDescending { it.cosineSimilarity }
    }
}
