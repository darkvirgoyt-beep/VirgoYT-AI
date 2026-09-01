package com.example.manus.data.auth

import com.example.manus.data.model.AuthSession
import com.example.manus.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AuthManager {

    private val users = ConcurrentHashMap<String, User>()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    val currentUser: User?
        get() = _currentSession.value?.user

    init {
        // Seed default guest account
        val guestUser = User(
            username = "guest",
            displayName = "Guest Developer",
            email = "guest@virgoyt.cloud",
            passwordHash = hashPassword("guest123"),
            role = "Guest Developer",
            avatarColorHex = 0xFF6366F1, // Indigo
            homeDir = "/home/guest"
        )
        users[guestUser.username.lowercase()] = guestUser

        // Default session starts as generic Guest (user has to sign up or log in)
        _currentSession.value = AuthSession(user = guestUser)
    }

    fun getAllUsers(): List<User> = users.values.toList().sortedBy { it.username }

    fun signup(username: String, email: String, password: String, displayName: String = ""): Result<User> {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters long."))
        }
        if (!cleanUsername.matches("^[a-z0-9_.-]+$".toRegex())) {
            return Result.failure(IllegalArgumentException("Username can only contain alphanumeric characters, underscores, dots, and dashes."))
        }
        if (users.containsKey(cleanUsername)) {
            return Result.failure(IllegalArgumentException("Username '$cleanUsername' is already registered."))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val colors = listOf(0xFF6366F1, 0xFF10B981, 0xFF00F0FF, 0xFFF59E0B, 0xFF8B5CF6, 0xFFEC4899)
        val assignedColor = colors[users.size % colors.size]

        val newUser = User(
            username = cleanUsername,
            displayName = if (displayName.isNotBlank()) displayName.trim() else cleanUsername,
            email = if (email.isBlank()) "$cleanUsername@virgoyt.cloud" else email.trim(),
            passwordHash = hashPassword(password),
            role = "Developer",
            avatarColorHex = assignedColor,
            homeDir = "/home/$cleanUsername"
        )

        users[cleanUsername] = newUser
        _currentSession.value = AuthSession(user = newUser)
        return Result.success(newUser)
    }

    fun signupWithGoogle(accountEmail: String, name: String): Result<User> {
        val cleanEmail = accountEmail.trim().lowercase()
        val baseUsername = cleanEmail.substringBefore("@").replace("[^a-z0-9_]".toRegex(), "")
        val username = if (baseUsername.length < 3) "user_${cleanEmail.hashCode().toString().takeLast(4)}" else baseUsername
        var uniqueUsername = username
        var counter = 1
        while (users.containsKey(uniqueUsername)) {
            uniqueUsername = "${username}_$counter"
            counter++
        }

        val colors = listOf(0xFF4285F4, 0xFF34A853, 0xFFFBBC05, 0xFFEA4335, 0xFF6366F1)
        val assignedColor = colors[users.size % colors.size]

        val newUser = User(
            username = uniqueUsername,
            displayName = if (name.isNotBlank()) name.trim() else uniqueUsername,
            email = cleanEmail,
            passwordHash = hashPassword("google_oauth_${UUID.randomUUID()}"),
            role = "Google Verified Developer",
            avatarColorHex = assignedColor,
            homeDir = "/home/$uniqueUsername"
        )
        users[uniqueUsername] = newUser
        _currentSession.value = AuthSession(user = newUser)
        return Result.success(newUser)
    }

    fun signupWithGitHub(githubUsername: String, email: String, name: String): Result<User> {
        val cleanUsername = githubUsername.trim().lowercase().replace("[^a-z0-9_-]".toRegex(), "")
        var uniqueUsername = cleanUsername.ifBlank { "gh_user" }
        var counter = 1
        while (users.containsKey(uniqueUsername)) {
            uniqueUsername = "${cleanUsername}_$counter"
            counter++
        }

        val newUser = User(
            username = uniqueUsername,
            displayName = if (name.isNotBlank()) name.trim() else "@$uniqueUsername",
            email = if (email.isNotBlank()) email.trim() else "$uniqueUsername@users.noreply.github.com",
            passwordHash = hashPassword("github_oauth_${UUID.randomUUID()}"),
            role = "GitHub Authenticated Developer",
            avatarColorHex = 0xFF24292E,
            homeDir = "/home/$uniqueUsername"
        )
        users[uniqueUsername] = newUser
        _currentSession.value = AuthSession(user = newUser)
        return Result.success(newUser)
    }

    fun login(username: String, password: String): Result<User> {
        val cleanUsername = username.trim().lowercase()
        val user = users[cleanUsername]
            ?: return Result.failure(IllegalArgumentException("User '$cleanUsername' does not exist."))

        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return Result.failure(IllegalArgumentException("Invalid password for user '$cleanUsername'."))
        }

        _currentSession.value = AuthSession(user = user)
        return Result.success(user)
    }

    fun logout() {
        // Fallback to guest user on logout to maintain session continuity in cloud terminal
        val guest = users["guest"] ?: User(
            username = "guest",
            email = "guest@virgoyt.cloud",
            passwordHash = hashPassword("guest123"),
            role = "Guest",
            avatarColorHex = 0xFF00F0FF,
            homeDir = "/home/guest"
        )
        _currentSession.value = AuthSession(user = guest)
    }

    fun switchUser(username: String): Boolean {
        val user = users[username.trim().lowercase()] ?: return false
        _currentSession.value = AuthSession(user = user)
        return true
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
