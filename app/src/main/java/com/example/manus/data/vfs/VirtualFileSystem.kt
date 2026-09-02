package com.example.manus.data.vfs

import com.example.manus.data.model.VirtualFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

class VirtualFileSystem {

    private val filesMap = ConcurrentHashMap<String, VirtualFile>()
    private val _fileListState = MutableStateFlow<List<VirtualFile>>(emptyList())
    val fileListState: StateFlow<List<VirtualFile>> = _fileListState.asStateFlow()

    init {
        loadDefaultWorkspace()
    }

    private fun loadDefaultWorkspace() {
        filesMap.clear()

        // Root workspace directory
        addDir("/workspace")
        addDir("/workspace/scripts")
        addDir("/workspace/data")
        addDir("/workspace/src")

        // 1. Interactive HTML5/JS/Canvas Cyber Game / Web Sandbox App
        val indexHtml = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>VirgoYT Cloud AI - Interactive Workspace</title>
  <link rel="stylesheet" href="styles.css">
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen font-sans flex flex-col items-center p-4">
  <div class="max-w-4xl w-full">
    <!-- Header -->
    <header class="flex justify-between items-center py-4 border-b border-slate-800">
      <div class="flex items-center gap-3">
        <div class="w-3 h-3 rounded-full bg-cyan-400 animate-ping"></div>
        <h1 class="text-xl font-bold tracking-tight text-cyan-400">⚡ VirgoYT Cloud AI Sandbox</h1>
      </div>
      <div class="flex gap-2">
        <span class="px-2.5 py-1 text-xs rounded-full bg-cyan-950 text-cyan-300 border border-cyan-800 font-mono">PORT 3000 // LIVE</span>
        <span class="px-2.5 py-1 text-xs rounded-full bg-slate-800 text-slate-300 font-mono" id="fpsCounter">60 FPS</span>
      </div>
    </header>

    <!-- Main Sandbox Workspace -->
    <main class="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
      <!-- Left: Interactive Controls -->
      <div class="bg-slate-900 border border-slate-800 rounded-xl p-5 shadow-lg flex flex-col gap-4">
        <h2 class="text-sm font-semibold uppercase tracking-wider text-slate-400">Sandbox Controls</h2>
        
        <div class="space-y-2">
          <label class="text-xs text-slate-400">Simulation Velocity</label>
          <input type="range" id="speedSlider" min="1" max="10" value="5" class="w-full accent-cyan-400 cursor-pointer">
        </div>

        <div class="space-y-2">
          <label class="text-xs text-slate-400">Particle Gravity Field</label>
          <div class="grid grid-cols-2 gap-2">
            <button onclick="setGravity('vortex')" class="px-3 py-2 text-xs rounded-lg bg-slate-800 hover:bg-cyan-900 hover:text-cyan-200 border border-slate-700 transition">Vortex Field</button>
            <button onclick="setGravity('orbit')" class="px-3 py-2 text-xs rounded-lg bg-slate-800 hover:bg-cyan-900 hover:text-cyan-200 border border-slate-700 transition">Orbit Mode</button>
            <button onclick="setGravity('repulse')" class="px-3 py-2 text-xs rounded-lg bg-slate-800 hover:bg-cyan-900 hover:text-cyan-200 border border-slate-700 transition">Repulsor</button>
            <button onclick="resetParticles()" class="px-3 py-2 text-xs rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white font-medium transition">Reset (500)</button>
          </div>
        </div>

        <div class="mt-4 p-3 bg-slate-950 rounded-lg border border-slate-800 font-mono text-xs text-slate-300 space-y-1">
          <div class="flex justify-between"><span class="text-slate-500">Node Status:</span> <span class="text-emerald-400">Active</span></div>
          <div class="flex justify-between"><span class="text-slate-500">Render Pipeline:</span> <span class="text-cyan-400">HTML5 2D Canvas</span></div>
          <div class="flex justify-between"><span class="text-slate-500">Live Memory:</span> <span id="memCounter">2.4 MB</span></div>
        </div>
      </div>

      <!-- Center & Right: Canvas Live Sandbox -->
      <div class="md:col-span-2 bg-slate-900 border border-slate-800 rounded-xl p-4 shadow-lg flex flex-col">
        <div class="flex justify-between items-center pb-3 mb-2 border-b border-slate-800">
          <span class="text-xs font-mono text-slate-400">CANVAS_SIMULATOR_V2.0</span>
          <button onclick="triggerBlast()" class="px-3 py-1 text-xs bg-amber-500/20 text-amber-300 border border-amber-500/40 rounded hover:bg-amber-500/30 transition">💥 Energy Blast</button>
        </div>
        <canvas id="simCanvas" class="w-full h-72 bg-slate-950 rounded-lg border border-slate-800 cursor-crosshair"></canvas>
      </div>
    </main>

    <!-- Activity Log -->
    <section class="mt-6 bg-slate-900 border border-slate-800 rounded-xl p-4">
      <h3 class="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-2">Terminal Event Bus</h3>
      <div id="eventLogs" class="font-mono text-xs space-y-1 max-h-24 overflow-y-auto text-slate-400">
        <div class="text-cyan-400">[SYSTEM] VirgoYT Cloud AI Sandbox initialized successfully.</div>
        <div class="text-emerald-400">[COMPILER] Web bundle compiled & hot-reloaded.</div>
      </div>
    </section>
  </div>

  <script src="app.js"></script>
</body>
</html>
""".trimIndent()

        val stylesCss = """
/* VirgoYT Cloud AI Sandbox Styles */
body {
  margin: 0;
  background-color: #070b14;
  color: #f1f5f9;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  user-select: none;
}

canvas {
  touch-action: none;
}

@keyframes pulseGlow {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 0.3; }
}

.glow-cyan {
  box-shadow: 0 0 15px rgba(6, 182, 212, 0.4);
}
""".trimIndent()

        val appJs = """
// Interactive Canvas Engine & Sandbox Simulation
const canvas = document.getElementById('simCanvas');
const ctx = canvas.getContext('2d');
let particles = [];
let mode = 'vortex';
let speedMultiplier = 1;
let mouse = { x: 0, y: 0, active: false };

function resizeCanvas() {
  canvas.width = canvas.clientWidth;
  canvas.height = canvas.clientHeight;
}
window.addEventListener('resize', resizeCanvas);
resizeCanvas();

class Particle {
  constructor(x, y) {
    this.x = x || Math.random() * canvas.width;
    this.y = y || Math.random() * canvas.height;
    this.vx = (Math.random() - 0.5) * 2;
    this.vy = (Math.random() - 0.5) * 2;
    this.radius = Math.random() * 2.5 + 1;
    this.hue = Math.random() * 60 + 170; // Cyan to Blue
  }

  update() {
    const cx = canvas.width / 2;
    const cy = canvas.height / 2;
    const dx = (mouse.active ? mouse.x : cx) - this.x;
    const dy = (mouse.active ? mouse.y : cy) - this.y;
    const dist = Math.sqrt(dx * dx + dy * dy) || 1;

    if (mode === 'vortex') {
      this.vx += (dx / dist) * 0.15 * speedMultiplier - (dy / dist) * 0.3;
      this.vy += (dy / dist) * 0.15 * speedMultiplier + (dx / dist) * 0.3;
    } else if (mode === 'repulse') {
      if (dist < 120) {
        this.vx -= (dx / dist) * 1.5;
        this.vy -= (dy / dist) * 1.5;
      }
    } else if (mode === 'orbit') {
      this.vx += (-dy / dist) * 0.4;
      this.vy += (dx / dist) * 0.4;
    }

    this.vx *= 0.96;
    this.vy *= 0.96;
    this.x += this.vx * speedMultiplier;
    this.y += this.vy * speedMultiplier;

    if (this.x < 0) this.x = canvas.width;
    if (this.x > canvas.width) this.x = 0;
    if (this.y < 0) this.y = canvas.height;
    if (this.y > canvas.height) this.y = 0;
  }

  draw() {
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2);
    ctx.fillStyle = `hsl(${'$'}{this.hue}, 90%, 60%)`;
    ctx.fill();
  }
}

function initParticles(count = 250) {
  particles = [];
  for (let i = 0; i < count; i++) {
    particles.push(new Particle());
  }
}
initParticles();

canvas.addEventListener('mousemove', (e) => {
  const rect = canvas.getBoundingClientRect();
  mouse.x = e.clientX - rect.left;
  mouse.y = e.clientY - rect.top;
  mouse.active = true;
});
canvas.addEventListener('mouseleave', () => { mouse.active = false; });
canvas.addEventListener('touchmove', (e) => {
  const rect = canvas.getBoundingClientRect();
  if (e.touches.length > 0) {
    mouse.x = e.touches[0].clientX - rect.left;
    mouse.y = e.touches[0].clientY - rect.top;
    mouse.active = true;
  }
});

window.setGravity = function(newMode) {
  mode = newMode;
  logEvent(`Mode changed to: ${'$'}{newMode}`);
};

window.resetParticles = function() {
  initParticles(350);
  logEvent('Particles re-seeded: 350 nodes');
};

window.triggerBlast = function() {
  particles.forEach(p => {
    p.vx = (Math.random() - 0.5) * 15;
    p.vy = (Math.random() - 0.5) * 15;
    p.hue = Math.random() * 60 + 20; // Amber explosion
  });
  logEvent('💥 Blast impulse applied');
};

document.getElementById('speedSlider')?.addEventListener('input', (e) => {
  speedMultiplier = e.target.value / 5;
});

function logEvent(msg) {
  const container = document.getElementById('eventLogs');
  if (!container) return;
  const div = document.createElement('div');
  div.textContent = `[${'$'}{new Date().toLocaleTimeString()}] ${'$'}{msg}`;
  container.appendChild(div);
  container.scrollTop = container.scrollHeight;
  console.log('[Sandbox]', msg);
}

function animate() {
  ctx.fillStyle = 'rgba(7, 11, 20, 0.25)';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  particles.forEach(p => {
    p.update();
    p.draw();
  });

  requestAnimationFrame(animate);
}
animate();
console.log('VirgoYT Cloud AI Sandbox application mounted.');
""".trimIndent()

        // 2. Python Data Analysis script
        val pythonScript = """
# /workspace/scripts/data_analyzer.py
# VirgoYT Cloud AI Computer - Python Data Science Engine

import sys
import math
import json

def analyze_dataset():
    print("========================================")
    print("  VIRGOYT CLOUD AI - DATA ANALYZER v2.4  ")
    print("========================================")
    
    data_points = [
        {"timestamp": "09:00", "latency_ms": 24, "throughput_mbps": 420.5, "errors": 0},
        {"timestamp": "10:00", "latency_ms": 19, "throughput_mbps": 512.8, "errors": 0},
        {"timestamp": "11:00", "latency_ms": 32, "throughput_mbps": 680.1, "errors": 1},
        {"timestamp": "12:00", "latency_ms": 45, "throughput_mbps": 890.4, "errors": 3},
        {"timestamp": "13:00", "latency_ms": 28, "throughput_mbps": 750.2, "errors": 0},
        {"timestamp": "14:00", "latency_ms": 21, "throughput_mbps": 620.0, "errors": 0},
        {"timestamp": "15:00", "latency_ms": 18, "throughput_mbps": 580.6, "errors": 0}
    ]

    latencies = [d["latency_ms"] for d in data_points]
    throughputs = [d["throughput_mbps"] for d in data_points]
    total_errors = sum(d["errors"] for d in data_points)

    avg_latency = sum(latencies) / len(latencies)
    avg_throughput = sum(throughputs) / len(throughputs)
    max_throughput = max(throughputs)
    min_latency = min(latencies)

    # Standard deviation calculation
    variance = sum((x - avg_latency) ** 2 for x in latencies) / len(latencies)
    std_dev = math.sqrt(variance)

    print(f"[*] Total Records Processed: {len(data_points)}")
    print(f"[*] Average Network Latency: {avg_latency:.2f} ms (std_dev: {std_dev:.2f})")
    print(f"[*] Peak Throughput:        {max_throughput:.1f} Mbps")
    print(f"[*] Average Throughput:     {avg_throughput:.1f} Mbps")
    print(f"[*] Total Anomaly Errors:   {total_errors}")
    print("----------------------------------------")
    print("[+] Regression Model Fitted: y = 14.2x + 380")
    print("[+] Status: HIGH_AVAILABILITY // PASSED")
    print("========================================")

if __name__ == "__main__":
    analyze_dataset()
""".trimIndent()

        // 3. JavaScript Benchmark Script
        val benchmarkJs = """
// /workspace/scripts/benchmark.js
// JavaScript Execution & Algorithm Benchmark

console.log("--> Starting VirgoYT Cloud AI JS Engine Benchmark...");
const startTime = Date.now();

// 1. Prime Sieve Benchmark
function sieve(limit) {
    const primes = [];
    const isPrime = new Uint8Array(limit + 1).fill(1);
    isPrime[0] = isPrime[1] = 0;
    for (let p = 2; p * p <= limit; p++) {
        if (isPrime[p]) {
            for (let i = p * p; i <= limit; i += p) isPrime[i] = 0;
        }
    }
    for (let p = 2; p <= limit; p++) {
        if (isPrime[p]) primes.push(p);
    }
    return primes;
}

const primes = sieve(200000);
const elapsed = Date.now() - startTime;

console.log(`[✓] Computed ${'$'}{primes.length} primes up to 200,000 in ${'$'}{elapsed}ms`);
console.log(`[✓] 10 Largest Primes: ${'$'}{primes.slice(-10).join(", ")}`);
console.log(`[✓] Memory Heap Allocation: 4.8MB`);
console.log(`[✓] Benchmark Score: 9,840 ops/sec`);
""".trimIndent()

        // 4. Sample C Code
        val mainC = """
// /workspace/main.c
#include <stdio.h>
#include <stdlib.h>

void bubble_sort(int arr[], int n) {
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) {
                int temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}

int main() {
    printf("[*] VirgoYT Cloud AI GCC 14.2 Compiler Test\n");
    int data[] = {64, 34, 25, 12, 22, 11, 90, 48, 88, 1};
    int n = sizeof(data) / sizeof(data[0]);

    printf("[*] Input array: ");
    for (int i = 0; i < n; i++) printf("%d ", data[i]);
    printf("\n");

    bubble_sort(data, n);

    printf("[+] Sorted array: ");
    for (int i = 0; i < n; i++) printf("%d ", data[i]);
    printf("\n[+] Binary execution finished with exit code 0.\n");
    return 0;
}
""".trimIndent()

        // 4b. Sample Modern C++23 Code
        val mainCpp = """
// /workspace/main.cpp - High-Performance Modern C++23
#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>
#include <chrono>

int main() {
    std::cout << "🚀 VirgoYT Cloud AI - C++23 High-Performance Engine\n";
    std::vector<int> numbers(100);
    std::iota(numbers.begin(), numbers.end(), 1);

    long long sum = 0;
    for (int n : numbers) sum += n * n;

    std::cout << "[+] Sum of squares (1..100): " << sum << std::endl;
    std::cout << "[✓] C++23 SIMD vectorized calculations completed in 0.042ms." << std::endl;
    return 0;
}
""".trimIndent()

        // 4c. Sample .NET 9.0 C# 13 Code
        val programCs = """
// /workspace/Program.cs - .NET 9.0 C# 13 High-Performance Microservice
using System;
using System.Linq;
using System.Threading.Tasks;

Console.WriteLine("⚡ VirgoYT Cloud AI - .NET 9.0 / C# 13 Runtime");
var squares = Enumerable.Range(1, 10).Select(x => $"{x}^2={x*x}");
Console.WriteLine($"[+] Computed Series: {string.Join(", ", squares)}");
Console.WriteLine("[✓] Task async pipeline dispatched: 0 allocations, 0ms latency.");
""".trimIndent()

        // 4d. Sample Rust Code
        val mainRs = """
// /workspace/main.rs - Rust 1.80 Zero-Cost Memory-Safe Engine
fn main() {
    println!("🦀 VirgoYT Cloud AI - Rust 1.80 Core Engine");
    let values: Vec<i32> = (1..=8).map(|x| x * 3).collect();
    println!("[+] Generated vector: {:?}", values);
    println!("[✓] Safe memory allocation checked: OK");
}
""".trimIndent()

        // 5. Shell Build Script
        val runSh = """
#!/bin/bash
# /workspace/run.sh
echo "[VIRGOYT CLOUD AI] Starting Isolated Sandbox Build Pipeline..."
echo "[1/4] Checking workspace dependencies..."
sleep 0.5
echo "[2/4] Compiling CSS bundle with Tailwind engine..."
sleep 0.5
echo "[3/4] Linting and bundling JavaScript components..."
sleep 0.5
echo "[4/4] Starting local HTTP server at http://localhost:3000..."
echo "✓ App live at http://localhost:3000"
""".trimIndent()

        // 6. package.json
        val packageJson = """
{
  "name": "virgoyt-cloud-workspace",
  "version": "1.0.0",
  "description": "VirgoYT Cloud AI Virtual Supercomputer Sandbox Environment",
  "main": "app.js",
  "scripts": {
    "dev": "node scripts/benchmark.js",
    "start": "bash run.sh",
    "test": "node scripts/test_runner.js"
  },
  "dependencies": {
    "chart.js": "^4.4.1",
    "tailwindcss": "^3.4.1"
  }
}
""".trimIndent()

        // 6b. test_runner.js
        val testRunnerJs = """
// /workspace/scripts/test_runner.js
// Automated Sandbox Unit & Integration Test Suite
console.log("--> Starting VirgoYT Cloud AI Automated Test Suite...");
const suites = [
  { name: "VFS Integrity Test", status: "PASS", durationMs: 4 },
  { name: "DOM Canvas Rendering Engine", status: "PASS", durationMs: 12 },
  { name: "Terminal Process IPC", status: "PASS", durationMs: 8 },
  { name: "Subtask Pipeline Safety Verifier", status: "PASS", durationMs: 6 }
];

suites.forEach(s => {
  console.log(`[✓] ${'$'}{s.name}: ${'$'}{s.status} (${'$'}{s.durationMs}ms)`);
});
console.log("════════════════════════════════════════════════");
console.log("Total: 4 passed, 0 failed, 0 skipped. All tests green!");
""".trimIndent()

        // 7. README.md
        val readmeMd = """
# VirgoYT Cloud AI Supercomputer & Virtual Sandbox 🚀

Welcome to your isolated Cloud AI environment! You have full access to:
- **VirgoYT Autonomous AI Agent**: Delegate complex coding and research tasks.
- **Linux Terminal**: Execute Python, Node.js, C compilers, and bash commands.
- **Code Editor**: Edit HTML, CSS, JavaScript, and Python files with live reload.
- **Isolated Browser**: Live preview your web apps in real-time with DevTools.

### Quick Commands:
- `python3 scripts/data_analyzer.py` - Run data science script
- `node scripts/benchmark.js` - Run prime sieve benchmark
- `npm run test` - Execute automated test runner
- `npm run start` - Compile and launch preview server
- `tree` - View full project tree
""".trimIndent()

        // 8. CSV Data
        val csvData = """
timestamp,region,cpu_usage,mem_mb,requests_per_sec,latency_p99
2026-08-31T09:00:00Z,us-west1,14.2,1420,12400,18.4
2026-08-31T10:00:00Z,us-west1,18.5,1580,16800,21.2
2026-08-31T11:00:00Z,us-west1,26.1,1920,24500,28.7
2026-08-31T12:00:00Z,us-west1,38.9,2410,38900,42.1
2026-08-31T13:00:00Z,us-west1,22.4,1800,20100,24.0
2026-08-31T14:00:00Z,us-west1,16.8,1540,14300,19.8
""".trimIndent()

        addFile("/workspace/index.html", indexHtml)
        addFile("/workspace/styles.css", stylesCss)
        addFile("/workspace/app.js", appJs)
        addFile("/workspace/scripts/data_analyzer.py", pythonScript)
        addFile("/workspace/scripts/benchmark.js", benchmarkJs)
        addFile("/workspace/scripts/test_runner.js", testRunnerJs)
        addFile("/workspace/main.cpp", mainCpp)
        addFile("/workspace/Program.cs", programCs)
        addFile("/workspace/main.rs", mainRs)
        addFile("/workspace/main.c", mainC)
        addFile("/workspace/run.sh", runSh)
        addFile("/workspace/package.json", packageJson)
        addFile("/workspace/README.md", readmeMd)
        addFile("/workspace/data/metrics.csv", csvData)
        addFile("/workspace/games/VirgoYT_Game.exe", "MZ\u0090\u0000[PE32+_WINDOWS_EXECUTABLE]\nTitle=VirgoYT Cyber Odyssey\nEngine=DirectX12\nFPS=120")
        addFile("/workspace/bin/VirgoApp.exe", "MZ\u0090\u0000[PE32+_WINDOWS_EXECUTABLE]\nTitle=VirgoYT Desktop Suite\nArchitecture=x86_64")

        // Seed Wine Drive C:\ virtual environment
        addDir("/workspace/drive_c")
        addDir("/workspace/drive_c/Program Files")
        addDir("/workspace/drive_c/Windows/System32")
        addFile("/workspace/drive_c/Windows/System32/kernel32.dll", "[SYSTEM_DLL_64BIT]")
        addFile("/workspace/drive_c/Windows/System32/d3d12.dll", "[DIRECT3D_12_DRIVER]")

        // Seed user home directories for session isolation
        addDir("/home")
        addDir("/home/developer")
        addDir("/home/admin")
        addDir("/home/guest")
        addFile("/home/developer/.bashrc", "export PATH=/workspace/bin:${'$'}PATH\nalias ll='ls -la'\n")
        addFile("/home/admin/admin_notes.txt", "# VirgoYT Cloud AI Admin Telemetry\n- Kernel: Ubuntu 24.04\n- Security: Sandboxed VFS\n")
        addFile("/home/guest/welcome.txt", "Welcome to VirgoYT Cloud AI Sandbox!\nYou are operating in an isolated guest session.\n")

        refreshList()
    }

    private fun addDir(path: String, owner: String = "developer") {
        val name = path.substringAfterLast('/').ifEmpty { "/" }
        filesMap[path] = VirtualFile(path = path, name = name, isDirectory = true, owner = owner)
    }

    fun addFile(path: String, content: String, owner: String = "developer") {
        val normalizedPath = if (path.startsWith("/")) path else "/workspace/$path"
        val name = normalizedPath.substringAfterLast('/')
        // Ensure parent directories exist
        val parentDir = normalizedPath.substringBeforeLast('/')
        if (parentDir.isNotEmpty() && !filesMap.containsKey(parentDir)) {
            addDir(parentDir, owner)
        }
        filesMap[normalizedPath] = VirtualFile(
            path = normalizedPath,
            name = name,
            isDirectory = false,
            content = content,
            owner = owner
        )
        refreshList()
    }

    fun readFile(path: String): String? {
        val normalized = if (path.startsWith("/")) path else "/workspace/$path"
        return filesMap[normalized]?.content
    }

    fun writeFile(path: String, content: String, owner: String = "developer"): Boolean {
        val normalized = if (path.startsWith("/")) path else "/workspace/$path"
        val name = normalized.substringAfterLast('/')
        val parentDir = normalized.substringBeforeLast('/')
        if (parentDir.isNotEmpty() && !filesMap.containsKey(parentDir)) {
            addDir(parentDir, owner)
        }
        filesMap[normalized] = VirtualFile(
            path = normalized,
            name = name,
            isDirectory = false,
            content = content,
            lastModified = System.currentTimeMillis(),
            owner = owner
        )
        refreshList()
        return true
    }

    fun deleteFile(path: String): Boolean {
        val normalized = if (path.startsWith("/")) path else "/workspace/$path"
        val removed = filesMap.remove(normalized) != null
        if (removed) {
            // Also remove child files if it was a directory
            val children = filesMap.keys.filter { it.startsWith("$normalized/") }
            children.forEach { filesMap.remove(it) }
            refreshList()
        }
        return removed
    }

    fun createDirectory(path: String, owner: String = "developer"): Boolean {
        val normalized = if (path.startsWith("/")) path else "/workspace/$path"
        addDir(normalized, owner)
        refreshList()
        return true
    }

    fun renameFile(sourcePath: String, newName: String): Boolean {
        val normalizedSrc = if (sourcePath.startsWith("/")) sourcePath else "/workspace/$sourcePath"
        val file = filesMap[normalizedSrc] ?: return false
        val parent = normalizedSrc.substringBeforeLast('/')
        val newPath = if (parent.isEmpty() || parent == "/") "/$newName" else "$parent/$newName"

        if (filesMap.containsKey(newPath)) return false

        filesMap.remove(normalizedSrc)
        filesMap[newPath] = file.copy(
            path = newPath,
            name = newName,
            lastModified = System.currentTimeMillis()
        )

        // If directory, update children paths
        if (file.isDirectory) {
            val children = filesMap.filter { it.key.startsWith("$normalizedSrc/") }
            children.forEach { (childKey, childFile) ->
                filesMap.remove(childKey)
                val updatedChildPath = childKey.replaceFirst(normalizedSrc, newPath)
                filesMap[updatedChildPath] = childFile.copy(path = updatedChildPath)
            }
        }

        refreshList()
        return true
    }

    fun copyFile(sourcePath: String, destPath: String, owner: String = "developer"): Boolean {
        val normSrc = if (sourcePath.startsWith("/")) sourcePath else "/workspace/$sourcePath"
        var normDest = if (destPath.startsWith("/")) destPath else "/workspace/$destPath"

        val srcFile = filesMap[normSrc] ?: return false

        // If dest is an existing directory, copy into it
        val destFile = filesMap[normDest]
        if (destFile != null && destFile.isDirectory) {
            normDest = "$normDest/${srcFile.name}"
        }

        if (srcFile.isDirectory) {
            addDir(normDest, owner)
            val children = filesMap.filter { it.key.startsWith("$normSrc/") }
            children.forEach { (childKey, child) ->
                val newChildPath = childKey.replaceFirst(normSrc, normDest)
                if (child.isDirectory) {
                    addDir(newChildPath, owner)
                } else {
                    filesMap[newChildPath] = child.copy(path = newChildPath, owner = owner)
                }
            }
        } else {
            filesMap[normDest] = srcFile.copy(
                path = normDest,
                name = normDest.substringAfterLast('/'),
                lastModified = System.currentTimeMillis(),
                owner = owner
            )
        }

        refreshList()
        return true
    }

    fun moveFile(sourcePath: String, destPath: String): Boolean {
        val normSrc = if (sourcePath.startsWith("/")) sourcePath else "/workspace/$sourcePath"
        var normDest = if (destPath.startsWith("/")) destPath else "/workspace/$destPath"

        val srcFile = filesMap[normSrc] ?: return false

        val destFile = filesMap[normDest]
        if (destFile != null && destFile.isDirectory) {
            normDest = "$normDest/${srcFile.name}"
        }

        if (normSrc == normDest) return false

        val copied = copyFile(normSrc, normDest, srcFile.owner)
        if (copied) {
            deleteFile(normSrc)
        }
        return copied
    }

    fun getFile(path: String): VirtualFile? {
        val normalized = if (path.startsWith("/")) path else "/workspace/$path"
        return filesMap[normalized]
    }

    fun listFilesInDir(dirPath: String): List<VirtualFile> {
        val normalized = if (dirPath.startsWith("/")) dirPath else "/workspace/$dirPath"
        return filesMap.values.filter { file ->
            val parent = file.path.substringBeforeLast('/')
            parent == normalized && file.path != normalized
        }.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.name })
    }

    fun getAllFiles(): List<VirtualFile> {
        return filesMap.values.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.path })
    }

    fun getUserFiles(username: String): List<VirtualFile> {
        val userHome = "/home/$username"
        return filesMap.values.filter {
            it.path.startsWith("/workspace") || it.path.startsWith(userHome)
        }.sortedWith(compareBy<VirtualFile> { !it.isDirectory }.thenBy { it.path })
    }

    private fun refreshList() {
        _fileListState.value = getAllFiles()
    }

    fun resetToDefaults() {
        loadDefaultWorkspace()
    }

    /**
     * Bundles index.html, styles.css, and app.js into a single robust offline HTML bundle
     * that can be rendered directly into the Android WebView Sandbox.
     */
    fun getBundledWebPreviewHtml(): String {
        val rawHtml = readFile("/workspace/index.html") ?: "<html><body><h1>Empty App</h1></body></html>"
        val rawCss = readFile("/workspace/styles.css") ?: ""
        val rawJs = readFile("/workspace/app.js") ?: ""

        // Inject styles and script inline if not already present
        var bundled = rawHtml
        if (rawCss.isNotBlank()) {
            bundled = if (bundled.contains("</head>", ignoreCase = true)) {
                bundled.replace("</head>", "<style>\n$rawCss\n</style>\n</head>", ignoreCase = true)
            } else {
                "<style>\n$rawCss\n</style>\n$bundled"
            }
        }

        if (rawJs.isNotBlank()) {
            val scriptTag = """
<script>
window.addEventListener('DOMContentLoaded', () => {
    try {
        $rawJs
    } catch(e) {
        console.error('Runtime error in app.js:', e);
    }
});
</script>
""".trimIndent()
            bundled = if (bundled.contains("</body>", ignoreCase = true)) {
                bundled.replace("</body>", "$scriptTag\n</body>", ignoreCase = true)
            } else {
                "$bundled\n$scriptTag"
            }
        }

        return bundled
    }
}
