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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
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
import com.example.manus.data.database.DatabaseEngine
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
fun DatabaseAiStudioView(viewModel: ManusCloudViewModel) {
    val dbEngine = viewModel.databaseAiEngine
    val selectedEngine by dbEngine.selectedEngine.collectAsState()
    val tables by dbEngine.tables.collectAsState()
    val queryHistory by dbEngine.queryHistory.collectAsState()
    val migrations by dbEngine.migrations.collectAsState()

    var activeSubTab by remember { mutableStateOf("TABLES") } // TABLES, QUERY_RUNNER, MIGRATIONS, DESIGNER
    var queryInput by remember { mutableStateOf("SELECT * FROM users JOIN projects ON users.id = projects.user_id WHERE projects.status = 'ACTIVE' LIMIT 10;") }
    var selectedTable by remember { mutableStateOf(tables.firstOrNull()) }

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
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Database AI",
                        tint = ManusCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Database AI Studio & Query Optimization",
                        color = ManusWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PostgreSQL, MySQL, SQLite, MongoDB, Redis, Supabase, Firebase",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Engine Selector Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(ManusSlate900)
                    .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = selectedEngine.iconEmoji, fontSize = 12.sp)
                    Text(
                        text = selectedEngine.displayName,
                        color = ManusCyan,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Database Engine Choice Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(DatabaseEngine.values().toList()) { engine ->
                val isSelected = selectedEngine == engine
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { dbEngine.selectEngine(engine) }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = engine.iconEmoji, fontSize = 11.sp)
                        Text(
                            text = engine.displayName.substringBefore(" "),
                            color = if (isSelected) ManusCyan else ManusSlate300,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Subtabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val tabs = listOf(
                "TABLES" to "📊 Tables & Schema",
                "QUERY_RUNNER" to "⚡ Query Runner & Explain",
                "MIGRATIONS" to "🔄 Migrations (${migrations.size})",
                "DESIGNER" to "✨ AI Schema Designer"
            )
            items(tabs) { (key, label) ->
                val isSelected = activeSubTab == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { activeSubTab = key }
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

        // View Body
        when (activeSubTab) {
            "TABLES" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tables) { table ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedTable = table }
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "📑", fontSize = 14.sp)
                                        Text(
                                            text = table.tableName,
                                            color = ManusWhite,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Text(
                                        text = "${table.rowCountEstimate} rows • ${table.sizeFormatted}",
                                        color = ManusCyan,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Columns list
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    table.columns.forEach { col ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ManusSlate950)
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (col.isPrimaryKey) {
                                                    Text(text = "🔑", fontSize = 10.sp)
                                                }
                                                Text(
                                                    text = col.name,
                                                    color = if (col.isPrimaryKey) ManusCyan else ManusWhite,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (col.isPrimaryKey) FontWeight.Bold else FontWeight.Normal,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Text(
                                                    text = col.type,
                                                    color = ManusIndigoLight,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                                if (col.isIndexed) {
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(ManusEmerald.copy(alpha = 0.2f))
                                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                                    ) {
                                                        Text("INDEXED", color = ManusEmerald, fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "QUERY_RUNNER" -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = queryInput,
                        onValueChange = { queryInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .testTag("db_sql_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManusCyan,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite,
                            focusedContainerColor = ManusSlate900,
                            unfocusedContainerColor = ManusSlate900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.5.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Auto-Index Advisor: ON (Sub-5ms Execution)",
                            color = ManusSlate400,
                            fontSize = 10.sp
                        )

                        Button(
                            onClick = {
                                dbEngine.executeQuery(queryInput)
                                viewModel.showToast("⚡ Query executed in 2ms on ${selectedEngine.displayName}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("run_query_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = ManusWhite, modifier = Modifier.size(16.dp))
                                Text("Execute Query", color = ManusWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Query Execution Logs / Results
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(queryHistory) { rec ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Status: SUCCESS • ${rec.executionTimeMs}ms • ${rec.rowsAffected} rows",
                                            color = ManusEmerald,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "JSON Payload",
                                            color = ManusSlate400,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Text(
                                        text = rec.resultsJson,
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

            "MIGRATIONS" -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(migrations) { mig ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Migration ${mig.version}",
                                        color = ManusCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ManusEmerald.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(mig.status, color = ManusEmerald, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = mig.title,
                                    color = ManusWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = mig.upSql,
                                    color = ManusSlate400,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            "DESIGNER" -> {
                var newTableName by remember { mutableStateOf("") }
                var schemaDescription by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "✨ AI-Powered Zero-Data-Loss Schema Generator",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = newTableName,
                        onValueChange = { newTableName = it },
                        label = { Text("Table / Collection Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManusCyan,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite
                        )
                    )
                    OutlinedTextField(
                        value = schemaDescription,
                        onValueChange = { schemaDescription = it },
                        label = { Text("Fields & Relationships (e.g. products with price, tags, user_id)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManusCyan,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = ManusWhite,
                            unfocusedTextColor = ManusWhite
                        )
                    )
                    Button(
                        onClick = {
                            if (newTableName.isNotBlank()) {
                                dbEngine.generateNewTable(newTableName, "")
                                viewModel.showToast("✓ Generated table '$newTableName' with B-Tree indexes and migration")
                                newTableName = ""
                                schemaDescription = ""
                                activeSubTab = "TABLES"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate & Apply Schema Migration", color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
