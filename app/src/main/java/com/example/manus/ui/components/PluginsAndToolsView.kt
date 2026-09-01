package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.PluginManifest
import com.example.manus.data.model.ToolCategory
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.VirgoCyanGlow
import com.example.ui.theme.VirgoGlassCard
import com.example.ui.theme.VirgoNeonViolet

enum class PluginsSubTab {
    INSTALLED_PLUGINS,
    ACTIVE_TOOLS,
    EXECUTION_LOGS
}

@Composable
fun PluginsAndToolsView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val pluginManager = viewModel.pluginManager
    val plugins by pluginManager.installedPlugins.collectAsState()
    val tools by pluginManager.availableTools.collectAsState()
    val logs by pluginManager.recentToolCalls.collectAsState()

    var activeSubTab by remember { mutableStateOf(PluginsSubTab.INSTALLED_PLUGINS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VirgoGlassCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = "Plugins",
                    tint = VirgoCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Tool Calling & Plugin Ecosystem",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${plugins.size} Plugins • ${tools.count { it.isEnabled }} Active Tools",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Sub Tab Switcher Pills
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                PluginsSubTab.values().forEach { tab ->
                    val isSelected = activeSubTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VirgoCyanGlow.copy(alpha = 0.2f) else ManusSlate850)
                            .border(1.dp, if (isSelected) VirgoCyanGlow else SleekBorder, RoundedCornerShape(8.dp))
                            .clickable { activeSubTab = tab }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = when (tab) {
                                PluginsSubTab.INSTALLED_PLUGINS -> "Plugins (${plugins.size})"
                                PluginsSubTab.ACTIVE_TOOLS -> "Tools (${tools.size})"
                                PluginsSubTab.EXECUTION_LOGS -> "History"
                            },
                            color = if (isSelected) VirgoCyanGlow else ManusSlate400,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (activeSubTab) {
            PluginsSubTab.INSTALLED_PLUGINS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plugins) { plugin ->
                        PluginItemCard(plugin = plugin, onToggle = { pluginManager.togglePlugin(plugin.id) })
                    }
                }
            }
            PluginsSubTab.ACTIVE_TOOLS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tools) { tool ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = tool.category.iconEmoji, fontSize = 14.sp)
                                        Text(
                                            text = tool.name,
                                            color = ManusWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "(${tool.id})",
                                            color = ManusSlate500,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = tool.description,
                                        color = ManusSlate400,
                                        fontSize = 10.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Schema: ${tool.parametersSchema}",
                                        color = VirgoCyanGlow,
                                        fontSize = 9.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Switch(
                                    checked = tool.isEnabled,
                                    onCheckedChange = { pluginManager.toggleTool(tool.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = ManusWhite,
                                        checkedTrackColor = ManusEmerald,
                                        uncheckedTrackColor = ManusSlate800
                                    )
                                )
                            }
                        }
                    }
                }
            }
            PluginsSubTab.EXECUTION_LOGS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "⚙️ ${log.toolName}",
                                            color = VirgoCyanGlow,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "${log.executionTimeMs}ms",
                                            color = ManusSlate500,
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        text = "✓ SUCCESS",
                                        color = ManusEmerald,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Args: ${log.arguments}",
                                    color = ManusSlate300,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Output: ${log.output}",
                                    color = ManusSlate400,
                                    fontSize = 10.sp,
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

@Composable
private fun PluginItemCard(
    plugin: PluginManifest,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = ManusSlate900)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = plugin.iconEmoji, fontSize = 18.sp)
                    Column {
                        Text(
                            text = plugin.name,
                            color = ManusWhite,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "v${plugin.version} • by ${plugin.author}",
                            color = ManusSlate400,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (plugin.isInstalled) ManusEmerald.copy(alpha = 0.2f) else ManusSlate800)
                        .border(1.dp, if (plugin.isInstalled) ManusEmerald else SleekBorder, RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggle)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (plugin.isInstalled) "INSTALLED" else "INSTALL",
                        color = if (plugin.isInstalled) ManusEmerald else ManusSlate300,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = plugin.description,
                color = ManusSlate300,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Provided Tools Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                plugin.toolsProvided.forEach { toolName ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ManusSlate850)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🔧 $toolName",
                            color = VirgoCyanGlow,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
