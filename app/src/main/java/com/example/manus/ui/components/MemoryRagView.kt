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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.rag.MemoryCategory
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
fun MemoryRagView(viewModel: ManusCloudViewModel) {
    val ragEngine = viewModel.memoryAndRagEngine
    val memories by ragEngine.memories.collectAsState()
    val searchResults by ragEngine.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<MemoryCategory?>(null) }

    var isAddingMemory by remember { mutableStateOf(false) }
    var newKey by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf(MemoryCategory.USER_PREFERENCE) }

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
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Memory RAG",
                        tint = ManusCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "Vector Memory & RAG Knowledge Retrieval",
                        color = ManusWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1536-Dim Embeddings • Sub-2ms Cosine Lookup • Zero Quality Loss",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            Button(
                onClick = { isAddingMemory = !isAddingMemory },
                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = ManusWhite, modifier = Modifier.size(14.dp))
                    Text("Store Memory", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Vector Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                ragEngine.searchKnowledgeBase(it)
            },
            placeholder = { Text("Search memory embeddings (e.g., 'unreal', 'database index', 'kotlin')...", color = ManusSlate400, fontSize = 11.5.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = ManusCyan, modifier = Modifier.size(16.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("rag_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ManusCyan,
                unfocusedBorderColor = SleekBorder,
                focusedTextColor = ManusWhite,
                unfocusedTextColor = ManusWhite,
                focusedContainerColor = ManusSlate900,
                unfocusedContainerColor = ManusSlate900
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Memory Category Filter
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
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "🧠 All Memories (${memories.size})",
                        color = if (isAllSelected) ManusCyan else ManusSlate300,
                        fontSize = 11.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            items(MemoryCategory.values().toList()) { cat ->
                val isSelected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) ManusIndigoBg else ManusSlate900)
                        .border(1.dp, if (isSelected) ManusCyan.copy(alpha = 0.5f) else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${cat.emoji} ${cat.label}",
                        color = if (isSelected) ManusCyan else ManusSlate300,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Add Memory Drawer (if open)
        if (isAddingMemory) {
            Card(
                colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ManusCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Store New Knowledge Vector into Agent Hive:", color = ManusWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        placeholder = { Text("Memory Key (e.g. backend_cache_policy)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ManusWhite, unfocusedTextColor = ManusWhite)
                    )
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        placeholder = { Text("Detailed memory content / prompt guidance...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = ManusWhite, unfocusedTextColor = ManusWhite)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (newKey.isNotBlank() && newContent.isNotBlank()) {
                                    ragEngine.storeMemory(newCategory, newKey, newContent)
                                    viewModel.showToast("✓ Stored 1536-dim embedding in Vector Database")
                                    newKey = ""
                                    newContent = ""
                                    isAddingMemory = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Index & Save", color = ManusWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Search Results or All Memories List
        val displayList = if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
            searchResults.map { it.entry }
        } else {
            if (selectedCategory != null) memories.filter { it.category == selectedCategory } else memories
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayList) { mem ->
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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = mem.category.emoji, fontSize = 14.sp)
                                Text(
                                    text = mem.key,
                                    color = ManusCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ManusIndigoBg)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${mem.vectorDimension}D EMBEDDING",
                                        color = ManusIndigoLight,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        ragEngine.deleteMemory(mem.id)
                                        viewModel.showToast("Deleted memory vector")
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ManusSlate500, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        Text(
                            text = mem.content,
                            color = ManusSlate200,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        // Tags
                        if (mem.tags.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(mem.tags) { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(ManusSlate950)
                                            .border(1.dp, SleekBorder, RoundedCornerShape(3.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("#$tag", color = ManusSlate400, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
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
