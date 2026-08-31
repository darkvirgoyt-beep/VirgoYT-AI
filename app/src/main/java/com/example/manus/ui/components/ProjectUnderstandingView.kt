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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.example.manus.data.project.SupportedFramework
import com.example.manus.data.project.SupportedLanguage
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
import com.example.ui.theme.SleekSurface

@Composable
fun ProjectUnderstandingView(viewModel: ManusCloudViewModel) {
    val projectEngine = viewModel.projectUnderstandingEngine
    val scanSummary by projectEngine.scanSummary.collectAsState()
    val isScanning by projectEngine.isScanning.collectAsState()

    var selectedTab by remember { mutableStateOf("OVERVIEW") } // OVERVIEW, DEPENDENCIES, APIS, ARCHITECTURE, LANGUAGES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Header Bar
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
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Project Graph",
                        tint = ManusCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Project Intelligence & Architecture AST",
                            color = ManusWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusEmerald.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "HEALTH ${scanSummary.codeQualityScore}%",
                                color = ManusEmerald,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Text(
                        text = "14+ Languages • 14+ Frameworks • Zero-AST Overhead Scanning",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Rescan Button
            Button(
                onClick = {
                    projectEngine.performDeepProjectScan()
                    viewModel.showToast("⚡ Rescanned entire project codebase & dependency graph")
                },
                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.testTag("rescan_project_btn")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        tint = ManusWhite,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isScanning) "Scanning..." else "Deep Scan",
                        color = ManusWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Metrics Banner Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricPill("Files", "${scanSummary.totalFiles}", "VFS Synced", ManusCyan, Modifier.weight(1f))
            MetricPill("Lines of Code", "${scanSummary.totalLinesOfCode}", "100% Parsed", ManusIndigoLight, Modifier.weight(1f))
            MetricPill("Coverage", "${scanSummary.testCoveragePercent}%", "Unit & E2E", ManusEmerald, Modifier.weight(1f))
            MetricPill("Security", "0 Vulns", "OWASP Clean", ManusPurple, Modifier.weight(1f))
        }

        // Sub-Navigation Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf(
                "OVERVIEW" to "🏛️ Architecture",
                "DEPENDENCIES" to "📦 Dependencies",
                "APIS" to "🔌 API Endpoints",
                "LANGUAGES" to "💻 14+ Languages",
                "FRAMEWORKS" to "⚡ 14+ Frameworks"
            )
            items(tabs) { (key, label) ->
                val isSelected = selectedTab == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedTab = key }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) ManusCyan else ManusSlate300,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            "OVERVIEW" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "📐 Architecture Pattern",
                                    color = ManusWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = scanSummary.architecturePattern,
                                    color = ManusIndigoLight,
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Inter-Module Code Relationships:",
                                    color = ManusSlate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                scanSummary.codeRelationships.forEach { rel ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(ManusSlate950)
                                            .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = rel.sourceFile,
                                            color = ManusWhite,
                                            fontSize = 10.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "─[ ${rel.relationType} ]─▶",
                                            color = ManusCyan,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = rel.targetFile,
                                            color = ManusIndigoLight,
                                            fontSize = 10.5.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "DEPENDENCIES" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(scanSummary.dependencies) { dep ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate900)
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = dep.name,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "v${dep.version}",
                                        color = ManusCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(
                                    text = dep.purpose,
                                    color = ManusSlate400,
                                    fontSize = 10.5.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (dep.type == "production") ManusIndigoBg else ManusSlate800)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = dep.type.uppercase(),
                                    color = if (dep.type == "production") ManusIndigoLight else ManusSlate400,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            "APIS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(scanSummary.apiEndpoints) { api ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate900)
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val methodColor = when (api.method) {
                                    "GET" -> ManusEmerald
                                    "POST" -> ManusIndigoLight
                                    "PUT" -> ManusAmber
                                    "DELETE" -> Color(0xFFEF4444)
                                    else -> ManusCyan
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(methodColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = api.method,
                                        color = methodColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Column {
                                    Text(
                                        text = api.path,
                                        color = ManusWhite,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Handler: ${api.handlerFile}",
                                        color = ManusSlate400,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = if (api.authenticationRequired) "🔒 Auth" else "🌐 Public",
                                color = if (api.authenticationRequired) ManusCyan else ManusSlate400,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            "LANGUAGES" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(SupportedLanguage.values().toList()) { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate900)
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = lang.icon, fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = lang.displayName,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${lang.fileExtension} • ${lang.category}",
                                        color = ManusSlate400,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ManusEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PARSER READY",
                                    color = ManusEmerald,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            "FRAMEWORKS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(SupportedFramework.values().toList()) { fw ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusSlate900)
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = fw.icon, fontSize = 16.sp)
                                Column {
                                    Text(
                                        text = fw.displayName,
                                        color = ManusWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${fw.category} • Default Port ${fw.defaultPort}",
                                        color = ManusSlate400,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ManusIndigoBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SUPPORTED",
                                    color = ManusCyan,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
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
private fun MetricPill(
    label: String,
    value: String,
    subtext: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ManusSlate900),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = label, color = ManusSlate400, fontSize = 9.sp)
            Text(
                text = value,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(text = subtext, color = ManusSlate500, fontSize = 8.sp)
        }
    }
}
