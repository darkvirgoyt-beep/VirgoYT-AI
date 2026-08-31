package com.example.manus.data.model

enum class TerminalMode(val label: String, val host: String, val promptPrefix: String, val description: String) {
    CLOUD_VM("Cloud VM", "asia-east1.cloud-node", "manus-cloud-pc", "Ubuntu 24.04 LTS (Remote Sandbox Container)"),
    LOCALHOST("Localhost :8080", "127.0.0.1:8080", "localhost", "Local Machine Bridge (Linux/Mac/Win/Termux)")
}

data class CliPlatformInstall(
    val platformName: String,
    val osBadge: String,
    val installCommand: String,
    val runCommand: String,
    val description: String,
    val requirements: String
)
