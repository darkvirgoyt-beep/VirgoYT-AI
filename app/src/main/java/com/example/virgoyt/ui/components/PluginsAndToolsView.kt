package com.example.virgoyt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun PluginsAndToolsView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val plugins by viewModel.pluginManager.plugins.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("plugins_and_tools_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🧩 Plugin Marketplace & Autonomous Tools",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        items(plugins) { plugin ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f)) {
                        Text(plugin.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(plugin.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF38BDF8))
                            Text(plugin.description, fontSize = 12.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Switch(
                        checked = plugin.isEnabled,
                        onCheckedChange = { viewModel.pluginManager.togglePluginEnabled(plugin.id) }
                    )
                }
            }
        }
    }
}
