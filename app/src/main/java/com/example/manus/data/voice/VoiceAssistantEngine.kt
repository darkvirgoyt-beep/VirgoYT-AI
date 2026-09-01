package com.example.manus.data.voice

import com.example.manus.data.model.VoiceProfile
import com.example.manus.data.model.VoiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class VoiceTranscriptEntry(
    val id: String = UUID.randomUUID().toString(),
    val speaker: String, // "User" or "VirgoYT AI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class VoiceAssistantEngine(private val scope: CoroutineScope) {
    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _currentVoice = MutableStateFlow(VoiceProfile())
    val currentVoice: StateFlow<VoiceProfile> = _currentVoice.asStateFlow()

    private val _liveTranscript = MutableStateFlow("")
    val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<VoiceTranscriptEntry>>(
        listOf(
            VoiceTranscriptEntry(
                speaker = "VirgoYT AI",
                text = "Voice Assistant initialized. Say 'Build an Unreal 5 game scene' or 'Run multi-agent audit'."
            )
        )
    )
    val conversationHistory: StateFlow<List<VoiceTranscriptEntry>> = _conversationHistory.asStateFlow()

    private val _waveformAmplitudes = MutableStateFlow<List<Float>>(List(24) { 0.2f })
    val waveformAmplitudes: StateFlow<List<Float>> = _waveformAmplitudes.asStateFlow()

    private var voiceJob: Job? = null
    private var waveformJob: Job? = null

    val availableVoices = listOf(
        VoiceProfile("virgo_neural_aura", "Aura (Neural Hologram)", "Female", 1.05f, 1.0f, "Futuristic and soothing"),
        VoiceProfile("virgo_cyber_nova", "Nova (Cyber Architect)", "Male", 0.95f, 1.05f, "Authoritative and technical"),
        VoiceProfile("virgo_quantum_echo", "Quantum Echo", "Neutral", 1.0f, 1.1f, "Crisp algorithmic synthesizer"),
        VoiceProfile("virgo_matrix_vibe", "Matrix Vibe", "Female", 1.15f, 0.95f, "Deep step-by-step guidance")
    )

    fun selectVoice(profile: VoiceProfile) {
        _currentVoice.value = profile
    }

    fun startListening(onCommandRecognized: (String) -> Unit) {
        if (_voiceState.value == VoiceState.LISTENING) return
        _voiceState.value = VoiceState.LISTENING
        _liveTranscript.value = ""

        startWaveformAnimation()

        voiceJob?.cancel()
        voiceJob = scope.launch(Dispatchers.Default) {
            val phrases = listOf(
                "Analyzing audio spectrum...",
                "Listening to speech input...",
                "Processing natural language request..."
            )
            for (p in phrases) {
                delay(600)
                if (_voiceState.value != VoiceState.LISTENING) return@launch
                _liveTranscript.value = p
            }
        }
    }

    fun stopListeningAndSubmit(onCommandRecognized: (String) -> Unit) {
        if (_voiceState.value != VoiceState.LISTENING) return
        _voiceState.value = VoiceState.PROCESSING

        voiceJob?.cancel()
        voiceJob = scope.launch(Dispatchers.Default) {
            delay(500)
            val sampleQueries = listOf(
                "Synthesize 3D procedural terrain for Unreal Engine 5 with Nanite foliage",
                "Execute full-stack security audit across all virtual workspace files",
                "Deploy React and Next.js frontend with Tailwind CSS glassmorphism",
                "Query vector database memory for user preference embeddings"
            )
            val query = sampleQueries.random()
            _liveTranscript.value = query
            _conversationHistory.value = _conversationHistory.value + VoiceTranscriptEntry(speaker = "User", text = query)

            delay(400)
            _voiceState.value = VoiceState.SPEAKING
            val reply = "Executing request: $query. Routing to VirgoYT AI engine."
            _conversationHistory.value = _conversationHistory.value + VoiceTranscriptEntry(speaker = "VirgoYT AI", text = reply)

            onCommandRecognized(query)

            delay(2500)
            _voiceState.value = VoiceState.IDLE
            stopWaveformAnimation()
        }
    }

    fun triggerVoiceResponse(text: String) {
        _voiceState.value = VoiceState.SPEAKING
        startWaveformAnimation()
        scope.launch(Dispatchers.Default) {
            _conversationHistory.value = _conversationHistory.value + VoiceTranscriptEntry(speaker = "VirgoYT AI", text = text)
            delay(3000)
            _voiceState.value = VoiceState.IDLE
            stopWaveformAnimation()
        }
    }

    private fun startWaveformAnimation() {
        waveformJob?.cancel()
        waveformJob = scope.launch(Dispatchers.Default) {
            while (_voiceState.value != VoiceState.IDLE) {
                _waveformAmplitudes.value = List(24) { (0.15f + Math.random().toFloat() * 0.85f) }
                delay(90)
            }
            _waveformAmplitudes.value = List(24) { 0.1f }
        }
    }

    private fun stopWaveformAnimation() {
        waveformJob?.cancel()
        _waveformAmplitudes.value = List(24) { 0.1f }
    }
}
