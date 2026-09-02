package com.example.virgoyt.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virgoyt.ui.VirgoCloudViewModel

@Composable
fun DatabaseAiStudioView(
    viewModel: VirgoCloudViewModel,
    modifier: Modifier = Modifier
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val tables by viewModel.dbEngine.tables.collectAsState()
    var sqlInput by remember { mutableStateOf("SELECT * FROM users LIMIT 10;") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("database_ai_studio_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🗄️ Database AI Studio (Postgres / SQLite)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (themeMode.isDark) Color.White else Color(0xFF0F172A)
            )
        }

        item {
            TextField(
                value = sqlInput,
                onValueChange = { sqlInput = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Write SQL query...") },
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.dbEngine.executeSql(sqlInput)
                    viewModel.showToast("SQL executed successfully")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4))
            ) {
                Text("Execute Query")
            }
        }

        items(tables) { table ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (themeMode.isDark) Color(0xFF0F172A) else Color(0xFFFFFFFF),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (themeMode.isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Table: ${table.name} (${table.rowCount} rows)", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
                    Text(table.columns.joinToString(", ") { "${it.name} (${it.type})" }, fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }
}
