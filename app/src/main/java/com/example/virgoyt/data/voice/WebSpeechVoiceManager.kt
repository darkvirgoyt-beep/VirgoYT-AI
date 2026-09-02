package com.example.virgoyt.data.voice

import com.example.virgoyt.data.model.VoiceProfile
import com.example.virgoyt.data.model.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class SpeechEngineType(val label: String, val badge: String) {
    WEB_SPEECH_API("Web Speech API (W3C Standard)", "🌐"),
    ANDROID_RECOGNIZER("Android SpeechRecognizer", "📱"),
    SERVER_STREAMING_WHISPER("OpenAI Whisper Streaming", "🎙️"),
    GEMINI_MULTIMODAL_LIVE("Gemini 2.5 Live Voice", "⚡")
}

enum class VoiceDispatchTarget(val label: String, val iconEmoji: String) {
    ACTIVE_AGENT_CHAT("Main Agent Chat Prompt", "⚡"),
    CLOUD_TERMINAL("Cloud Terminal Command Line", "⌨️"),
    AI_CODE_EDITOR("AI Code Editor Inline", "💻"),
    SYSTEM_AUTOMATION("Workflow Trigger", "🔄")
}

data class VoiceTranscriptEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val confidence: Float,
    val isFinal: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class VoiceCommandLog(
    val id: String = UUID.randomUUID().toString(),
    val transcript: String,
    val dispatchedTo: VoiceDispatchTarget,
    val timestamp: Long = System.currentTimeMillis()
)

class WebSpeechVoiceManager {

    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _activeProfile = MutableStateFlow(
        VoiceProfile(id = "virgo-cyber", name = "Virgo Cybernetic AI", locale = "en-US", pitch = 1.05f, rate = 1.0f, gender = "Female")
    )
    val activeProfile: StateFlow<VoiceProfile> = _activeProfile.asStateFlow()

    private val _transcripts = MutableStateFlow<List<VoiceTranscriptEntry>>(emptyList())
    val transcripts: StateFlow<List<VoiceTranscriptEntry>> = _transcripts.asStateFlow()

    private val _commandLogs = MutableStateFlow<List<VoiceCommandLog>>(emptyList())
    val commandLogs: StateFlow<List<VoiceCommandLog>> = _commandLogs.asStateFlow()

    private val _dispatchTarget = MutableStateFlow(VoiceDispatchTarget.ACTIVE_AGENT_CHAT)
    val dispatchTarget: StateFlow<VoiceDispatchTarget> = _dispatchTarget.asStateFlow()

    fun setDispatchTarget(target: VoiceDispatchTarget) {
        _dispatchTarget.value = target
    }

    fun setVoiceProfile(profile: VoiceProfile) {
        _activeProfile.value = profile
    }

    fun startListening() {
        _voiceState.value = VoiceState.LISTENING
    }

    fun stopListening() {
        _voiceState.value = VoiceState.IDLE
    }

    fun simulateVoiceInput(spokenText: String, onDispatch: (String, VoiceDispatchTarget) -> Unit) {
        _voiceState.value = VoiceState.LISTENING
        val entry = VoiceTranscriptEntry(text = spokenText, confidence = 0.96f, isFinal = true)
        _transcripts.value = _transcripts.value + entry
        _commandLogs.value = _commandLogs.value + VoiceCommandLog(transcript = spokenText, dispatchedTo = _dispatchTarget.value)
        _voiceState.value = VoiceState.PROCESSING
        onDispatch(spokenText, _dispatchTarget.value)
        _voiceState.value = VoiceState.IDLE
    }
}
