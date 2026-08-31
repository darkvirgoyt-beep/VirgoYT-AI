package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.appgen.AppCategory
import com.example.manus.data.appgen.AppTemplate
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder

@Composable
fun AppCreationHubView(viewModel: ManusCloudViewModel) {
    val appGenEngine = viewModel.applicationGenerationEngine
    val templates = appGenEngine.availableTemplates
    val runningApps by appGenEngine.generatedApps.collectAsState()
    val isGenerating by appGenEngine.isGenerating.collectAsState()

    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    var customAppName by remember { mutableStateOf("") }
    var selectedTemplate by remember { mutableStateOf<AppTemplate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusIndigoBg)
                        .border(1.dp, SleekBorder, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = "App Generation Hub",
                        tint = ManusCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Autonomous App Creation Ecosystem",
                        color = ManusWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Web • Mobile • Desktop • 3D Games (Instant Scaffold & Hot-Reload)",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ManusEmerald.copy(alpha = 0.2f))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${runningApps.size} APPS LIVE",
                    color = ManusEmerald,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Category Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                val isAllSelected = selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAllSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isAllSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = null }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🌟 All Categories (${templates.size})",
                        color = if (isAllSelected) ManusCyan else ManusSlate300,
                        fontSize = 11.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            items(AppCategory.values().toList()) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${cat.icon} ${cat.title.substringBefore(" ")}",
                        color = if (isSelected) ManusCyan else ManusSlate300,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Templates & Generation List
        val filteredTemplates = if (selectedCategory == null) templates else templates.filter { it.category == selectedCategory }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Running Apps Live Section
            if (runningApps.isNotEmpty()) {
                item {
                    Text(
                        text = "🚀 Active Cloud Sandbox Deployments:",
                        color = ManusWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(runningApps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManusSlate900)
                            .border(1.dp, ManusEmerald.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = app.template.iconEmoji, fontSize = 16.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = app.name,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(ManusEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(text = ":${app.port}", color = ManusEmerald, fontSize = 8.5.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                                Text(
                                    text = "${app.template.framework} • Build ${app.buildDurationMs}ms",
                                    color = ManusSlate400,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.selectTab(com.example.manus.data.model.ActiveWorkspaceTab.BROWSER)
                                viewModel.showToast("🌐 Opened live preview for ${app.name}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigoBg),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Open Sandbox", color = ManusCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Text(
                    text = "📦 One-Click AI Application Blueprints:",
                    color = ManusWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(filteredTemplates) { template ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = template.iconEmoji, fontSize = 18.sp)
                                Column {
                                    Text(
                                        text = template.title,
                                        color = ManusWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${template.framework} • ${template.language}",
                                        color = ManusIndigoLight,
                                        fontSize = 10.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val instance = appGenEngine.generateAppFromTemplate(template, customAppName)
                                    viewModel.showToast("🚀 Scaffolded & deployed '${instance.name}' in ${instance.buildDurationMs}ms")
                                    customAppName = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("scaffold_btn_${template.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "Scaffold", tint = ManusWhite, modifier = Modifier.size(13.dp))
                                    Text("Scaffold AI App", color = ManusWhite, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Text(
                            text = template.description,
                            color = ManusSlate400,
                            fontSize = 11.sp
                        )

                        // Feature pills
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(template.features) { feat ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ManusSlate950)
                                        .border(1.dp, SleekBorder, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "✓ $feat",
                                        color = ManusSlate300,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
