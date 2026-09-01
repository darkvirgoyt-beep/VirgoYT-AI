package com.example.manus.data.auth

import com.example.manus.data.model.AuthSession
import com.example.manus.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class AuthManager {

    private val users = ConcurrentHashMap<String, User>()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    val currentUser: User?
        get() = _currentSession.value?.user

    init {
        // Seed default accounts
        val darkvirgo = User(
            username = "darkvirgoyt-beep",
            email = "darkvirgoyt@gmail.com",
            passwordHash = hashPassword("virgo123"),
            role = "Chief AI Architect",
            avatarColorHex = 0xFF00F0FF, // Cyan
            homeDir = "/home/darkvirgoyt-beep"
        )
        val defaultDev = User(
            username = "developer",
            email = "dev@virgoyt.ai",
            passwordHash = hashPassword("developer123"),
            role = "Lead Developer",
            avatarColorHex = 0xFF6366F1, // Indigo
            homeDir = "/workspace"
        )
        val defaultAdmin = User(
            username = "admin",
            email = "admin@virgoyt.ai",
            passwordHash = hashPassword("admin123"),
            role = "Root Administrator",
            avatarColorHex = 0xFF10B981, // Emerald
            homeDir = "/home/admin"
        )
        val guestUser = User(
            username = "guest",
            email = "guest@virgoyt.ai",
            passwordHash = hashPassword("guest123"),
            role = "Sandbox Guest",
            avatarColorHex = 0xFFF59E0B, // Amber
            homeDir = "/home/guest"
        )

        users[darkvirgo.username.lowercase()] = darkvirgo
        users[defaultDev.username.lowercase()] = defaultDev
        users[defaultAdmin.username.lowercase()] = defaultAdmin
        users[guestUser.username.lowercase()] = guestUser

        // Default login as 'darkvirgoyt-beep'
        _currentSession.value = AuthSession(user = darkvirgo)
    }

    fun getAllUsers(): List<User> = users.values.toList().sortedBy { it.username }

    fun signup(username: String, email: String, password: String): Result<User> {
        val cleanUsername = username.trim().lowercase()
        if (cleanUsername.length < 3) {
            return Result.failure(IllegalArgumentException("Username must be at least 3 characters long."))
        }
        if (!cleanUsername.matches("^[a-z0-9_-]+$".toRegex())) {
            return Result.failure(IllegalArgumentException("Username can only contain alphanumeric characters, underscores, and dashes."))
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
            email = if (email.isBlank()) "$cleanUsername@manus.cloud" else email.trim(),
            passwordHash = hashPassword(password),
            role = "Developer",
            avatarColorHex = assignedColor,
            homeDir = "/home/$cleanUsername"
        )

        users[cleanUsername] = newUser
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
            email = "guest@manus.cloud",
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
