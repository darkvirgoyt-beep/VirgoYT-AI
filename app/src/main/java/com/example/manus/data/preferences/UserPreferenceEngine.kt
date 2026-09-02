package com.example.manus.data.preferences

import com.example.manus.data.model.AiTone
import com.example.manus.data.model.AppThemeMode
import com.example.manus.data.model.CodeStylePreference
import com.example.manus.data.model.UserPreferences
import com.example.manus.data.model.VoiceProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferenceEngine {
    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private val _learnedTraits = MutableStateFlow<List<String>>(
        listOf(
            "Prefers declarative Kotlin Jetpack Compose over XML layouts",
            "Favors dark glassmorphism aesthetic with cyan/violet accents",
            "Frequently generates Unreal Engine 5 C++ & Blueprint architectures",
            "Prioritizes low-latency response streaming (<20ms per token)",
            "Prefers automatic tool execution for safe read & compilation operations"
        )
    )
    val learnedTraits: StateFlow<List<String>> = _learnedTraits.asStateFlow()

    fun updateTone(tone: AiTone) {
        _preferences.value = _preferences.value.copy(tone = tone)
    }

    fun updateThemeMode(mode: AppThemeMode) {
        _preferences.value = _preferences.value.copy(themeMode = mode)
    }

    fun updateCodeStyle(style: CodeStylePreference) {
        _preferences.value = _preferences.value.copy(codeStyle = style)
    }

    fun updateVoice(voice: VoiceProfile) {
        _preferences.value = _preferences.value.copy(activeVoice = voice)
    }

    fun updateSystemPrompt(prompt: String) {
        _preferences.value = _preferences.value.copy(customSystemPrompt = prompt)
    }

    fun toggleAutoRunTools() {
        _preferences.value = _preferences.value.copy(autoRunSafeTools = !_preferences.value.autoRunSafeTools)
    }

    fun learnNewTrait(trait: String) {
        if (!_learnedTraits.value.contains(trait)) {
            _learnedTraits.value = listOf(trait) + _learnedTraits.value
        }
    }
}
