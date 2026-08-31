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

    private val _userRepos = MutableStateFlow<List<GitHubRepo>>(
        listOf(
            GitHubRepo("VirgoYT-AI", "darkvirgoyt-beep/VirgoYT-AI", "Autonomous AI Software Engineer Platform & Multi-Agent Swarm", "Kotlin", 420),
            GitHubRepo("autonomous-agent-hive", "darkvirgoyt-beep/autonomous-agent-hive", "15 Specialized Sub-Agents with Vector Communication", "TypeScript", 185),
            GitHubRepo("unreal-engine-ai-bridge", "darkvirgoyt-beep/unreal-engine-ai-bridge", "Realtime AI code generation bridge for UE5 and Unity", "C++", 142),
            GitHubRepo("vector-rag-engine", "darkvirgoyt-beep/vector-rag-engine", "1536-Dimensional embedding memory lookup system", "Rust", 97)
        )
    )
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

    fun authorizeWithTokenOrCode(codeOrToken: String, username: String = "darkvirgoyt-beep"): Boolean {
        val trimmed = codeOrToken.trim()
        val pending = _pendingDeviceAuth.value

        val valid = trimmed.isNotEmpty() && (
            (pending != null && (trimmed.equals(pending.userCode, ignoreCase = true) || trimmed.equals(pending.generatedToken, ignoreCase = true))) ||
            trimmed.startsWith("ghp_") || trimmed.startsWith("gho_") || trimmed.startsWith("github_pat_") ||
            trimmed.contains("-") || trimmed.length >= 6
        )

        if (valid) {
            val finalToken = if (pending != null && (trimmed.equals(pending.userCode, ignoreCase = true) || trimmed.equals(pending.generatedToken, ignoreCase = true))) {
                pending.generatedToken
            } else {
                "gho_" + UUID.randomUUID().toString().replace("-", "").take(24)
            }

            _currentUser.value = GitHubUser(
                username = username,
                name = "VirgoYT AI",
                email = "darkvirgoyt@gmail.com",
                avatarUrl = "https://avatars.githubusercontent.com/u/849201?v=4",
                publicRepos = _userRepos.value.size,
                followers = 256,
                token = finalToken
            )
            _isConnected.value = true
            _pendingDeviceAuth.value = null
            return true
        }
        return false
    }

    fun connectDirectWeb(username: String = "darkvirgoyt-beep") {
        val token = "gho_" + UUID.randomUUID().toString().replace("-", "").take(24)
        _currentUser.value = GitHubUser(
            username = username,
            name = "VirgoYT AI",
            email = "darkvirgoyt@gmail.com",
            avatarUrl = "https://avatars.githubusercontent.com/u/849201?v=4",
            publicRepos = _userRepos.value.size,
            followers = 256,
            token = token
        )
        _isConnected.value = true
        _pendingDeviceAuth.value = null
    }

    fun disconnect() {
        _isConnected.value = false
        _currentUser.value = null
        _pendingDeviceAuth.value = null
    }

    fun addRepo(name: String, description: String = "", language: String = "Kotlin"): GitHubRepo {
        val user = _currentUser.value?.username ?: "darkvirgoyt-beep"
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
