package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.data.model.GameEngineType
import com.example.virgoyt.data.model.GameProject
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun GameStudioView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val projects by viewModel.gameEngine.projects.collectAsState()
    var selectedTemplateFilter by remember { mutableStateOf("All") }

    val templates = listOf(
        Template3DItem(
            name = "Cyberpunk Neo-Metropolis",
            engine = "Unreal Engine 5.5",
            badge = "Raytracing PBR",
            color = Color(0xFF06B6D4),
            polyCount = "2.4M Nanite",
            fps = "120 FPS",
            description = "High-poly procedural night city with volumetric rain shaders, neon lighting & drone traffic."
        ),
        Template3DItem(
            name = "Holographic Cyber Core",
            engine = "Three.js WebGL",
            badge = "WebGL 2.0 Shader",
            color = Color(0xFFA855F7),
            polyCount = "450K Verts",
            fps = "60 FPS",
            description = "Interactive glowing wireframe core with particle orbital mesh, bloom & orbit drag camera."
        ),
        Template3DItem(
            name = "Tokyo Shinjuku LiDAR 3D",
            engine = "Google Earth GIS",
            badge = "1m Elevation Mesh",
            color = Color(0xFF10B981),
            polyCount = "8.2M LiDAR",
            fps = "90 FPS",
            description = "Real-world GIS geospatial 3D coordinate elevation model with photorealistic procedural satellite textures."
        ),
        Template3DItem(
            name = "Sci-Fi Space Odyssey Orbit",
            engine = "WebGPU Cinema",
            badge = "Zero-Copy Compute",
            color = Color(0xFFF59E0B),
            polyCount = "1.8M Triangles",
            fps = "144 FPS",
            description = "Planetary celestial bodies with procedural atmospheric ray scattering and asteroid belt physics."
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("game_studio_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🎮 3D & Unreal Studio Hub",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
                    )
                    Text(
                        text = "Real-time WebGL, Three.js & Unreal Engine 5.5 Templates",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF06B6D4).copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WebGPU Ready", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                    }
                }
            }
        }

        // Live 3D Interactive Canvas Simulation Card
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (themeMode.isDark) Color(0xFF050B14) else Color(0xFF0F172A),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🪐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "3D Viewport: Hologram Wireframe Matrix",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                                Text(
                                    text = "Shader: Bloom PBR • 60 FPS • Real-Time WebGL",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "RENDER ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated 3D Graphic Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF091E3A), Color(0xFF020617))
                                )
                            )
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⬡ ⬢ ⬡", fontSize = 28.sp, color = Color(0xFF06B6D4))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "[Three.js Holographic Mesh Vector Active]",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = "Drag to orbit camera • Double-tap to reset position",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModel.executePrompt("Launch interactive Three.js 3D viewport canvas in web tab")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Launch 3D Web Canvas", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.executePrompt("Synthesize Unreal Engine 5 C++ procedural landscape actor")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unreal 5.5 C++", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Section Title: 3D Studio Templates
        item {
            Text(
                text = "✨ Curated 3D Templates & Engines",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        // Template Cards
        items(templates) { tpl ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(tpl.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = tpl.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = tpl.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = tpl.badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tpl.color,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${tpl.engine} • ${tpl.polyCount} • ${tpl.fps}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tpl.color
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tpl.description,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.executePrompt("Load 3D template \"${tpl.name}\" and synthesize files")
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Load Template in Chat", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

data class Template3DItem(
    val name: String,
    val engine: String,
    val badge: String,
    val color: Color,
    val polyCount: String,
    val fps: String,
    val description: String
)

