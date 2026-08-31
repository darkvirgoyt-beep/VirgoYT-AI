package com.example.manus.data.rag

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import kotlin.math.sqrt

enum class MemoryCategory(val label: String, val emoji: String) {
    USER_PREFERENCE("User Preferences", "👤"),
    PROJECT_ARCHITECTURE("Project Architecture", "🏛️"),
    CODE_KNOWLEDGE("Codebase Embeddings", "💻"),
    CONVERSATION_HISTORY("Conversation Context", "💬"),
    TEAM_COLLABORATION("Multi-Agent Memory", "🐝"),
    DOCUMENTATION_INDEX("Docs & RFC Index", "📚")
}

data class MemoryEntry(
    val id: String = UUID.randomUUID().toString(),
    val category: MemoryCategory,
    val key: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val importanceScore: Float = 0.95f,
    val accessCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val vectorDimension: Int = 1536
)

data class RagSearchResult(
    val entry: MemoryEntry,
    val similarityScore: Float,
    val matchedTokens: List<String>
)

class MemoryAndRagEngine {
    private val _memories = MutableStateFlow<List<MemoryEntry>>(
        listOf(
            MemoryEntry(
                category = MemoryCategory.USER_PREFERENCE,
                key = "coding_style_preferences",
                content = "User prefers ultra-clean, modern Kotlin Jetpack Compose & TypeScript architecture. Strongly favors Material 3 tokens, explicit error handling, and zero boilerplate.",
                tags = listOf("style", "m3", "kotlin", "typescript")
            ),
            MemoryEntry(
                category = MemoryCategory.PROJECT_ARCHITECTURE,
                key = "system_topology",
                content = "VirgoYT AI operates as a unified cloud intelligence hub with 15 specialized sub-agents, live VFS file trees, containerized terminal emulator, and real-time diff playback.",
                tags = listOf("topology", "agents", "vfs", "architecture")
            ),
            MemoryEntry(
                category = MemoryCategory.CODE_KNOWLEDGE,
                key = "game_engine_nanite_lumen",
                content = "Unreal Engine 5.4 integration utilizes Nanite geometry streaming and Lumen dynamic global illumination with 60-120 FPS target optimization on cloud A100 GPUs.",
                tags = listOf("ue5", "nanite", "lumen", "graphics")
            ),
            MemoryEntry(
                category = MemoryCategory.DOCUMENTATION_INDEX,
                key = "database_indexing_guidelines",
                content = "PostgreSQL B-Tree and GIN indexes should be applied to high-cardinality foreign keys and JSONB payload search fields to maintain sub-5ms query times.",
                tags = listOf("postgres", "database", "indexes", "performance")
            ),
            MemoryEntry(
                category = MemoryCategory.TEAM_COLLABORATION,
                key = "agent_inter_comm_protocol",
                content = "All 15 agents exchange typed payloads through the shared Vector Hive with zero serialization overhead and isolated memory sandboxes.",
                tags = listOf("agents", "swarm", "memory", "protocol")
            ),
            MemoryEntry(
                category = MemoryCategory.CONVERSATION_HISTORY,
                key = "recent_task_context",
                content = "Initialized autonomous game studio, multi-model router, and live diff playback computer.",
                tags = listOf("context", "history", "recent")
            )
        )
    )
    val memories: StateFlow<List<MemoryEntry>> = _memories.asStateFlow()

    private val _lastQuery = MutableStateFlow("")
    val lastQuery: StateFlow<String> = _lastQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<RagSearchResult>>(emptyList())
    val searchResults: StateFlow<List<RagSearchResult>> = _searchResults.asStateFlow()

    fun storeMemory(category: MemoryCategory, key: String, content: String, tags: List<String> = emptyList()) {
        val entry = MemoryEntry(
            category = category,
            key = key,
            content = content,
            tags = tags
        )
        _memories.value = listOf(entry) + _memories.value
    }

    fun deleteMemory(id: String) {
        _memories.value = _memories.value.filterNot { it.id == id }
    }

    // High-performance simulated Vector Embedding cosine similarity calculation
    fun searchKnowledgeBase(query: String): List<RagSearchResult> {
        _lastQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return emptyList()
        }

        val queryTokens = query.lowercase().split(" ", ",", ".", "-", "_").filter { it.isNotBlank() }
        
        val results = _memories.value.mapNotNull { entry ->
            val contentTokens = (entry.content + " " + entry.key + " " + entry.tags.joinToString(" ")).lowercase()
            var matches = 0
            val matchedList = mutableListOf<String>()

            for (qt in queryTokens) {
                if (contentTokens.contains(qt)) {
                    matches++
                    matchedList.add(qt)
                }
            }

            if (matches > 0 || queryTokens.isEmpty()) {
                val score = (matches.toFloat() / queryTokens.size.coerceAtLeast(1).toFloat()).coerceIn(0.45f, 0.99f) + (entry.importanceScore * 0.05f)
                RagSearchResult(
                    entry = entry,
                    similarityScore = score.coerceAtMost(0.99f),
                    matchedTokens = matchedList
                )
            } else null
        }.sortedByDescending { it.similarityScore }

        _searchResults.value = results
        return results
    }
}
