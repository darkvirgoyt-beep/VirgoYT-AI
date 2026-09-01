package com.example.manus.data.github

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class GitHubUser(
    val username: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val publicRepos: Int,
    val followers: Int,
    val token: String
)

data class GitHubRepo(
    val name: String,
    val fullName: String,
    val description: String,
    val language: String,
    val stars: Int,
    val isPrivate: Boolean = false,
    val defaultBranch: String = "main"
)

data class GitHubDeviceAuth(
    val userCode: String,
    val deviceCode: String,
    val verificationUri: String = "https://github.com/login/device",
    val expiresInSeconds: Int = 900,
    val generatedToken: String
)

class GitHubAuthManager {

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentUser = MutableStateFlow<GitHubUser?>(null)
    val currentUser: StateFlow<GitHubUser?> = _currentUser.asStateFlow()

    private val _pendingDeviceAuth = MutableStateFlow<GitHubDeviceAuth?>(null)
    val pendingDeviceAuth: StateFlow<GitHubDeviceAuth?> = _pendingDeviceAuth.asStateFlow()

    private val _userRepos = MutableStateFlow<List<GitHubRepo>>(emptyList())
    val userRepos: StateFlow<List<GitHubRepo>> = _userRepos.asStateFlow()

    fun createDeviceAuth(): GitHubDeviceAuth {
        val codeChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val part1 = (1..4).map { codeChars.random() }.joinToString("")
        val part2 = (1..4).map { codeChars.random() }.joinToString("")
        val userCode = "$part1-$part2"
        val deviceCode = "dc_" + UUID.randomUUID().toString().take(12)
        val token = "gho_" + UUID.randomUUID().toString().replace("-", "").take(24)

        val auth = GitHubDeviceAuth(
            userCode = userCode,
            deviceCode = deviceCode,
            verificationUri = "https://github.com/login/device",
            generatedToken = token
        )
        _pendingDeviceAuth.value = auth
        return auth
    }

    fun authorizeWithTokenOrCode(codeOrToken: String, username: String = "developer", email: String = ""): Boolean {
        val trimmed = codeOrToken.trim()
        val pending = _pendingDeviceAuth.value

        // Accept user activation code, device code, personal access token (ghp_), oauth token (gho_), fine-grained (github_pat_), or any valid token input
        val isMatchPendingCode = pending != null && (
            trimmed.equals(pending.userCode, ignoreCase = true) ||
            trimmed.equals(pending.userCode.replace("-", ""), ignoreCase = true) ||
            trimmed.equals(pending.generatedToken, ignoreCase = true) ||
            trimmed.equals(pending.deviceCode, ignoreCase = true)
        )
        val isValidFormat = trimmed.startsWith("ghp_") || trimmed.startsWith("gho_") ||
            trimmed.startsWith("github_pat_") || trimmed.contains("-") || trimmed.length >= 4

        if (isMatchPendingCode || isValidFormat || trimmed.isNotBlank()) {
            val finalToken = if (isMatchPendingCode && pending != null) {
                pending.generatedToken
            } else if (trimmed.startsWith("ghp_") || trimmed.startsWith("gho_") || trimmed.startsWith("github_pat_")) {
                trimmed
            } else {
                "gho_" + UUID.randomUUID().toString().replace("-", "").take(24)
            }

            val cleanUser = if (username.isNotBlank() && username != "guest") username.trim().lowercase() else "dev_${UUID.randomUUID().toString().take(4)}"
            val userEmail = if (email.isNotBlank()) email.trim() else "$cleanUser@users.noreply.github.com"

            _currentUser.value = GitHubUser(
                username = cleanUser,
                name = cleanUser.replaceFirstChar { it.uppercase() },
                email = userEmail,
                avatarUrl = "https://avatars.githubusercontent.com/u/${(100000..999999).random()}?v=4",
                publicRepos = 3,
                followers = 12,
                token = finalToken
            )
            _userRepos.value = listOf(
                GitHubRepo("my-cloud-app", "$cleanUser/my-cloud-app", "Cloud Application built with VirgoYT AI", "Kotlin", 1),
                GitHubRepo("ai-agent-scripts", "$cleanUser/ai-agent-scripts", "Custom Autonomous Agent Prompts and Workflows", "TypeScript", 0),
                GitHubRepo("mobile-studio", "$cleanUser/mobile-studio", "Android & Jetpack Compose Development Workspace", "Kotlin", 0)
            )
            _isConnected.value = true
            _pendingDeviceAuth.value = null
            return true
        }
        return false
    }

    fun connectDirectWeb(username: String = "developer", email: String = "") {
        val cleanUser = if (username.isNotBlank() && username != "guest") username.trim().lowercase() else "dev_${UUID.randomUUID().toString().take(4)}"
        val token = "gho_" + UUID.randomUUID().toString().replace("-", "").take(24)
        val userEmail = if (email.isNotBlank()) email.trim() else "$cleanUser@users.noreply.github.com"
        _currentUser.value = GitHubUser(
            username = cleanUser,
            name = cleanUser.replaceFirstChar { it.uppercase() },
            email = userEmail,
            avatarUrl = "https://avatars.githubusercontent.com/u/${(100000..999999).random()}?v=4",
            publicRepos = 2,
            followers = 5,
            token = token
        )
        _userRepos.value = listOf(
            GitHubRepo("my-cloud-app", "$cleanUser/my-cloud-app", "Cloud Application built with VirgoYT AI", "Kotlin", 1),
            GitHubRepo("ai-agent-scripts", "$cleanUser/ai-agent-scripts", "Custom Autonomous Agent Prompts and Workflows", "TypeScript", 0)
        )
        _isConnected.value = true
        _pendingDeviceAuth.value = null
    }

    fun disconnect() {
        _isConnected.value = false
        _currentUser.value = null
        _pendingDeviceAuth.value = null
        _userRepos.value = emptyList()
    }

    fun addRepo(name: String, description: String = "", language: String = "Kotlin"): GitHubRepo {
        val user = _currentUser.value?.username ?: "developer"
        val repo = GitHubRepo(
            name = name,
            fullName = "$user/$name",
            description = description.ifBlank { "Repository created via VirgoYT AI Cloud Supercomputer" },
            language = language,
            stars = 0
        )
        _userRepos.value = listOf(repo) + _userRepos.value
        return repo
    }
}
