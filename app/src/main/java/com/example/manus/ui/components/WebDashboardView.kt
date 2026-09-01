package com.example.manus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manus.data.model.ActiveWorkspaceTab
import com.example.manus.ui.ManusCloudViewModel
import com.example.ui.theme.ManusAmber
import com.example.ui.theme.ManusCyan
import com.example.ui.theme.ManusEmerald
import com.example.ui.theme.ManusIndigo
import com.example.ui.theme.ManusIndigoLight
import com.example.ui.theme.ManusPurple
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
import com.example.ui.theme.VirgoCyanGlow
import com.example.ui.theme.VirgoGlassCard
import com.example.ui.theme.VirgoNeonViolet

enum class WebViewportMode(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DESKTOP("Desktop 1920x1080", Icons.Default.Computer),
    TABLET("Tablet 768x1024", Icons.Default.Tablet),
    MOBILE("Mobile 375x812", Icons.Default.PhoneAndroid)
}

@Composable
fun WebDashboardView(
    viewModel: ManusCloudViewModel,
    modifier: Modifier = Modifier
) {
    var viewportMode by remember { mutableStateOf(WebViewportMode.DESKTOP) }
    var isCodeView by remember { mutableStateOf(false) }

    val systemStats by viewModel.systemStats.collectAsState()
    val activeSession by viewModel.currentSession.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ManusSlate950)
            .padding(12.dp)
    ) {
        // Web Dashboard Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VirgoGlassCard)
                .border(1.dp, SleekBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Web Dashboard",
                    tint = VirgoCyanGlow,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "VirgoYT Next.js Web Application",
                        color = ManusWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Live Server: http://localhost:3000 • React 19 + Tailwind CSS",
                        color = ManusSlate400,
                        fontSize = 10.sp
                    )
                }
            }

            // Viewport Switcher Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WebViewportMode.values().forEach { mode ->
                    val isSelected = viewportMode == mode
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) VirgoCyanGlow.copy(alpha = 0.25f) else ManusSlate850)
                            .border(1.dp, if (isSelected) VirgoCyanGlow else SleekBorder, RoundedCornerShape(8.dp))
                            .clickable { viewportMode = mode },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = mode.icon,
                            contentDescription = mode.label,
                            tint = if (isSelected) VirgoCyanGlow else ManusSlate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Toggle TSX Code / Live Preview
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCodeView) VirgoNeonViolet.copy(alpha = 0.25f) else ManusSlate850)
                        .border(1.dp, if (isCodeView) VirgoNeonViolet else SleekBorder, RoundedCornerShape(8.dp))
                        .clickable { isCodeView = !isCodeView }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = "Code",
                            tint = if (isCodeView) VirgoNeonViolet else ManusSlate400,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isCodeView) "Preview" else "Source",
                            color = if (isCodeView) VirgoNeonViolet else ManusSlate300,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isCodeView) {
            // Next.js TSX Code View
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ManusSlate900)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    item {
                        Text(
                            text = """// app/page.tsx - VirgoYT Advanced AI Assistant Web Dashboard
'use client';

import React, { useState, useEffect } from 'react';
import { Bot, Mic, Database, Cpu, HardDrive, Terminal, Shield, Zap } from 'lucide-react';

export default function VirgoWebDashboard() {
  const [activeTab, setActiveTab] = useState('chat');
  const [model, setModel] = useState('BazaarLink AI (v1)');
  const [streamingResponse, setStreamingResponse] = useState('');
  
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans">
      {/* Holographic Top Navigation */}
      <header className="h-14 border-b border-cyan-500/20 bg-slate-900/60 backdrop-blur-xl px-6 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-lg bg-gradient-to-tr from-cyan-500 to-indigo-600 flex items-center justify-center font-bold text-white shadow-lg shadow-cyan-500/20">
            V
          </div>
          <span className="font-bold tracking-wide text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-indigo-300">
            VirgoYT AI Cloud Assistant
          </span>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs px-2.5 py-1 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/30">
            ● AI Gateway Online
          </span>
        </div>
      </header>

      {/* Main Glassmorphic Grid */}
      <main className="flex-1 grid grid-cols-12 gap-4 p-6">
        {/* Left Side: Autonomous AI Swarm Matrix */}
        <div className="col-span-8 bg-slate-900/40 border border-slate-800 rounded-2xl p-6 backdrop-blur-md">
          <h2 className="text-lg font-semibold text-cyan-300 mb-4 flex items-center gap-2">
            <Zap className="w-5 h-5" /> Real-time Streaming AI Assistant
          </h2>
          {/* Chat & Prompt Stream Area */}
        </div>

        {/* Right Side: Telemetry & Memory RAG */}
        <div className="col-span-4 space-y-4">
          <div className="bg-slate-900/40 border border-slate-800 rounded-2xl p-4">
            <h3 className="text-sm font-semibold text-slate-300 mb-3 flex items-center gap-2">
              <Database className="w-4 h-4 text-indigo-400" /> 1536-D Vector Memory Hive
            </h3>
          </div>
        </div>
      </main>
    </div>
  );
}
""".trimIndent(),
                            color = ManusSlate200,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        } else {
            // Live Interactive Next.js Web App Emulation Frame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SleekBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030712))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    // Browser Frame Chrome Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ManusSlate900)
                            .border(1.dp, SleekBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF10B981)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🔒 https://virgoyt.ai/dashboard",
                                color = ManusSlate400,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = "Next.js 15.0 App Router",
                            color = VirgoCyanGlow,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Simulated Web UI Layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Web Left Column: Assistant Playground
                        Card(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚡ VirgoYT AI Real-Time Agent",
                                    color = VirgoCyanGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Connected to BazaarLink & Multi-Agent Swarm. Ready to execute code, terminal commands, database migrations, and 3D scenes.",
                                    color = ManusSlate300,
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                // Interactive Web Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.selectTab(ActiveWorkspaceTab.APP_GEN)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ManusIndigo),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("App Studio", fontSize = 10.sp, color = ManusWhite)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.selectTab(ActiveWorkspaceTab.PLUGINS_TOOLS)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ManusSlate800),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).height(32.dp)
                                    ) {
                                        Text("Plugins Hub", fontSize = 10.sp, color = ManusSlate200)
                                    }
                                }
                            }
                        }

                        // Web Right Column: Telemetry & Memory
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SleekBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ManusSlate900.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📊 Cloud Telemetry",
                                    color = ManusEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                MetricWebRow("CPU Load", "${systemStats.cpuUsagePercent}%")
                                MetricWebRow("GPU A100", "${systemStats.gpuUsagePercent}%")
                                MetricWebRow("RAM Used", "${systemStats.memoryUsedMb} MB")
                                MetricWebRow("Uptime", "${systemStats.uptimeSeconds}s")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricWebRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = ManusSlate400, fontSize = 10.sp)
        Text(text = value, color = ManusWhite, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}
