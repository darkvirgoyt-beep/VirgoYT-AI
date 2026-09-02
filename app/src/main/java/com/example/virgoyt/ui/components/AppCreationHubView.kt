package com.example.virgoyt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun AppCreationHubView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val templates by viewModel.appGenEngine.templates.collectAsState()
    val generatedApps by viewModel.appGenEngine.generatedApps.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("app_creation_hub_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🚀 App Creation & Deployment Studio",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        item {
            Text(
                text = "Scaffold production full-stack SaaS apps with one tap",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        items(templates) { template ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row {
                        Text(template.iconEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(template.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF38BDF8))
                            Text(template.description, fontSize = 12.sp, color = if (themeMode.isDark) Color(0xFFCBD5E1) else Color(0xFF475569))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.appGenEngine.generateAppFromTemplate(template, "App-${System.currentTimeMillis() % 1000}")
                            viewModel.showToast("Deployed template: ${template.title}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
                    ) {
                        Text("Deploy Application", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
