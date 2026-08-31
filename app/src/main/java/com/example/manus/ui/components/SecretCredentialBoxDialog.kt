package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate300
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder

@Composable
fun SecretCredentialBoxDialog(viewModel: ManusCloudViewModel) {
    val secretPrompt by viewModel.secretPromptState.collectAsState()

    if (secretPrompt == null) return

    val prompt = secretPrompt!!

    var credentialUser by remember { mutableStateOf("") }
    var credentialPass by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { viewModel.closeSecretBox() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, SleekBorder, RoundedCornerShape(16.dp)),
            color = ManusSlate950
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ManusIndigoBg)
                                .border(1.dp, SleekBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Secret Vault",
                                tint = ManusCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Secret Credential Vault",
                                color = ManusWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Service: ${prompt.serviceName}",
                                color = ManusIndigoLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.closeSecretBox() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ManusSlate400
                        )
                    }
                }

                // Reason info
                Card(
                    colors = CardDefaults.cardColors(containerColor = ManusSlate900),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Authentication Required by VirgoYT AI:",
                            color = ManusSlate300,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = prompt.promptReason,
                            color = ManusSlate400,
                            fontSize = 12.sp
                        )
                    }
                }

                // Credential Form
                OutlinedTextField(
                    value = credentialUser,
                    onValueChange = { credentialUser = it },
                    label = { Text("Account Email / Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ManusCyan,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = ManusWhite,
                        unfocusedTextColor = ManusWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("secret_email_input")
                )

                OutlinedTextField(
                    value = credentialPass,
                    onValueChange = { credentialPass = it },
                    label = { Text("Password / Secure App Token") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = ManusSlate400
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ManusCyan,
                        unfocusedBorderColor = SleekBorder,
                        focusedTextColor = ManusWhite,
                        unfocusedTextColor = ManusWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("secret_password_input")
                )

                Text(
                    text = "🔒 Passwords and tokens are stored in isolated encrypted RAM memory and never written to disk logs.",
                    color = ManusSlate400,
                    fontSize = 10.5.sp
                )

                // Action Buttons: [ Save to Vault | Direct Remote Control Sign In ]
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.submitSecretCredentials(
                                prompt.serviceName,
                                credentialUser.ifBlank { "developer@virgoyt.cloud" },
                                credentialPass.ifBlank { "token_auth_verified" }
                            )
                            viewModel.showToast("✓ Credentials saved securely in Secret Vault")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_secret_btn")
                    ) {
                        Text(
                            text = "Save Credentials & Proceed",
                            color = ManusWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Direct Remote Desktop Control Option
                    Button(
                        onClick = {
                            viewModel.closeSecretBox()
                            viewModel.selectTab(ActiveWorkspaceTab.LIVE_COMPUTER)
                            viewModel.showToast("🎮 Remote Desktop Takeover Activated: Sign in directly on the Cloud PC screen")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ManusSlate850),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "Direct Control",
                                tint = ManusCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Take Remote Desktop Control to Sign In",
                                color = ManusCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
