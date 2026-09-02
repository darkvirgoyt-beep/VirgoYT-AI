package com.example.virgoyt.ui.components

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun LiveComputerDiffView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val diffs by viewModel.cursorAiService.recentDiffs.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("live_computer_diff_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🖥️ Cloud PC Live Diff & AST Inspector",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        if (diffs.isEmpty()) {
            item {
                Text("No pending diffs. Working directory clean.", fontSize = 13.sp, color = Color(0xFF94A3B8))
            }
        } else {
            items(diffs) { diff ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(diff.filePath, fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                        Text("+${diff.additions} / -${diff.deletions} lines", fontSize = 11.sp, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}
