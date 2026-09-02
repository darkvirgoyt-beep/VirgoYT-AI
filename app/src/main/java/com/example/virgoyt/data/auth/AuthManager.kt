package com.example.virgoyt.data.auth

import com.example.virgoyt.data.model.AuthSession
import com.example.virgoyt.data.model.User
import com.example.virgoyt.data.model.UserPreferences
import com.example.virgoyt.data.vfs.VirtualFileSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthManager(private val vfs: VirtualFileSystem? = null) {

    private val users = mutableMapOf<String, User>()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    val currentSession: StateFlow<AuthSession?> = _currentSession.asStateFlow()

    init {
        val defaultAdmin = User(
            username = "virgoyt",
            email = "developer@virgoyt.ai",
            fullName = "VirgoYT Master Architect",
            role = "Super Admin"
        )
        users[defaultAdmin.username] = defaultAdmin
        _currentSession.value = AuthSession(user = defaultAdmin)
        provisionUserWorkspace(defaultAdmin)
    }

    fun login(username: String, email: String = ""): Boolean {
        val user = users[username] ?: User(
            username = username,
            email = if (email.isNotEmpty()) email else "$username@virgoyt.ai",
            fullName = username.replaceFirstChar { it.uppercase() },
            role = "Cloud Developer"
        ).also {
            users[username] = it
            provisionUserWorkspace(it)
        }
        _currentSession.value = AuthSession(user = user)
        return true
    }

    fun logout() {
        _currentSession.value = null
    }

    fun updateUserPreferences(preferences: UserPreferences) {
        val curr = _currentSession.value ?: return
        val updatedUser = curr.user.copy(preferences = preferences)
        users[updatedUser.username] = updatedUser
        _currentSession.value = curr.copy(user = updatedUser)
    }

    fun updateUserProfile(fullName: String, email: String, role: String) {
        val curr = _currentSession.value ?: return
        val updatedUser = curr.user.copy(
            fullName = fullName,
            email = email,
            role = role
        )
        users[updatedUser.username] = updatedUser
        _currentSession.value = curr.copy(user = updatedUser)
    }

    fun getAllUsers(): List<User> = users.values.sortedBy { it.username }

    private fun provisionUserWorkspace(user: User) {
        vfs?.let {
            it.createDirectory(user.homeDir, user.username)
            it.addFile("${user.homeDir}/welcome.txt", "welcome.txt", "Welcome to your VirgoYT isolated cloud workspace, ${user.fullName}!", user.username)
        }
    }
}
