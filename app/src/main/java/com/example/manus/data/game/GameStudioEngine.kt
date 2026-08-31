package com.example.manus.data.game

import com.example.manus.data.model.Game3DModel
import com.example.manus.data.model.GameEngineType
import com.example.manus.data.model.GameProject
import com.example.manus.data.model.GoogleEarthGISMap
import com.example.manus.data.vfs.VirtualFileSystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameStudioEngine(
    private val vfs: VirtualFileSystem
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _currentProject = MutableStateFlow(createDefaultProject())
    val currentProject: StateFlow<GameProject> = _currentProject.asStateFlow()

    private val _is3dWireframeMode = MutableStateFlow(false)
    val is3dWireframeMode: StateFlow<Boolean> = _is3dWireframeMode.asStateFlow()

    private val _cameraRotationAngle = MutableStateFlow(45f)
    val cameraRotationAngle: StateFlow<Float> = _cameraRotationAngle.asStateFlow()

    private val _cameraZoomLevel = MutableStateFlow(1.0f)
    val cameraZoomLevel: StateFlow<Float> = _cameraZoomLevel.asStateFlow()

    private val _isBuildingShaders = MutableStateFlow(false)
    val isBuildingShaders: StateFlow<Boolean> = _isBuildingShaders.asStateFlow()

    private val _shaderCompileCount = MutableStateFlow(0)
    val shaderCompileCount: StateFlow<Int> = _shaderCompileCount.asStateFlow()

    private val _selectedModelIndex = MutableStateFlow(0)
    val selectedModelIndex: StateFlow<Int> = _selectedModelIndex.asStateFlow()

    init {
        setupInitialWorkspaceFiles()
    }

    private fun createDefaultProject(): GameProject {
        val models = listOf(
            Game3DModel(
                name = "Tyrannosaurus_Titan.glb",
                format = "GLB / Nanite Mesh",
                category = "Apex Creature / Dinosaur",
                vertexCount = 48200,
                polygonCount = 96400,
                materials = listOf("M_DinoScales_Nanite_PBR", "M_EyeGlow_Emissive", "M_Claw_Roughness"),
                description = "High-poly Ark-style creature with procedural muscle deformation and 4K PBR displacement maps."
            ),
            Game3DModel(
                name = "Pochinki_Tactical_Compound.glb",
                format = "GLB / StaticMesh",
                category = "Architecture & Cover",
                vertexCount = 32100,
                polygonCount = 64200,
                materials = listOf("M_Concrete_Lumen", "M_WeatheredWood", "M_Glass_RayTraced"),
                description = "Modular multi-floor tactical building with destructible doors and high-resolution lightmaps."
            ),
            Game3DModel(
                name = "Pal_Elemental_Companion.glb",
                format = "GLB / SkeletalMesh",
                category = "Companion / Mount",
                vertexCount = 22400,
                polygonCount = 44800,
                materials = listOf("M_Toon_CelShader", "M_Aura_ParticleFX", "M_Saddle_Leather"),
                description = "Stylized elemental companion creature equipped for riding, flight, and automated base gathering."
            ),
            Game3DModel(
                name = "Dawnlands_Ancient_Ruins.glb",
                format = "GLB / Landscape Foliage",
                category = "World & Biome",
                vertexCount = 19800,
                polygonCount = 39600,
                materials = listOf("M_MossyStone_Nanite", "M_Foliage_WindShader", "M_AncientRune_Glow"),
                description = "Open-world fantasy monolith with procedural wind vertex animation and glowing runic decals."
            )
        )

        return GameProject(
            title = "VirgoYT: Nexus Horizon (UE5 Open World)",
            engine = GameEngineType.UNREAL_ENGINE_5,
            genre = "AAA Open World Survival & Battle Royale",
            graphicPreset = "Cinematic 4K (Lumen GI, Nanite Virtual Geometry, Subsurface Scattering)",
            targetFps = 120,
            mapLocation = GoogleEarthGISMap(
                locationName = "Mount Rainier & Crater Lake Volcanic Biome",
                latitude = 46.8523,
                longitude = -121.7603,
                elevationMeters = 4392.0,
                terrainResolution = "0.25m Ultra Photogrammetry DEM",
                biomeType = "Volcanic Snow Peaks & Subtropical Forest",
                gameInspiration = "BGMI / Ark Survival / Palworld / Dawnlands"
            ),
            models = models,
            isRendering = true,
            renderProgressPercent = 100
        )
    }

    private fun setupInitialWorkspaceFiles() {
        // Generate Unreal Engine 5 C++ & Blueprint structure in VirtualFileSystem
        vfs.addFile(
            "/workspace/UnrealEngine5/Source/VirgoYTGame/VirgoYTCharacter.cpp",
            """
            // Copyright (c) 2026 VirgoYT AI Game Studio. All Rights Reserved.
            #include "VirgoYTCharacter.h"
            #include "GameFramework/SpringArmComponent.h"
            #include "Camera/CameraComponent.h"
            #include "Components/CapsuleComponent.h"
            #include "EnhancedInputComponent.h"
            #include "EnhancedInputSubsystems.h"
            #include "Kismet/GameplayStatics.h"

            AVirgoYTCharacter::AVirgoYTCharacter()
            {
                GetCapsuleComponent()->InitCapsuleSize(42.f, 96.0f);
                bUseControllerRotationPitch = false;
                bUseControllerRotationYaw = false;
                bUseControllerRotationRoll = false;

                CameraBoom = CreateDefaultSubobject<USpringArmComponent>(TEXT("CameraBoom"));
                CameraBoom->SetupAttachment(RootComponent);
                CameraBoom->TargetArmLength = 400.0f;
                CameraBoom->bUsePawnControlRotation = true;

                FollowCamera = CreateDefaultSubobject<UCameraComponent>(TEXT("FollowCamera"));
                FollowCamera->SetupAttachment(CameraBoom, USpringArmComponent::SocketName);
                FollowCamera->bUsePawnControlRotation = false;

                // Nanite & Lumen physics initialization
                Health = 100.0f;
                MaxHealth = 100.0f;
                Stamina = 150.0f;
                bIsGliding = false;
            }

            void AVirgoYTCharacter::SpawnCompanionCreature(TSubclassOf<AActor> CreatureClass)
            {
                if (CreatureClass)
                {
                    FVector SpawnLocation = GetActorLocation() + GetActorForwardVector() * 200.0f;
                    GetWorld()->SpawnActor<AActor>(CreatureClass, SpawnLocation, FRotator::ZeroRotator);
                    UE_LOG(LogTemp, Warning, TEXT("[VirgoYT Game] Companion Creature successfully spawned!"));
                }
            }
            """.trimIndent()
        )

        vfs.addFile(
            "/workspace/UnrealEngine5/Config/DefaultEngine.ini",
            """
            [/Script/Engine.RendererSettings]
            r.DynamicGlobalIlluminationMethod=1 ; Lumen Global Illumination
            r.ReflectionMethod=1 ; Lumen Reflections
            r.Nanite.ProjectEnabled=1 ; Nanite Virtualized Geometry Enabled
            r.Shadow.Virtual.Enable=1 ; Virtual Shadow Maps
            r.DefaultFeature.AntiAliasing=2 ; TSR (Temporal Super Resolution)
            r.RayTracing=1 ; DirectX 12 DXR Ray Traced Ambient Occlusion & Shadows
            r.ShaderCompiler.NumWorkers=16
            """.trimIndent()
        )

        vfs.addFile(
            "/workspace/GoogleEarth_GIS/terrain_dem_rainier.json",
            """
            {
              "satelliteSource": "Google Earth 3D Photogrammetry Mesh",
              "coordinates": { "lat": 46.8523, "lon": -121.7603 },
              "elevationMinMeters": 1420.0,
              "elevationMaxMeters": 4392.0,
              "heightmapResolution": "4096x4096 16-bit PNG",
              "foliageScatterDensity": "3200 trees/km²",
              "biomePresets": ["Alpine Snow", "Evergreen Pine Forest", "Basalt Rock Cliffs", "Geothermal Springs"]
            }
            """.trimIndent()
        )
    }

    fun rotateCamera(deltaDegrees: Float) {
        _cameraRotationAngle.value = (_cameraRotationAngle.value + deltaDegrees) % 360f
    }

    fun setZoomLevel(zoom: Float) {
        _cameraZoomLevel.value = zoom.coerceIn(0.4f, 3.0f)
    }

    fun toggleWireframe() {
        _is3dWireframeMode.value = !_is3dWireframeMode.value
    }

    fun selectModel(index: Int) {
        if (index in 0 until (_currentProject.value.models.size)) {
            _selectedModelIndex.value = index
        }
    }

    fun switchGameEngine(engine: GameEngineType) {
        _currentProject.value = _currentProject.value.copy(engine = engine)
    }

    fun generateNewTerrainFromGoogleEarth(
        locationName: String,
        lat: Double,
        lon: Double,
        biome: String,
        inspiration: String
    ) {
        scope.launch {
            _isBuildingShaders.value = true
            _shaderCompileCount.value = 1420
            
            for (i in 1..5) {
                delay(200)
                _shaderCompileCount.value = (1420 - i * 280).coerceAtLeast(0)
            }

            val newMap = GoogleEarthGISMap(
                locationName = locationName,
                latitude = lat,
                longitude = lon,
                elevationMeters = 3800.0,
                terrainResolution = "0.15m Ultra LiDAR DEM",
                biomeType = biome,
                gameInspiration = inspiration
            )

            _currentProject.value = _currentProject.value.copy(
                mapLocation = newMap,
                isRendering = true,
                renderProgressPercent = 100
            )

            // Save new GIS terrain file to VFS
            val sanitizedName = locationName.replace(" ", "_").lowercase()
            vfs.addFile(
                "/workspace/GoogleEarth_GIS/terrain_$sanitizedName.json",
                """
                {
                  "location": "$locationName",
                  "lat": $lat,
                  "lon": $lon,
                  "biome": "$biome",
                  "inspiration": "$inspiration",
                  "naniteTriangles": "14,800,000",
                  "lumenLighting": "Dynamic Atmospheric Sky & Volumetric Fog"
                }
                """.trimIndent()
            )

            _isBuildingShaders.value = false
        }
    }

    fun addGenerated3DModel(
        modelName: String,
        category: String,
        format: String = "GLB",
        description: String
    ) {
        val newModel = Game3DModel(
            name = if (modelName.endsWith(".glb")) modelName else "$modelName.glb",
            format = "$format / Nanite Mesh",
            category = category,
            vertexCount = (15000..65000).random(),
            polygonCount = (30000..130000).random(),
            materials = listOf("M_${modelName}_Albedo", "M_${modelName}_Normal", "M_${modelName}_PBR"),
            description = description
        )

        val updatedModels = _currentProject.value.models + newModel
        _currentProject.value = _currentProject.value.copy(models = updatedModels)
        _selectedModelIndex.value = updatedModels.lastIndex

        vfs.addFile(
            "/workspace/GameAssets/3DModels/${newModel.name}",
            "/* GLB Binary Header: Magic=0x46546C67, Version=2, Length=2048576 */\n" +
            "/* Nanite High Fidelity Mesh: Vertices=${newModel.vertexCount}, Triangles=${newModel.polygonCount} */"
        )
    }
}
