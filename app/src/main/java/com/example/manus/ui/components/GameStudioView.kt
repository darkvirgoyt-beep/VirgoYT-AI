package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.data.model.GameEngineType
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusIndigoSoft
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate600
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameStudioView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val gameEngine = viewModel.gameStudioEngine
    val project by gameEngine.currentProject.collectAsState()
    val isWireframe by gameEngine.is3dWireframeMode.collectAsState()
    val cameraAngle by gameEngine.cameraRotationAngle.collectAsState()
    val zoomLevel by gameEngine.cameraZoomLevel.collectAsState()
    val selectedModelIdx by gameEngine.selectedModelIndex.collectAsState()
    val isBuildingShaders by gameEngine.isBuildingShaders.collectAsState()
    val shaderCount by gameEngine.shaderCompileCount.collectAsState()

    var studioTab by remember { mutableIntStateOf(0) } // 0 = 3D Viewport & GLB, 1 = Google Earth GIS, 2 = UE5 Engine & Blueprints

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ==========================================
        // Top Toolbar: Game Title & Graphic Engine Badge
        // ==========================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Game Studio",
                        tint = ManusCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = project.title,
                        color = ManusWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "High-End AAA Engine • Nanite Geometry & Lumen Ray Tracing • 120 FPS",
                    color = ManusSlate400,
                    fontSize = 11.5.sp
                )
            }

            Button(
                onClick = {
                    viewModel.showToast("🚀 Building & Launching Unreal Engine 5 Sandbox Game...")
                    viewModel.executeTerminalCommand("wine ./Binaries/Win64/VirgoYTGame-Win64-Shipping.exe -dx12 -high")
                    viewModel.selectTab(ActiveWorkspaceTab.TERMINAL)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("launch_game_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Run Game",
                        tint = ManusWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Run Game (EXE)",
                        color = ManusWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Sub-tabs: [ 🧊 3D Viewport & GLB | 🌍 Google Earth GIS | 🎮 Unreal Blueprint & C++ ]
        TabRow(
            selectedTabIndex = studioTab,
            containerColor = ManusSlate900,
            contentColor = ManusCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[studioTab]),
                    color = ManusCyan
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = studioTab == 0,
                onClick = { studioTab = 0 },
                text = { Text("🧊 3D Viewport & GLB", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = studioTab == 1,
                onClick = { studioTab = 1 },
                text = { Text("🌍 Google Earth GIS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = studioTab == 2,
                onClick = { studioTab = 2 },
                text = { Text("🎮 UE5 C++ & Settings", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        // ==========================================
        // Tab Content
        // ==========================================
        when (studioTab) {
            0 -> {
                // ==========================================
                // 3D VIEWPORT & GLB MODEL MAKER
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 3D Viewport Canvas with drag-to-rotate
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    gameEngine.rotateCamera(dragAmount.x * 0.5f)
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117))
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Procedural 3D Mesh Rendering Canvas
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val rad = Math.toRadians(cameraAngle.toDouble())
                                val cosA = cos(rad).toFloat()
                                val sinA = sin(rad).toFloat()
                                val scale = (size.minDimension * 0.35f) * zoomLevel

                                // Draw 3D Grid Floor
                                for (i in -4..4) {
                                    val startX = centerX + (i * 35f * cosA)
                                    val startY = centerY + 70f + (i * 12f * sinA)
                                    val endX = centerX + (i * 35f * cosA) - (120f * sinA)
                                    val endY = centerY + 140f + (120f * cosA)
                                    drawLine(
                                        color = Color(0xFF1E293B),
                                        start = Offset(startX, startY),
                                        end = Offset(endX, endY),
                                        strokeWidth = 1.2f
                                    )
                                }

                                // 3D Procedural Polyhedral Mesh based on selectedModelIdx
                                when (selectedModelIdx % 4) {
                                    0 -> {
                                        // Tyrannosaurus_Titan / Apex Creature Wireframe & Solid Mesh
                                        draw3dCreatureMesh(centerX, centerY, cosA, sinA, scale, isWireframe)
                                    }
                                    1 -> {
                                        // Tactical Military Compound
                                        draw3dBuildingMesh(centerX, centerY, cosA, sinA, scale, isWireframe)
                                    }
                                    2 -> {
                                        // Pal Companion / Creature Mesh
                                        draw3dCompanionMesh(centerX, centerY, cosA, sinA, scale, isWireframe)
                                    }
                                    3 -> {
                                        // Ancient Fantasy Ruins & Landscape Monolith
                                        draw3dLandscapeRuinsMesh(centerX, centerY, cosA, sinA, scale, isWireframe)
                                    }
                                }
                            }

                            // Viewport Controls Overlay (Top Right)
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isWireframe) ManusIndigo else ManusSlate850.copy(alpha = 0.85f))
                                        .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                                        .clickable { gameEngine.toggleWireframe() }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isWireframe) "Wireframe ON" else "PBR Solid",
                                        color = ManusWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ManusSlate850.copy(alpha = 0.85f))
                                        .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                                        .clickable { gameEngine.rotateCamera(45f) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "↺ 45°",
                                        color = ManusWhite,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Active Model Specs Overlay (Bottom Left)
                            val currentModel = project.models.getOrNull(selectedModelIdx)
                            if (currentModel != null) {
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ManusSlate950.copy(alpha = 0.85f))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = currentModel.name,
                                        color = ManusCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Vertices: ${currentModel.vertexCount} • Polygons: ${currentModel.polygonCount} • Format: ${currentModel.format}",
                                        color = ManusSlate300,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    // 3D Model Asset Picker
                    Text(
                        text = "Active 3D Models in Scene (GLB / Nanite Mesh):",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(project.models) { idx, model ->
                            val isSelected = idx == selectedModelIdx
                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        if (isSelected) ManusCyan else SleekBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { gameEngine.selectModel(idx) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ManusSlate850 else ManusSlate900
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = model.category,
                                            color = ManusIndigoLight,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = ManusCyan,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = model.name,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = model.description,
                                        color = ManusSlate400,
                                        fontSize = 10.5.sp,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }

                    // Quick 3D Generator Button
                    Button(
                        onClick = {
                            gameEngine.addGenerated3DModel(
                                modelName = "Cyber_Mech_Paladin_${(10..99).random()}",
                                category = "Weapon & Vehicle",
                                format = "GLB",
                                description = "Heavy sci-fi combat exo-suit with dual particle cannons and dynamic thruster trails."
                            )
                            viewModel.showToast("✨ Generated & Imported 3D GLB Model into UE5 Scene")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManusSlate850),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Add 3D Model",
                                tint = ManusCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Generate New 3D GLB Model Mesh",
                                color = ManusWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            1 -> {
                // ==========================================
                // GOOGLE EARTH 3D GIS & TERRAIN MAP MAKER
                // ==========================================
                var locName by remember { mutableStateOf("Grand Canyon & Pochinki Highlands") }
                var latText by remember { mutableStateOf("36.1069") }
                var lonText by remember { mutableStateOf("-112.1129") }
                var biomeType by remember { mutableStateOf("Volcanic Island & Jungle Ruins") }
                var gameInsp by remember { mutableStateOf("Ark Survival & BGMI Open World") }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "GIS",
                                    tint = ManusEmerald,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Google Earth 3D Photogrammetry & DEM Importer",
                                    color = ManusWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = "Imports real-world LiDAR elevation maps and satellite textures directly into Unreal Engine 5 Nanite Landscapes.",
                                color = ManusSlate300,
                                fontSize = 11.5.sp
                            )

                            OutlinedTextField(
                                value = locName,
                                onValueChange = { locName = it },
                                label = { Text("Location Name / Map Title") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusCyan,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = latText,
                                    onValueChange = { latText = it },
                                    label = { Text("Latitude") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ManusCyan,
                                        unfocusedBorderColor = SleekBorder,
                                        focusedTextColor = ManusWhite,
                                        unfocusedTextColor = ManusWhite
                                    ),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = lonText,
                                    onValueChange = { lonText = it },
                                    label = { Text("Longitude") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ManusCyan,
                                        unfocusedBorderColor = SleekBorder,
                                        focusedTextColor = ManusWhite,
                                        unfocusedTextColor = ManusWhite
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = biomeType,
                                onValueChange = { biomeType = it },
                                label = { Text("Biome Style (e.g. Snow, Desert, Jungle)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusCyan,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Button(
                                onClick = {
                                    val lat = latText.toDoubleOrNull() ?: 36.1069
                                    val lon = lonText.toDoubleOrNull() ?: -112.1129
                                    gameEngine.generateNewTerrainFromGoogleEarth(locName, lat, lon, biomeType, gameInsp)
                                    viewModel.showToast("🌍 Generating 3D Heightmap from Google Earth GIS...")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ManusEmerald),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isBuildingShaders) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = ManusWhite,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = "Compiling Nanite Shaders ($shaderCount remaining)...",
                                            color = ManusWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Generate 3D Map in Unreal Engine",
                                        color = ManusWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Active GIS Map Info Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, SleekBorder, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = ManusSlate850)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Active Google Earth Map: ${project.mapLocation.locationName}",
                                color = ManusCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Coordinates: ${project.mapLocation.latitude}° N, ${project.mapLocation.longitude}° W • Peak: ${project.mapLocation.elevationMeters}m",
                                color = ManusSlate300,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Inspired by: ${project.mapLocation.gameInspiration}",
                                color = ManusSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            2 -> {
                // ==========================================
                // UNREAL ENGINE 5 C++ & CONFIGURATION
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Source/VirgoYTGame/VirgoYTCharacter.cpp",
                                    color = ManusWhite,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ManusIndigoSoft)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("UE5 C++", color = ManusCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "// Enhanced Input & Companion Creature Spawning\n" +
                                        "void AVirgoYTCharacter::SpawnCompanionCreature(TSubclassOf<AActor> CreatureClass)\n" +
                                        "{\n" +
                                        "    if (CreatureClass)\n" +
                                        "    {\n" +
                                        "        FVector SpawnLocation = GetActorLocation() + GetActorForwardVector() * 200.0f;\n" +
                                        "        GetWorld()->SpawnActor<AActor>(CreatureClass, SpawnLocation, FRotator::ZeroRotator);\n" +
                                        "    }\n" +
                                        "}",
                                color = ManusSlate300,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Engine Presets
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Engine Preset & Graphics Features:", color = ManusWhite, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            EngineFeatureRow("Lumen Global Illumination", "Active (Hardware Ray Tracing)")
                            EngineFeatureRow("Nanite Virtualized Geometry", "14.8M Triangles Streamed")
                            EngineFeatureRow("Target Rendering FPS", "120 FPS Locked")
                            EngineFeatureRow("Virtual Shadow Maps (VSM)", "Enabled (Ultra Soft)")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EngineFeatureRow(name: String, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, color = ManusSlate300, fontSize = 11.5.sp)
        Text(status, color = ManusCyan, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
    }
}

// 3D Wireframe / Shading Helper Functions for Canvas
private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3dCreatureMesh(
    cx: Float, cy: Float, cosA: Float, sinA: Float, scale: Float, wireframe: Boolean
) {
    val bodyPath = Path().apply {
        moveTo(cx - 60f * cosA, cy + 20f * sinA)
        lineTo(cx - 20f * cosA + 10f * sinA, cy - 50f * scale / 100f)
        lineTo(cx + 40f * cosA, cy - 70f * scale / 100f)
        lineTo(cx + 80f * cosA - 30f * sinA, cy - 20f * scale / 100f)
        lineTo(cx + 40f * cosA + 30f * sinA, cy + 50f * scale / 100f)
        lineTo(cx - 30f * cosA, cy + 40f * scale / 100f)
        close()
    }

    if (wireframe) {
        drawPath(bodyPath, color = ManusCyan, style = Stroke(width = 2f))
        drawLine(color = ManusGreen, start = Offset(cx - 20f * cosA, cy - 50f), end = Offset(cx + 40f * cosA, cy + 50f), strokeWidth = 1.5f)
        drawLine(color = ManusGreen, start = Offset(cx - 60f * cosA, cy + 20f), end = Offset(cx + 80f * cosA, cy - 20f), strokeWidth = 1.5f)
    } else {
        drawPath(bodyPath, brush = Brush.verticalGradient(listOf(ManusCyan, ManusIndigo)))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3dBuildingMesh(
    cx: Float, cy: Float, cosA: Float, sinA: Float, scale: Float, wireframe: Boolean
) {
    val bPath = Path().apply {
        moveTo(cx - 50f * cosA, cy - 30f)
        lineTo(cx + 50f * cosA, cy - 50f)
        lineTo(cx + 50f * cosA + 40f * sinA, cy + 30f)
        lineTo(cx - 50f * cosA + 40f * sinA, cy + 50f)
        close()
    }
    if (wireframe) {
        drawPath(bPath, color = ManusGreen, style = Stroke(width = 2f))
    } else {
        drawPath(bPath, brush = Brush.linearGradient(listOf(ManusSlate500, ManusSlate800)))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3dCompanionMesh(
    cx: Float, cy: Float, cosA: Float, sinA: Float, scale: Float, wireframe: Boolean
) {
    val cPath = Path().apply {
        moveTo(cx, cy - 60f)
        lineTo(cx + 35f * cosA, cy - 10f)
        lineTo(cx + 20f * cosA, cy + 40f)
        lineTo(cx - 20f * cosA, cy + 40f)
        lineTo(cx - 35f * cosA, cy - 10f)
        close()
    }
    if (wireframe) {
        drawPath(cPath, color = ManusPurple, style = Stroke(width = 2f))
    } else {
        drawPath(cPath, brush = Brush.radialGradient(listOf(ManusPurple, ManusIndigo)))
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.draw3dLandscapeRuinsMesh(
    cx: Float, cy: Float, cosA: Float, sinA: Float, scale: Float, wireframe: Boolean
) {
    val rPath = Path().apply {
        moveTo(cx - 40f * cosA, cy + 60f)
        lineTo(cx - 20f * cosA, cy - 70f)
        lineTo(cx + 10f * cosA, cy - 75f)
        lineTo(cx + 30f * cosA, cy + 60f)
        close()
    }
    if (wireframe) {
        drawPath(rPath, color = ManusEmerald, style = Stroke(width = 2f))
    } else {
        drawPath(rPath, brush = Brush.verticalGradient(listOf(ManusEmerald, ManusSlate700)))
    }
}
