package com.example.virgoyt.data.agent

import com.example.virgoyt.data.model.DiffFileSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CursorAiResult(
    val generatedCode: String,
    val diff: DiffFileSnapshot,
    val explanation: String
)

class CursorAiAssistantService {

    private val _recentDiffs = MutableStateFlow<List<DiffFileSnapshot>>(emptyList())
    val recentDiffs: StateFlow<List<DiffFileSnapshot>> = _recentDiffs.asStateFlow()

    fun recordDiff(filePath: String, oldContent: String, newContent: String) {
        val diff = DiffFileSnapshot(
            filePath = filePath,
            originalContent = oldContent,
            modifiedContent = newContent,
            additions = newContent.lines().size,
            deletions = oldContent.lines().size
        )
        _recentDiffs.value = listOf(diff) + _recentDiffs.value
    }
}
