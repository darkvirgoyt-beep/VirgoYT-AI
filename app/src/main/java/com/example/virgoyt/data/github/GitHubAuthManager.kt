package com.example.virgoyt.data.github

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class GitHubUser(
    val id: String = UUID.randomUUID().toString(),
    val login: String,
    val name: String,
    val avatarUrl: String,
    val publicRepos: Int = 18,
    val bio: String = "Building autonomous multi-agent systems with VirgoYT"
)

data class GitHubRepo(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val fullName: String,
    val isPrivate: Boolean = false,
    val description: String,
    val language: String = "Kotlin",
    val stars: Int = 142,
    val defaultBranch: String = "main",
    val htmlUrl: String = "https://github.com/virgoyt/$name"
)

data class GitHubDeviceAuth(
    val deviceCode: String = UUID.randomUUID().toString().substring(0, 8).uppercase(),
    val userCode: String = "VIRG-9982",
    val verificationUri: String = "https://github.com/login/device",
    val expiresInSeconds: Int = 900
)

class GitHubAuthManager {

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<GitHubUser?>(
        GitHubUser(
            login = "virgoyt-dev",
            name = "VirgoYT Engineering",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
        )
    )
    val currentUser: StateFlow<GitHubUser?> = _currentUser.asStateFlow()

    private val _repositories = MutableStateFlow<List<GitHubRepo>>(
        listOf(
            GitHubRepo(name = "virgoyt-cloud-ai", fullName = "virgoyt/virgoyt-cloud-ai", description = "Autonomous cloud platform and multi-agent AI engine", stars = 520, language = "Kotlin"),
            GitHubRepo(name = "nextjs-fastapi-template", fullName = "virgoyt/nextjs-fastapi-template", description = "Next.js 15 App Router with Python FastAPI runtime", stars = 230, language = "TypeScript"),
            GitHubRepo(name = "threejs-cyber-world", fullName = "virgoyt/threejs-cyber-world", description = "Procedural WebGL cybernetic 3D environment", stars = 89, language = "JavaScript")
        )
    )
    val repositories: StateFlow<List<GitHubRepo>> = _repositories.asStateFlow()

    private val _deviceAuth = MutableStateFlow<GitHubDeviceAuth?>(null)
    val deviceAuth: StateFlow<GitHubDeviceAuth?> = _deviceAuth.asStateFlow()

    fun startDeviceFlow() {
        _deviceAuth.value = GitHubDeviceAuth()
    }

    fun completeDeviceFlow(userToken: String = "gho_sample_token") {
        _isAuthenticated.value = true
        _currentUser.value = GitHubUser(
            login = "virgoyt-engineer",
            name = "VirgoYT Developer",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150"
        )
        _deviceAuth.value = null
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentUser.value = null
    }

    fun createRepository(name: String, description: String, isPrivate: Boolean): GitHubRepo {
        val repo = GitHubRepo(
            name = name,
            fullName = "virgoyt/$name",
            description = description,
            isPrivate = isPrivate
        )
        _repositories.value = _repositories.value + repo
        return repo
    }
}
