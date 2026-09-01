package com.example.manus.data.plugins

import com.example.manus.data.model.AiToolDefinition
import com.example.manus.data.model.PluginManifest
import com.example.manus.data.model.TaskStatus
import com.example.manus.data.model.ToolCategory
import com.example.manus.data.model.ToolCallRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class PluginManager {
    private val _installedPlugins = MutableStateFlow<List<PluginManifest>>(
        listOf(
            PluginManifest(
                name = "Web Search & Scraper Pro",
                version = "2.4.0",
                author = "VirgoYT Research Labs",
                description = "Real-time web crawling, DuckDuckGo search queries, DOM tree extraction, and markdown summarization.",
                iconEmoji = "🔍",
                category = ToolCategory.SEARCH,
                isInstalled = true,
                toolsProvided = listOf("web_search", "fetch_url_dom", "extract_structured_json")
            ),
            PluginManifest(
                name = "Cloud Terminal & Sandboxed Bash",
                version = "3.1.0",
                author = "VirgoYT Core",
                description = "Isolated Linux container command execution, environment variable exports, and package management.",
                iconEmoji = "⌨️",
                category = ToolCategory.SYSTEM,
                isInstalled = true,
                toolsProvided = listOf("bash_exec", "export_env", "list_processes", "kill_process")
            ),
            PluginManifest(
                name = "GitHub & Git VCS Engine",
                version = "1.8.2",
                author = "VirgoYT DevTools",
                description = "Automated git commits, branch management, pull request drafting, and remote GitHub synchronizations.",
                iconEmoji = "🐙",
                category = ToolCategory.CODE,
                isInstalled = true,
                toolsProvided = listOf("git_commit", "git_push", "create_pull_request", "sync_repo")
            ),
            PluginManifest(
                name = "Vector Database & RAG Indexer",
                version = "2.0.0",
                author = "VirgoYT AI",
                description = "1536-dimensional embedding storage, cosine similarity search, and automated memory consolidation.",
                iconEmoji = "🧠",
                category = ToolCategory.DATABASE,
                isInstalled = true,
                toolsProvided = listOf("vector_search", "upsert_embedding", "delete_vector_entry")
            ),
            PluginManifest(
                name = "Cloud Object Storage (S3/GCS)",
                version = "1.5.0",
                author = "VirgoYT Cloud Ops",
                description = "Direct multipart uploads, bucket snapshotting, and cloud asset distribution across AWS S3 and GCS.",
                iconEmoji = "☁️",
                category = ToolCategory.STORAGE,
                isInstalled = true,
                toolsProvided = listOf("s3_upload_object", "gcs_list_buckets", "download_snapshot")
            ),
            PluginManifest(
                name = "Multimodal Vision & 3D Synthesis",
                version = "2.1.0",
                author = "VirgoYT Game Studio",
                description = "Procedural GLB generation, texture baking, image understanding, and PBR shader compilation.",
                iconEmoji = "🎨",
                category = ToolCategory.MULTIMODAL,
                isInstalled = true,
                toolsProvided = listOf("generate_3d_mesh", "analyze_image", "compile_pbr_shader")
            ),
            PluginManifest(
                name = "SQL / NoSQL AI Studio",
                version = "1.2.0",
                author = "VirgoYT Database Team",
                description = "Automated query optimization, schema generation, migration execution, and JSONB index management.",
                iconEmoji = "🗄️",
                category = ToolCategory.DATABASE,
                isInstalled = true,
                toolsProvided = listOf("run_sql_query", "generate_migration", "explain_query_plan")
            )
        )
    )
    val installedPlugins: StateFlow<List<PluginManifest>> = _installedPlugins.asStateFlow()

    private val _availableTools = MutableStateFlow<List<AiToolDefinition>>(
        listOf(
            AiToolDefinition("web_search", "Web Search", "Query live web indices for real-time information", ToolCategory.SEARCH, true, "{\"query\": \"string\"}"),
            AiToolDefinition("bash_exec", "Bash Sandbox", "Run shell commands in safe container environment", ToolCategory.SYSTEM, true, "{\"command\": \"string\"}"),
            AiToolDefinition("file_write", "File System Write", "Create or edit files in virtual workspace", ToolCategory.CODE, true, "{\"path\": \"string\", \"content\": \"string\"}"),
            AiToolDefinition("git_commit", "Git VCS Commit", "Stage and commit changes to local Git workspace", ToolCategory.CODE, true, "{\"message\": \"string\"}"),
            AiToolDefinition("sql_exec", "SQL Query Runner", "Execute database transactions and queries", ToolCategory.DATABASE, true, "{\"query\": \"string\"}"),
            AiToolDefinition("vector_search", "Vector Memory Search", "Cosine similarity search on 1536-D embeddings", ToolCategory.DATABASE, true, "{\"query\": \"string\", \"topK\": 5}"),
            AiToolDefinition("s3_sync", "S3 Storage Sync", "Sync local virtual files to cloud storage buckets", ToolCategory.STORAGE, true, "{\"bucket\": \"string\"}"),
            AiToolDefinition("mesh_gen_3d", "3D Mesh Generator", "Generate procedural GLB 3D models with PBR textures", ToolCategory.MULTIMODAL, true, "{\"prompt\": \"string\"}")
        )
    )
    val availableTools: StateFlow<List<AiToolDefinition>> = _availableTools.asStateFlow()

    private val _recentToolCalls = MutableStateFlow<List<ToolCallRecord>>(
        listOf(
            ToolCallRecord(
                toolName = "vector_search",
                arguments = "query=\"UE5 Nanite landscape\"",
                output = "Found 3 matching vectors (similarity > 0.89)",
                executionTimeMs = 42
            ),
            ToolCallRecord(
                toolName = "bash_exec",
                arguments = "command=\"npm run build\"",
                output = "✓ Compiled 48 modules successfully in 1.4s",
                executionTimeMs = 1420
            ),
            ToolCallRecord(
                toolName = "file_write",
                arguments = "path=\"/workspace/src/App.tsx\"",
                output = "✓ Written 148 lines (4.2 KB)",
                executionTimeMs = 8
            )
        )
    )
    val recentToolCalls: StateFlow<List<ToolCallRecord>> = _recentToolCalls.asStateFlow()

    fun togglePlugin(id: String) {
        _installedPlugins.value = _installedPlugins.value.map {
            if (it.id == id) it.copy(isInstalled = !it.isInstalled) else it
        }
    }

    fun toggleTool(id: String) {
        _availableTools.value = _availableTools.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun recordToolExecution(name: String, args: String, output: String, durationMs: Long, status: TaskStatus = TaskStatus.COMPLETED) {
        val record = ToolCallRecord(
            toolName = name,
            arguments = args,
            output = output,
            executionTimeMs = durationMs,
            status = status
        )
        _recentToolCalls.value = listOf(record) + _recentToolCalls.value.take(40)
    }

    fun installCustomPlugin(name: String, description: String, category: ToolCategory, endpoint: String?) {
        val plugin = PluginManifest(
            name = name,
            description = description,
            category = category,
            iconEmoji = "🔌",
            apiEndpoint = endpoint,
            toolsProvided = listOf("${name.lowercase().replace(" ", "_")}_tool")
        )
        _installedPlugins.value = _installedPlugins.value + plugin
    }
}
