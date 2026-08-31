package com.example.manus.data.appgen

import com.example.manus.data.vfs.VirtualFileSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class AppCategory(val title: String, val icon: String) {
    WEB("Web & SaaS Applications", "🌐"),
    MOBILE("Native & Cross Mobile", "📱"),
    DESKTOP("Desktop Native & Cross", "🖥️"),
    GAME("3D & 2D Game Engines", "🎮")
}

data class AppTemplate(
    val id: String,
    val title: String,
    val category: AppCategory,
    val framework: String,
    val language: String,
    val description: String,
    val iconEmoji: String,
    val features: List<String>,
    val defaultFiles: List<String>
)

data class GeneratedAppInstance(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val template: AppTemplate,
    val port: Int = (3000..9000).random(),
    val status: String = "RUNNING", // BUILDING, RUNNING, STOPPED
    val previewUrl: String = "https://app-sandbox-node-${(100..999).random()}.virgoyt.cloud",
    val buildDurationMs: Long = 420L,
    val generatedFilesCount: Int = 24
)

class ApplicationGenerationEngine(
    private val vfs: VirtualFileSystem
) {
    val availableTemplates = listOf(
        // Web
        AppTemplate(
            id = "saas_nextjs_ai",
            title = "Next.js 15 AI SaaS Platform",
            category = AppCategory.WEB,
            framework = "Next.js 15 + Tailwind + Shadcn",
            language = "TypeScript",
            description = "Complete subscription SaaS with Stripe auth, vector search, dashboard charts and dark mode.",
            iconEmoji = "⚡",
            features = listOf("App Router SSR", "Server Actions", "PostgreSQL Prisma", "Stripe Billing"),
            defaultFiles = listOf("app/layout.tsx", "app/page.tsx", "app/dashboard/page.tsx", "prisma/schema.prisma")
        ),
        AppTemplate(
            id = "ecommerce_vue_nuxt",
            title = "Vue 3 / Nuxt E-Commerce Store",
            category = AppCategory.WEB,
            framework = "Nuxt 3 + Pinia",
            language = "TypeScript",
            description = "High-performance storefront with live inventory, shopping cart, and multi-currency checkout.",
            iconEmoji = "🛍️",
            features = listOf("SSR Hydration", "State Store Pinia", "Instant Checkout", "SEO Meta Engine"),
            defaultFiles = listOf("pages/index.vue", "pages/products/[id].vue", "stores/cart.ts")
        ),
        AppTemplate(
            id = "analytics_dashboard_react",
            title = "Real-Time Enterprise Analytics Dashboard",
            category = AppCategory.WEB,
            framework = "React 19 + Vite + Vico",
            language = "TypeScript",
            description = "Telemetry monitoring dashboard with live WebSocket graphs, heatmaps, and audit logs.",
            iconEmoji = "📊",
            features = listOf("WebSocket Streams", "Canvas Heatmaps", "Role-Based Access", "Export PDF/CSV"),
            defaultFiles = listOf("src/App.tsx", "src/components/MetricsGrid.tsx", "src/hooks/useTelemetry.ts")
        ),

        // Mobile
        AppTemplate(
            id = "android_compose_native",
            title = "Android Jetpack Compose Native App",
            category = AppCategory.MOBILE,
            framework = "Jetpack Compose + Material 3 + Room",
            language = "Kotlin",
            description = "Edge-to-edge Android application with Room local database, ViewModel state flow, and M3 themes.",
            iconEmoji = "🤖",
            features = listOf("M3 Dynamic Theme", "Room SQLite", "Coroutines Flow", "Robolectric Tested"),
            defaultFiles = listOf("MainActivity.kt", "ui/theme/Theme.kt", "data/local/AppDatabase.kt")
        ),
        AppTemplate(
            id = "flutter_cross_platform",
            title = "Flutter 3 Multi-Platform Mobile App",
            category = AppCategory.MOBILE,
            framework = "Flutter 3 + Riverpod",
            language = "Dart",
            description = "Pixel-perfect iOS & Android application with offline caching, smooth 120Hz animations.",
            iconEmoji = "💙",
            features = listOf("Riverpod State", "Custom Shaders", "Biometric Auth", "Offline Sync"),
            defaultFiles = listOf("lib/main.dart", "lib/screens/home_screen.dart", "lib/providers/app_state.dart")
        ),
        AppTemplate(
            id = "react_native_expo",
            title = "React Native Expo Mobile Hub",
            category = AppCategory.MOBILE,
            framework = "Expo SDK 51 + NativeWind",
            language = "TypeScript",
            description = "Cross-platform mobile application with push notifications, camera scanner, and gestures.",
            iconEmoji = "📱",
            features = listOf("Expo Router", "NativeWind CSS", "Camera & Sensors", "OTA Updates"),
            defaultFiles = listOf("app/(tabs)/index.tsx", "app/_layout.tsx", "components/ThemedText.tsx")
        ),

        // Desktop
        AppTemplate(
            id = "tauri_rust_desktop",
            title = "Tauri 2.0 Lightweight Desktop App",
            category = AppCategory.DESKTOP,
            framework = "Tauri 2.0 + Rust + React",
            language = "Rust & TypeScript",
            description = "Ultra-fast <10MB native desktop application for Windows, macOS, and Linux with native file access.",
            iconEmoji = "🦀",
            features = listOf("Rust Backend Core", "Native Windowing", "Sub-15ms Startup", "Secure IPC"),
            defaultFiles = listOf("src-tauri/src/main.rs", "src/App.tsx", "src-tauri/Cargo.toml")
        ),
        AppTemplate(
            id = "electron_cross_desktop",
            title = "Electron Enterprise Desktop Tool",
            category = AppCategory.DESKTOP,
            framework = "Electron + Node.js + React",
            language = "TypeScript",
            description = "Feature-packed desktop IDE and developer tool with multi-window support and system tray.",
            iconEmoji = "🖥️",
            features = listOf("Main/Renderer IPC", "System Tray Integration", "Auto-Updater", "Native Menus"),
            defaultFiles = listOf("main.ts", "preload.ts", "renderer/App.tsx")
        ),

        // Games
        AppTemplate(
            id = "unreal_engine_5_openworld",
            title = "Unreal Engine 5.4 AAA Open-World Game",
            category = AppCategory.GAME,
            framework = "Unreal Engine 5.4 (Nanite & Lumen)",
            language = "C++ & Blueprints",
            description = "High-fidelity open-world survival game with dynamic weather, PBR shaders, and Google Earth GIS map.",
            iconEmoji = "🎮",
            features = listOf("Nanite Virtual Geometry", "Lumen Real-Time GI", "LiDAR 0.5m DEM Terrain", "Dedicated Server"),
            defaultFiles = listOf("Source/VirgoYTGame/Character.cpp", "Source/VirgoYTGame/Character.h", "Content/Maps/OpenWorld.umap")
        ),
        AppTemplate(
            id = "unity_6_cross_game",
            title = "Unity 6 Cross-Platform 3D Action RPG",
            category = AppCategory.GAME,
            framework = "Unity 6 + Universal Render Pipeline",
            language = "C#",
            description = "Action combat RPG with procedural dungeons, ragdoll physics, and cross-play multiplayer.",
            iconEmoji = "🎲",
            features = listOf("URP 3D Graphics", "DOTS ECS Simulation", "Netcode for GameObjects", "Cinemachine Cam"),
            defaultFiles = listOf("Assets/Scripts/PlayerController.cs", "Assets/Scripts/GameManager.cs", "Assets/Scenes/Main.unity")
        ),
        AppTemplate(
            id = "godot_4_lightweight_game",
            title = "Godot 4.3 2D/3D Fast Indie Game",
            category = AppCategory.GAME,
            framework = "Godot 4.3 Engine",
            language = "GDScript / C#",
            description = "Fast-loading lightweight indie game with tilemaps, particle systems, and web HTML5 export.",
            iconEmoji = "🤖",
            features = listOf("Forward+ Renderer", "TileMap Layers", "Audio Polyphony", "WASM Export"),
            defaultFiles = listOf("scripts/player.gd", "scenes/world.tscn", "project.godot")
        )
    )

    private val _generatedApps = MutableStateFlow<List<GeneratedAppInstance>>(
        listOf(
            GeneratedAppInstance(
                name = "Valkyrie Open World Survival",
                template = availableTemplates.first { it.id == "unreal_engine_5_openworld" },
                port = 7777,
                status = "RUNNING",
                previewUrl = "https://preview-game-ue5.virgoyt.cloud"
            ),
            GeneratedAppInstance(
                name = "Omni SaaS AI Cloud Hub",
                template = availableTemplates.first { it.id == "saas_nextjs_ai" },
                port = 3000,
                status = "RUNNING",
                previewUrl = "https://preview-saas-next.virgoyt.cloud"
            )
        )
    )
    val generatedApps: StateFlow<List<GeneratedAppInstance>> = _generatedApps.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    fun generateAppFromTemplate(template: AppTemplate, customName: String): GeneratedAppInstance {
        _isGenerating.value = true
        val appName = customName.ifBlank { template.title }
        
        // Populate VFS with initial template files
        template.defaultFiles.forEach { path ->
            vfs.addFile(
                path = "/workspace/$appName/$path",
                content = "// Auto-generated by VirgoYT AI App Engine\n// Framework: ${template.framework}\n// Language: ${template.language}\n\nexport const metadata = {\n  title: '$appName',\n  framework: '${template.framework}',\n  status: 'OPTIMAL'\n};\n",
                owner = "developer"
            )
        }

        val instance = GeneratedAppInstance(
            name = appName,
            template = template,
            status = "RUNNING",
            buildDurationMs = (280..520).random().toLong()
        )

        _generatedApps.value = listOf(instance) + _generatedApps.value
        _isGenerating.value = false
        return instance
    }
}
