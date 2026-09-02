package com.example.virgoyt.data.voice

class VoiceAssistantEngine(
    val voiceManager: WebSpeechVoiceManager = WebSpeechVoiceManager()
) {
    val voiceState = voiceManager.voiceState
    val activeProfile = voiceManager.activeProfile
    val transcripts = voiceManager.transcripts
    val commandLogs = voiceManager.commandLogs
    val dispatchTarget = voiceManager.dispatchTarget
}
