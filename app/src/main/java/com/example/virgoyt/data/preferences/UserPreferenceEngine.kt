package com.example.virgoyt.data.preferences

import com.example.virgoyt.data.model.AiTone
import com.example.virgoyt.data.model.AppThemeMode
import com.example.virgoyt.data.model.CodeStylePreference
import com.example.virgoyt.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferenceEngine {

    private val _themeMode = MutableStateFlow(AppThemeMode.HOLOGRAPHIC_DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    fun setThemeMode(theme: AppThemeMode) {
        _themeMode.value = theme
    }

    fun toggleTheme() {
        _themeMode.value = if (_themeMode.value.isDark) AppThemeMode.ENGINEER_LIGHT else AppThemeMode.HOLOGRAPHIC_DARK
    }

    fun setAiTone(tone: AiTone) {
        _preferences.value = _preferences.value.copy(preferredAiTone = tone)
    }

    fun setCodeStyle(style: CodeStylePreference) {
        _preferences.value = _preferences.value.copy(codeStyle = style)
    }

    fun setAutoRunTests(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(autoRunTests = enabled)
    }

    fun setStreamAudio(enabled: Boolean) {
        _preferences.value = _preferences.value.copy(streamAudioResponses = enabled)
    }
}
