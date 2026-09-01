package com.example.manus.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.manus.data.model.BrowserConsoleMessage
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusGreen
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoBg
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusIndigoSoft
import com.example.ui.theme.ManusPurple
import com.example.ui.theme.ManusRed
import com.example.ui.theme.ManusSlate200
import com.example.ui.theme.ManusSlate400
import com.example.ui.theme.ManusSlate500
import com.example.ui.theme.ManusSlate600
import com.example.ui.theme.ManusSlate700
import com.example.ui.theme.ManusSlate800
import com.example.ui.theme.ManusSlate850
import com.example.ui.theme.ManusSlate900
import com.example.ui.theme.ManusSlate950
import com.example.ui.theme.ManusWhite
import com.example.ui.theme.SleekBorder
import com.example.ui.theme.SleekSurface
import com.example.ui.theme.TermBg
import com.example.ui.theme.TermText

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserSandboxView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    val reloadTrigger by viewModel.browserReloadTrigger.collectAsState()
    val consoleLogs by viewModel.browserConsoleLogs.collectAsState()

    var showDevTools by remember { mutableStateOf(false) }
    var activeDevTab by remember { mutableStateOf("console") }
    var isMobileViewport by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var urlText by remember { mutableStateOf("http://localhost:3000/") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
    ) {
        // Sleek Browser Navigation Bar (Address bar + controls)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ManusSlate900)
                .border(1.dp, SleekBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Navigation buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = { webViewRef?.goBack() },
                        enabled = webViewRef?.canGoBack() == true,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (webViewRef?.canGoBack() == true) ManusSlate200 else ManusSlate600,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(
                        onClick = { webViewRef?.goForward() },
                        enabled = webViewRef?.canGoForward() == true,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Forward",
                            tint = if (webViewRef?.canGoForward() == true) ManusSlate200 else ManusSlate600,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.reloadBrowserSandbox()
                        },
                        modifier = Modifier.size(26.dp).testTag("browser_reload_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload Sandbox",
                            tint = ManusIndigoLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Address Bar in Sleek Obsidian styling
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ManusSlate950)
                        .border(1.dp, SleekBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Secure Sandbox",
                            tint = ManusEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = urlText,
                            color = ManusSlate200,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManusEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Text(
                                    text = "🛡️ STEALTH PACING (ANTI-BLOCK ACTIVE)",
                                    color = ManusEmerald,
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                    }
                }

                // Viewport toggle & DevTools toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { isMobileViewport = !isMobileViewport },
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = if (isMobileViewport) Icons.Default.PhoneAndroid else Icons.Default.Laptop,
                            contentDescription = "Toggle Viewport",
                            tint = if (isMobileViewport) ManusAmber else ManusSlate400,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (showDevTools) ManusIndigoBg else SleekSurface)
                            .border(1.dp, if (showDevTools) ManusIndigo.copy(alpha = 0.4f) else SleekBorder, RoundedCornerShape(6.dp))
                            .clickable { showDevTools = !showDevTools }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("toggle_devtools_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "DevTools",
                                tint = if (showDevTools) ManusIndigoLight else ManusSlate400,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "DevTools",
                                color = if (showDevTools) ManusIndigoLight else ManusSlate400,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Live WebView Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (showDevTools) 0.6f else 1f)
                .background(Color(0xFF070B14))
                .border(1.dp, SleekBorder),
            contentAlignment = Alignment.Center
        ) {
            val previewHtml = remember(reloadTrigger) {
                viewModel.vfs.getBundledWebPreviewHtml()
            }

            Box(
                modifier = if (isMobileViewport) {
                    Modifier
                        .width(360.dp)
                        .fillMaxSize()
                        .border(2.dp, ManusIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                } else {
                    Modifier.fillMaxSize()
                }
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.allowFileAccess = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true

                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    if (consoleMessage != null) {
                                        viewModel.addBrowserConsoleLog(
                                            level = consoleMessage.messageLevel().name,
                                            message = consoleMessage.message() ?: "",
                                            source = consoleMessage.sourceId(),
                                            lineNumber = consoleMessage.lineNumber()
                                        )
                                    }
                                    return super.onConsoleMessage(consoleMessage)
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                }
                            }

                            webViewRef = this
                            loadDataWithBaseURL(
                                "http://localhost:3000/",
                                previewHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    update = { view ->
                        webViewRef = view
                        view.loadDataWithBaseURL(
                            "http://localhost:3000/",
                            previewHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Live DevTools Drawer Panel (Console logs & DOM viewer)
        AnimatedVisibility(
            visible = showDevTools,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(TermBg)
                    .border(1.dp, SleekBorder)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // DevTools Tab Bar
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            DevTabButton(
                                label = "Console (${consoleLogs.size})",
                                isSelected = activeDevTab == "console",
                                onClick = { activeDevTab = "console" }
                            )
                            DevTabButton(
                                label = "DOM Elements",
                                isSelected = activeDevTab == "dom",
                                onClick = { activeDevTab = "dom" }
                            )
                        }

                        if (activeDevTab == "console" && consoleLogs.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearBrowserConsole() },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = "Clear Console",
                                    tint = ManusSlate400,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }

                    // DevTools Content
                    if (activeDevTab == "console") {
                        if (consoleLogs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No console events logged yet. (All console.log statements will stream here live)",
                                    color = ManusSlate600,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                items(consoleLogs) { log ->
                                    ConsoleLogItem(log = log)
                                }
                            }
                        }
                    } else {
                        // DOM Inspector tab
                        val rawHtml = viewModel.vfs.readFile("/workspace/index.html") ?: ""
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(rawHtml.lines()) { line ->
                                Text(
                                    text = line,
                                    color = if (line.trim().startsWith("<") && line.trim().endsWith(">")) ManusIndigoLight else TermText,
                                    fontSize = 9.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevTabButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) ManusIndigoBg else SleekSurface)
            .border(1.dp, if (isSelected) ManusIndigo.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) ManusIndigoLight else ManusSlate400,
            fontSize = 9.5.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun ConsoleLogItem(log: BrowserConsoleMessage) {
    val color = when (log.level.uppercase()) {
        "ERROR" -> ManusRed
        "WARN", "WARNING" -> ManusAmber
        "INFO" -> ManusIndigoLight
        else -> TermText
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[${log.level}] ${log.message}",
            color = color,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 14.sp,
            modifier = Modifier.weight(1f)
        )
        if (log.source != null) {
            Text(
                text = "${log.source.substringAfterLast('/')}:${log.lineNumber}",
                color = ManusSlate600,
                fontSize = 8.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
