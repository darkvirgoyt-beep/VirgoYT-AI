package com.example.manus.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.manus.data.model.User
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusBorderLight
import com.example.ui.theme.ManusCyanAccent
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder

enum class AuthTab {
    LOGIN,
    SIGNUP,
    PROFILES
}

@Composable
fun AuthDialog(viewModel: ManusCloudViewModel) {
    val isDialogOpen by viewModel.isAuthDialogOpen.collectAsState()
    val session by viewModel.currentSession.collectAsState()
    val currentUser = session?.user

    var activeAuthTab by remember { mutableStateOf(AuthTab.LOGIN) }
    var usernameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (!isDialogOpen) return

    Dialog(onDismissRequest = { viewModel.closeAuthDialog() }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                .testTag("auth_dialog"),
            color = ManusSlate950
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ManusIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security",
                                tint = ManusIndigoLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Terminal Authentication",
                                color = ManusWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Isolated User Sessions & VFS Sandboxes",
                                color = ManusSlate400,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.closeAuthDialog() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ManusSlate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current Active Session Pill
                if (currentUser != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ManusSlate900)
                            .border(1.dp, SleekBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(currentUser.avatarColorHex)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser.username.take(1).uppercase(),
                                        color = ManusWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentUser.username,
                                            color = ManusWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(ManusIndigo.copy(alpha = 0.25f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = currentUser.role,
                                                color = ManusIndigoLight,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Sandbox: ${currentUser.homeDir}",
                                        color = ManusEmerald,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = ManusSlate400,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Tabs: Login / Sign Up / Switch User
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ManusSlate900)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(
                        AuthTab.LOGIN to "Log In",
                        AuthTab.SIGNUP to "Sign Up",
                        AuthTab.PROFILES to "Users"
                    ).forEach { (tab, label) ->
                        val isSelected = activeAuthTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) ManusIndigo else Color.Transparent)
                                .clickable {
                                    activeAuthTab = tab
                                    errorMessage = null
                                }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) ManusWhite else ManusSlate400,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (activeAuthTab) {
                    AuthTab.LOGIN -> {
                        Column {
                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = { usernameInput = it },
                                label = { Text("Username", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_login_username"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = ManusBorderLight,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite,
                                    focusedLabelColor = ManusIndigoLight,
                                    unfocusedLabelColor = ManusSlate400
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password", fontSize = 11.sp) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_login_password"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = ManusBorderLight,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite,
                                    focusedLabelColor = ManusIndigoLight,
                                    unfocusedLabelColor = ManusSlate400
                                )
                            )

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (usernameInput.isBlank()) {
                                        errorMessage = "Please enter username."
                                    } else {
                                        viewModel.login(usernameInput.trim(), passwordInput)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_login_submit"),
                                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Authenticate Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    AuthTab.SIGNUP -> {
                        Column {
                            OutlinedTextField(
                                value = usernameInput,
                                onValueChange = { usernameInput = it },
                                label = { Text("Username", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_username"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = ManusBorderLight,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite,
                                    focusedLabelColor = ManusIndigoLight,
                                    unfocusedLabelColor = ManusSlate400
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = ManusBorderLight,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite,
                                    focusedLabelColor = ManusIndigoLight,
                                    unfocusedLabelColor = ManusSlate400
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password (min 6 chars)", fontSize = 11.sp) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_password"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ManusIndigoLight,
                                    unfocusedBorderColor = ManusBorderLight,
                                    focusedTextColor = ManusWhite,
                                    unfocusedTextColor = ManusWhite,
                                    focusedLabelColor = ManusIndigoLight,
                                    unfocusedLabelColor = ManusSlate400
                                )
                            )

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (usernameInput.isBlank()) {
                                        errorMessage = "Please choose a username."
                                    } else {
                                        viewModel.signup(usernameInput.trim(), emailInput.trim(), passwordInput)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_signup_submit"),
                                colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Create Account & Provision VFS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(ManusBorderLight))
                                Text("OR SIGN UP WITH", color = ManusSlate400, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                Box(modifier = Modifier.weight(1f).height(1.dp).background(ManusBorderLight))
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Google Sign Up Button
                            Button(
                                onClick = {
                                    val targetEmail = if (emailInput.isNotBlank() && emailInput.contains("@")) emailInput.trim() else "user@gmail.com"
                                    val targetName = if (usernameInput.isNotBlank()) usernameInput.trim() else "Google User"
                                    viewModel.signupWithGoogle(targetEmail, targetName)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("google_signup_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F2937)),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF374151))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🌐", fontSize = 14.sp)
                                    Text("Sign Up with Google", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // GitHub Sign Up Button
                            Button(
                                onClick = {
                                    val targetGhUser = if (usernameInput.isNotBlank()) usernameInput.trim() else "developer"
                                    val targetEmail = if (emailInput.isNotBlank()) emailInput.trim() else "$targetGhUser@users.noreply.github.com"
                                    val targetName = if (usernameInput.isNotBlank()) usernameInput.trim() else "GitHub Developer"
                                    viewModel.signupWithGitHub(targetGhUser, targetEmail, targetName)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("github_signup_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24292E)),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4B5563))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🐙", fontSize = 14.sp)
                                    Text("Sign Up with GitHub", color = ManusWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    AuthTab.PROFILES -> {
                        val allUsers = viewModel.authManager.getAllUsers()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            items(allUsers) { u ->
                                val isCurrent = currentUser?.username == u.username
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isCurrent) ManusIndigo.copy(alpha = 0.15f) else ManusSlate900)
                                        .border(
                                            1.dp,
                                            if (isCurrent) ManusIndigo else SleekBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            viewModel.switchUser(u.username)
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(Color(u.avatarColorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = u.username.take(1).uppercase(),
                                                color = ManusWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = u.username,
                                                color = ManusWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "${u.role} • ${u.homeDir}",
                                                color = ManusSlate400,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    if (isCurrent) {
                                        Text(
                                            text = "Active",
                                            color = ManusEmerald,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            text = "Switch",
                                            color = ManusIndigoLight,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
