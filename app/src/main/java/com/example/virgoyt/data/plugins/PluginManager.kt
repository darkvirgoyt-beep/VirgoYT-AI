package com.example.virgoyt.data.plugins

import com.example.virgoyt.data.model.AiToolDefinition
import com.example.virgoyt.data.model.PluginManifest
import com.example.virgoyt.data.model.ToolCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PluginManager {

    private val _plugins = MutableStateFlow<List<PluginManifest>>(
        listOf(
            PluginManifest(
                id = "core-vfs-tools",
                name = "Virtual File System Kit",
                version = "2.1.0",
                author = "VirgoYT Core",
                description = "High-speed read, write, grep, sed, and diff inspection for cloud VFS",
                iconEmoji = "📁",
                category = ToolCategory.CODING,
                isInstalled = true,
                isEnabled = true,
                toolsProvided = listOf(
                    AiToolDefinition("read_file", "Read text content of a file", ToolCategory.CODING, "{\"path\": \"string\"}"),
                    AiToolDefinition("write_file", "Write text content to a file", ToolCategory.CODING, "{\"path\": \"string\", \"content\": \"string\"}"),
                    AiToolDefinition("list_files", "List files in directory", ToolCategory.CODING, "{\"path\": \"string\"}")
                )
            ),
            PluginManifest(
                id = "database-ai-pro",
                name = "PostgreSQL & SQLite AI",
                version = "1.5.0",
                author = "DataSwarm Labs",
                description = "Autonomous SQL generation, migrations, indexing advisor and query executor",
                iconEmoji = "🗄️",
                category = ToolCategory.DATA_AI,
                isInstalled = true,
                isEnabled = true,
                toolsProvided = listOf(
                    AiToolDefinition("execute_sql", "Run SQL query against sandbox database", ToolCategory.DATA_AI, "{\"sql\": \"string\"}"),
                    AiToolDefinition("generate_migration", "Create schema migration plan", ToolCategory.DATA_AI, "{\"spec\": \"string\"}")
                )
            ),
            PluginManifest(
                id = "web-crawler-sandbox",
                name = "Headless Browser & Crawler",
                version = "3.0.1",
                author = "Playwright AI",
                description = "Scrapes live documentation, validates web URLs and tests DOM elements",
                iconEmoji = "🌐",
                category = ToolCategory.BROWSER_AUTOMATION,
                isInstalled = true,
                isEnabled = true,
                toolsProvided = listOf(
                    AiToolDefinition("crawl_url", "Extract text and markdown from web page", ToolCategory.BROWSER_AUTOMATION, "{\"url\": \"string\"}")
                )
            ),
            PluginManifest(
                id = "unreal-three-bridge",
                name = "3D Graphics & GIS Engine",
                version = "1.1.0",
                author = "Epic WebGL",
                description = "Procedural Three.js scene builder, shader generator and Google Earth GIS importer",
                iconEmoji = "🎮",
                category = ToolCategory.GRAPHICS_3D,
                isInstalled = true,
                isEnabled = true,
                toolsProvided = listOf(
                    AiToolDefinition("build_3d_scene", "Generate WebGL 3D scene code", ToolCategory.GRAPHICS_3D, "{\"prompt\": \"string\"}")
                )
            )
        )
    )
    val plugins: StateFlow<List<PluginManifest>> = _plugins.asStateFlow()

    fun togglePluginEnabled(id: String) {
        _plugins.value = _plugins.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun installPlugin(plugin: PluginManifest) {
        if (_plugins.value.none { it.id == plugin.id }) {
            _plugins.value = _plugins.value + plugin.copy(isInstalled = true, isEnabled = true)
        }
    }

    fun getAllActiveTools(): List<AiToolDefinition> {
        return _plugins.value.filter { it.isInstalled && it.isEnabled }
            .flatMap { it.toolsProvided }
    }
}
