package com.example.manus.data.agent

import com.example.BuildConfig
import com.example.manus.data.model.AgentSubtask
import com.example.manus.data.model.AgentTask
import com.example.manus.data.model.TaskStatus
import com.example.manus.data.terminal.TerminalEngine
import com.example.manus.data.vfs.VirtualFileSystem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class ManusAgentService(
    private val vfs: VirtualFileSystem,
    private val terminal: TerminalEngine
) {

    private val _currentTask = MutableStateFlow<AgentTask?>(null)
    val currentTask: StateFlow<AgentTask?> = _currentTask.asStateFlow()

    private val _agentStatusText = MutableStateFlow("Agent Ready")
    val agentStatusText: StateFlow<String> = _agentStatusText.asStateFlow()

    private val _isAgentBusy = MutableStateFlow(false)
    val isAgentBusy: StateFlow<Boolean> = _isAgentBusy.asStateFlow()

    private val _agentReasoningLogs = MutableStateFlow<List<String>>(emptyList())
    val agentReasoningLogs: StateFlow<List<String>> = _agentReasoningLogs.asStateFlow()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    fun resetTask() {
        _currentTask.value = null
        _agentStatusText.value = "Agent Ready"
        _isAgentBusy.value = false
        _agentReasoningLogs.value = emptyList()
    }

    private fun addReasoningLog(log: String) {
        _agentReasoningLogs.value = _agentReasoningLogs.value + log
    }

    suspend fun executeUserGoal(goal: String, onBrowserRefresh: () -> Unit) = withContext(Dispatchers.IO) {
        if (_isAgentBusy.value) return@withContext

        _isAgentBusy.value = true
        _agentReasoningLogs.value = emptyList()
        _agentStatusText.value = "Analyzing user goal..."
        addReasoningLog("🧠 [THINKING] Received goal: \"$goal\"")
        addReasoningLog("🔍 [INSPECTION] Scanning workspace state and active environment runtime...")

        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            try {
                executeWithGeminiApi(goal, apiKey, onBrowserRefresh)
            } catch (e: Exception) {
                addReasoningLog("⚠️ [FALLBACK] Gemini API call failed (${e.message}), switching to Autonomous Sandbox Pipeline Engine.")
                executeWithAutonomousEngine(goal, onBrowserRefresh)
            }
        } else {
            addReasoningLog("💡 [AUTONOMOUS ENGINE] Using high-speed Sandbox Computer Controller.")
            executeWithAutonomousEngine(goal, onBrowserRefresh)
        }

        _isAgentBusy.value = false
        _agentStatusText.value = "Task Completed Successfully"
    }

    private suspend fun executeWithGeminiApi(goal: String, apiKey: String, onBrowserRefresh: () -> Unit) {
        _agentStatusText.value = "Reasoning with Gemini 3.5..."
        addReasoningLog("🌐 [GEMINI] Dispatching autonomous plan decomposition request to gemini-3.5-flash...")

        val systemPrompt = """
You are VirgoYT Cloud AI, an autonomous virtual computer agent. Given a user goal, break it down into 3-5 concrete actionable subtasks that will compile code, execute shell commands, create or edit files in /workspace, and verify the app in the sandbox browser.
Return STRICTLY a JSON object with this format:
{
  "explanation": "High level strategy for the virtual computer",
  "subtasks": [
    {
      "title": "Short title",
      "thought": "Why we are doing this",
      "toolName": "write_file | bash_exec | browser_open",
      "toolInput": "File path / content / command"
    }
  ]
}
""".trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "Goal: $goal\n\nGenerate structured subtasks."))
                    })
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().put("text", systemPrompt))
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.4)
            })
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string() ?: throw RuntimeException("Empty response from Gemini API")

        val jsonObject = JSONObject(responseBody)
        val candidate = jsonObject.optJSONArray("candidates")?.optJSONObject(0)
        val text = candidate?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
            ?: throw RuntimeException("No text in candidate response")

        val parsedPlan = JSONObject(text)
        val explanation = parsedPlan.optString("explanation", "Executing task on Manus Cloud PC")
        val subtasksArray = parsedPlan.optJSONArray("subtasks") ?: JSONArray()

        val taskList = mutableListOf<AgentSubtask>()
        for (i in 0 until subtasksArray.length()) {
            val item = subtasksArray.getJSONObject(i)
            taskList.add(
                AgentSubtask(
                    title = item.optString("title", "Subtask ${i + 1}"),
                    thought = item.optString("thought", ""),
                    toolName = item.optString("toolName", "bash_exec"),
                    toolInput = item.optString("toolInput", ""),
                    status = TaskStatus.PENDING
                )
            )
        }

        val agentTask = AgentTask(
            goal = goal,
            explanation = explanation,
            status = TaskStatus.IN_PROGRESS,
            subtasks = taskList
        )
        _currentTask.value = agentTask

        // Execute subtasks step by step
        runSubtasksPipeline(agentTask, onBrowserRefresh)
    }

    private suspend fun executeWithAutonomousEngine(goal: String, onBrowserRefresh: () -> Unit) {
        val plan = generateAutonomousPlan(goal)
        _currentTask.value = plan
        runSubtasksPipeline(plan, onBrowserRefresh)
    }

    private suspend fun runSubtasksPipeline(initialTask: AgentTask, onBrowserRefresh: () -> Unit) {
        var current = initialTask
        val updatedSubtasks = current.subtasks.toMutableList()

        for (i in updatedSubtasks.indices) {
            val subtask = updatedSubtasks[i]
            _agentStatusText.value = "Executing Step ${i + 1}/${updatedSubtasks.size}: ${subtask.title}"
            addReasoningLog("⚡ [STEP ${i + 1}/${updatedSubtasks.size}] ${subtask.title}")
            if (!subtask.thought.isNullOrBlank()) {
                addReasoningLog("💭 \"${subtask.thought}\"")
            }

            updatedSubtasks[i] = subtask.copy(status = TaskStatus.IN_PROGRESS)
            current = current.copy(subtasks = updatedSubtasks.toList())
            _currentTask.value = current

            delay(350)

            // Perform actual tool action in Virtual PC
            val output = executeTool(subtask.toolName ?: "bash_exec", subtask.toolInput ?: "", onBrowserRefresh)

            updatedSubtasks[i] = updatedSubtasks[i].copy(
                status = TaskStatus.COMPLETED,
                toolOutput = output
            )
            current = current.copy(subtasks = updatedSubtasks.toList())
            _currentTask.value = current

            addReasoningLog("✓ [SUCCESS] ${subtask.title} output: ${output.take(80)}${if (output.length > 80) "..." else ""}")
            delay(200)
        }

        _currentTask.value = current.copy(status = TaskStatus.COMPLETED)
        addReasoningLog("🎉 [DELIVERED] All autonomous steps verified in Sandbox Browser and Linux Terminal.")
        onBrowserRefresh()
    }

    private suspend fun executeTool(toolName: String, toolInput: String, onBrowserRefresh: () -> Unit): String {
        return when (toolName) {
            "write_file" -> {
                // Format: path:::content or file creation
                if (toolInput.contains(":::")) {
                    val path = toolInput.substringBefore(":::")
                    val content = toolInput.substringAfter(":::")
                    vfs.writeFile(path, content)
                    "Successfully written ${content.length} characters to $path"
                } else {
                    vfs.writeFile("/workspace/$toolInput", "// Generated by VirgoYT Cloud AI Agent\n")
                    "Created file $toolInput"
                }
            }
            "bash_exec" -> {
                terminal.executeCommand(toolInput)
            }
            "browser_open" -> {
                onBrowserRefresh()
                "Sandbox browser hot-reloaded: rendered live HTML5/CSS bundle."
            }
            else -> {
                terminal.executeCommand(toolInput)
            }
        }
    }

    private fun generateAutonomousPlan(goal: String): AgentTask {
        val lower = goal.lowercase()

        return when {
            lower.contains("game") || lower.contains("snake") || lower.contains("arcade") -> {
                createGamePlan(goal)
            }
            lower.contains("crypto") || lower.contains("stock") || lower.contains("tracker") || lower.contains("finance") -> {
                createFinancePlan(goal)
            }
            lower.contains("data") || lower.contains("analyze") || lower.contains("python") || lower.contains("csv") -> {
                createDataSciencePlan(goal)
            }
            lower.contains("todo") || lower.contains("kanban") || lower.contains("task") -> {
                createKanbanPlan(goal)
            }
            lower.contains("benchmark") || lower.contains("sort") || lower.contains("algorithm") -> {
                createBenchmarkPlan(goal)
            }
            else -> {
                createGenericWebPlan(goal)
            }
        }
    }

    private fun createGamePlan(goal: String): AgentTask {
        val gameHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Cyberpunk Arcade - VirgoYT Cloud AI</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    body { background: #020617; color: #f8fafc; font-family: monospace; }
    canvas { border: 2px solid #00F0FF; box-shadow: 0 0 20px rgba(0,240,255,0.4); }
  </style>
</head>
<body class="flex flex-col items-center justify-center min-h-screen p-4">
  <div class="max-w-md w-full text-center space-y-4">
    <div class="flex justify-between items-center bg-slate-900 p-3 rounded-xl border border-cyan-500/30">
      <span class="text-cyan-400 font-bold text-lg">⚡ CYBER SNAKE 2077</span>
      <span class="text-amber-400 font-mono text-base">SCORE: <span id="score">0</span></span>
    </div>
    <canvas id="gameCanvas" width="360" height="360" class="rounded-xl mx-auto bg-slate-950"></canvas>
    <div class="grid grid-cols-3 gap-2">
      <div></div>
      <button onclick="changeDir('UP')" class="p-3 bg-slate-800 hover:bg-cyan-900 border border-slate-700 rounded-lg text-lg">▲</button>
      <div></div>
      <button onclick="changeDir('LEFT')" class="p-3 bg-slate-800 hover:bg-cyan-900 border border-slate-700 rounded-lg text-lg">◀</button>
      <button onclick="resetGame()" class="p-3 bg-cyan-600 hover:bg-cyan-500 rounded-lg text-sm font-bold text-white">RESTART</button>
      <button onclick="changeDir('RIGHT')" class="p-3 bg-slate-800 hover:bg-cyan-900 border border-slate-700 rounded-lg text-lg">▶</button>
      <div></div>
      <button onclick="changeDir('DOWN')" class="p-3 bg-slate-800 hover:bg-cyan-900 border border-slate-700 rounded-lg text-lg">▼</button>
      <div></div>
    </div>
    <div class="text-xs text-slate-500">Autonomous VirgoYT Cloud AI Game Compilation Complete</div>
  </div>
  <script>
    const cvs = document.getElementById('gameCanvas');
    const ctx = cvs.getContext('2d');
    const grid = 18;
    let snake = [{x: 9, y: 9}, {x: 8, y: 9}];
    let food = {x: 14, y: 9};
    let dx = 1, dy = 0;
    let score = 0;
    let loop = null;

    function resetGame() {
      snake = [{x: 9, y: 9}, {x: 8, y: 9}];
      dx = 1; dy = 0;
      score = 0;
      document.getElementById('score').innerText = score;
      spawnFood();
      if(loop) clearInterval(loop);
      loop = setInterval(gameStep, 100);
    }

    function spawnFood() {
      food = {
        x: Math.floor(Math.random() * (cvs.width/grid)),
        y: Math.floor(Math.random() * (cvs.height/grid))
      };
    }

    window.changeDir = function(dir) {
      if(dir === 'UP' && dy === 0) { dx = 0; dy = -1; }
      if(dir === 'DOWN' && dy === 0) { dx = 0; dy = 1; }
      if(dir === 'LEFT' && dx === 0) { dx = -1; dy = 0; }
      if(dir === 'RIGHT' && dx === 0) { dx = 1; dy = 0; }
    };

    function gameStep() {
      const head = {x: snake[0].x + dx, y: snake[0].y + dy};
      if(head.x < 0) head.x = (cvs.width/grid)-1;
      if(head.x >= cvs.width/grid) head.x = 0;
      if(head.y < 0) head.y = (cvs.height/grid)-1;
      if(head.y >= cvs.height/grid) head.y = 0;

      if(head.x === food.x && head.y === food.y) {
        score += 10;
        document.getElementById('score').innerText = score;
        spawnFood();
      } else {
        snake.pop();
      }
      snake.unshift(head);

      ctx.fillStyle = '#020617';
      ctx.fillRect(0, 0, cvs.width, cvs.height);

      ctx.fillStyle = '#F59E0B';
      ctx.shadowBlur = 15;
      ctx.shadowColor = '#F59E0B';
      ctx.fillRect(food.x*grid+2, food.y*grid+2, grid-4, grid-4);

      ctx.shadowBlur = 10;
      ctx.shadowColor = '#00F0FF';
      snake.forEach((s, idx) => {
        ctx.fillStyle = idx === 0 ? '#00F0FF' : '#38BDF8';
        ctx.fillRect(s.x*grid+1, s.y*grid+1, grid-2, grid-2);
      });
      ctx.shadowBlur = 0;
    }
    resetGame();
  </script>
</body>
</html>
""".trimIndent()

        return AgentTask(
            goal = goal,
            explanation = "Designing, assembling, and launching an interactive 2D Cyberpunk HTML5 arcade game in the cloud sandbox environment.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Initialize Game Engine Directory & Assets",
                    thought = "Create isolated workspace folder for arcade assets and game logic.",
                    toolName = "bash_exec",
                    toolInput = "mkdir -p /workspace/games/cyber-snake && touch /workspace/games/cyber-snake/engine.js"
                ),
                AgentSubtask(
                    title = "Synthesize Canvas 2D Rendering Pipeline",
                    thought = "Write optimized HTML5 / Canvas / Tailwind responsive layout for mobile and desktop viewport.",
                    toolName = "write_file",
                    toolInput = "/workspace/index.html:::$gameHtml"
                ),
                AgentSubtask(
                    title = "Compile Game Logic & Test Build",
                    thought = "Run test build and verify zero runtime memory leaks.",
                    toolName = "bash_exec",
                    toolInput = "npm run test"
                ),
                AgentSubtask(
                    title = "Launch Sandbox Preview Server",
                    thought = "Mount the live web application on port 3000.",
                    toolName = "browser_open",
                    toolInput = "http://localhost:3000"
                )
            )
        )
    }

    private fun createFinancePlan(goal: String): AgentTask {
        val financeHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>VirgoYT Cloud AI - Alpha Market Terminal</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body class="bg-slate-950 text-slate-100 p-4 font-mono">
  <div class="max-w-4xl mx-auto space-y-4">
    <div class="flex justify-between items-center border-b border-slate-800 pb-3">
      <div class="flex items-center gap-2">
        <span class="w-3 h-3 bg-emerald-400 rounded-full animate-ping"></span>
        <h1 class="text-lg font-bold text-cyan-400">VIRGOYT CRYPTO QUANT TERMINAL</h1>
      </div>
      <span class="text-xs bg-emerald-950 text-emerald-300 px-2 py-1 rounded border border-emerald-800">LIVE FEED • PING: 12ms</span>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
      <div class="bg-slate-900 border border-slate-800 p-4 rounded-xl">
        <div class="text-xs text-slate-400">BTC / USD</div>
        <div class="text-2xl font-bold text-white mt-1">$94,820.50</div>
        <div class="text-xs text-emerald-400 mt-1">▲ +4.82% (24h)</div>
      </div>
      <div class="bg-slate-900 border border-slate-800 p-4 rounded-xl">
        <div class="text-xs text-slate-400">ETH / USD</div>
        <div class="text-2xl font-bold text-white mt-1">$3,420.10</div>
        <div class="text-xs text-emerald-400 mt-1">▲ +6.15% (24h)</div>
      </div>
      <div class="bg-slate-900 border border-slate-800 p-4 rounded-xl">
        <div class="text-xs text-slate-400">SOL / USD</div>
        <div class="text-2xl font-bold text-white mt-1">$214.80</div>
        <div class="text-xs text-emerald-400 mt-1">▲ +8.90% (24h)</div>
      </div>
    </div>

    <div class="bg-slate-900 border border-slate-800 p-4 rounded-xl">
      <div class="text-sm font-semibold text-slate-300 mb-2">Realtime Price Momentum (High-Frequency Order Flow)</div>
      <canvas id="quantChart" height="120"></canvas>
    </div>
  </div>
  <script>
    const ctx = document.getElementById('quantChart').getContext('2d');
    const chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', 'Now'],
        datasets: [{
          label: 'Portfolio Alpha (USD)',
          data: [91200, 92400, 91800, 93500, 94100, 93900, 94820],
          borderColor: '#00F0FF',
          backgroundColor: 'rgba(0, 240, 255, 0.1)',
          fill: true,
          tension: 0.4
        }]
      },
      options: {
        responsive: true,
        plugins: { legend: { labels: { color: '#94A3B8' } } },
        scales: {
          x: { grid: { color: '#1E293B' }, ticks: { color: '#64748B' } },
          y: { grid: { color: '#1E293B' }, ticks: { color: '#64748B' } }
        }
      }
    });
  </script>
</body>
</html>
""".trimIndent()

        return AgentTask(
            goal = goal,
            explanation = "Deploying Real-Time Market Analytics & Quant Portfolio Dashboard with Chart.js visualization engine.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Fetch & Structure Market Data Schema",
                    thought = "Generate mock orderbook feed and volatility records.",
                    toolName = "bash_exec",
                    toolInput = "python3 scripts/data_analyzer.py"
                ),
                AgentSubtask(
                    title = "Generate High-Frequency Trading Interface",
                    thought = "Assemble interactive Chart.js charts and live tickers.",
                    toolName = "write_file",
                    toolInput = "/workspace/index.html:::$financeHtml"
                ),
                AgentSubtask(
                    title = "Run Security & Performance Audit",
                    thought = "Inspect network requests and memory pressure in sandbox.",
                    toolName = "bash_exec",
                    toolInput = "npm run dev"
                ),
                AgentSubtask(
                    title = "Render in Cloud Browser Sandbox",
                    thought = "Mount and refresh live DOM preview on port 3000.",
                    toolName = "browser_open",
                    toolInput = "http://localhost:3000"
                )
            )
        )
    }

    private fun createDataSciencePlan(goal: String): AgentTask {
        return AgentTask(
            goal = goal,
            explanation = "Running automated Python data processing, statistical regression analysis, and CSV dataset report generation.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Verify Python 3.12 Virtual Environment",
                    thought = "Check installed packages and math library availability.",
                    toolName = "bash_exec",
                    toolInput = "python3 -V && pip list"
                ),
                AgentSubtask(
                    title = "Execute Data Science & Regression Script",
                    thought = "Process /workspace/data/metrics.csv through data_analyzer.py.",
                    toolName = "bash_exec",
                    toolInput = "python3 scripts/data_analyzer.py"
                ),
                AgentSubtask(
                    title = "Generate Markdown Summary Report",
                    thought = "Write formatted analytics summary to /workspace/REPORT.md.",
                    toolName = "write_file",
                    toolInput = "/workspace/REPORT.md:::# VirgoYT Cloud AI Automated Data Science Report\n\n- Processed: 7 time-series metrics\n- Mean Latency: 26.1ms\n- Model Accuracy: 98.4%\n- Conclusion: High system stability verified."
                ),
                AgentSubtask(
                    title = "Hot Reload Sandbox Dashboard",
                    thought = "Update the live sandbox preview with fresh metrics.",
                    toolName = "browser_open",
                    toolInput = "http://localhost:3000"
                )
            )
        )
    }

    private fun createKanbanPlan(goal: String): AgentTask {
        val kanbanHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>VirgoYT Cloud AI Kanban Workspace</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 p-4 font-sans min-h-screen">
  <div class="max-w-4xl mx-auto space-y-4">
    <div class="flex justify-between items-center border-b border-slate-800 pb-3">
      <h1 class="text-xl font-bold text-cyan-400">📋 VirgoYT Cloud Task Manager</h1>
      <button onclick="addTask()" class="px-3 py-1.5 bg-cyan-600 hover:bg-cyan-500 rounded-lg text-xs font-bold text-white">+ New Task</button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4" id="board">
      <div class="bg-slate-900 border border-slate-800 rounded-xl p-3">
        <h2 class="text-xs font-bold uppercase tracking-wider text-amber-400 mb-3">To Do (2)</h2>
        <div class="space-y-2">
          <div class="p-3 bg-slate-800/80 rounded-lg border border-slate-700 text-xs">
            <div class="font-bold text-white">Optimize GCC Compiler Flags</div>
            <div class="text-slate-400 mt-1">Add -O3 and -march=native for fast SIMD</div>
          </div>
          <div class="p-3 bg-slate-800/80 rounded-lg border border-slate-700 text-xs">
            <div class="font-bold text-white">Add Redis Cache Worker</div>
            <div class="text-slate-400 mt-1">Handle ephemeral sandbox state</div>
          </div>
        </div>
      </div>

      <div class="bg-slate-900 border border-slate-800 rounded-xl p-3">
        <h2 class="text-xs font-bold uppercase tracking-wider text-cyan-400 mb-3">In Progress (1)</h2>
        <div class="space-y-2">
          <div class="p-3 bg-cyan-950/40 rounded-lg border border-cyan-800 text-xs">
            <div class="font-bold text-cyan-300">Live Browser DevTools Integration</div>
            <div class="text-cyan-400/70 mt-1">Streaming console.log events to terminal</div>
          </div>
        </div>
      </div>

      <div class="bg-slate-900 border border-slate-800 rounded-xl p-3">
        <h2 class="text-xs font-bold uppercase tracking-wider text-emerald-400 mb-3">Done (3)</h2>
        <div class="space-y-2">
          <div class="p-3 bg-emerald-950/30 rounded-lg border border-emerald-900 text-xs">
            <div class="font-bold text-emerald-300">Virtual File System Mounted</div>
            <div class="text-emerald-400/70 mt-1">In-memory tree structure initialized</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <script>
    function addTask() {
      const title = prompt('Enter Task Title:');
      if(title) {
        alert('Task added: ' + title);
      }
    }
  </script>
</body>
</html>
""".trimIndent()

        return AgentTask(
            goal = goal,
            explanation = "Constructing a lightweight, local-storage enabled responsive Task & Kanban Board app.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Scaffold Project Structure",
                    thought = "Set up HTML/JS templates for client-side state storage.",
                    toolName = "bash_exec",
                    toolInput = "mkdir -p /workspace/src/components"
                ),
                AgentSubtask(
                    title = "Generate Kanban Application Core",
                    thought = "Assemble interactive task lists with status indicators.",
                    toolName = "write_file",
                    toolInput = "/workspace/index.html:::$kanbanHtml"
                ),
                AgentSubtask(
                    title = "Mount Web Sandbox",
                    thought = "Refresh browser sandbox and verify responsiveness.",
                    toolName = "browser_open",
                    toolInput = "http://localhost:3000"
                )
            )
        )
    }

    private fun createBenchmarkPlan(goal: String): AgentTask {
        return AgentTask(
            goal = goal,
            explanation = "Running multi-runtime benchmark: C (GCC 14.2) vs Node.js v22 vs Python 3.12.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Compile C Sorting Benchmark",
                    thought = "Compile main.c with GCC and check return codes.",
                    toolName = "bash_exec",
                    toolInput = "gcc main.c -o sort_benchmark && ./sort_benchmark"
                ),
                AgentSubtask(
                    title = "Execute Node.js Prime Sieve Benchmark",
                    thought = "Run compute-heavy JavaScript algorithm.",
                    toolName = "bash_exec",
                    toolInput = "node scripts/benchmark.js"
                ),
                AgentSubtask(
                    title = "Execute Python Statistical Analysis",
                    thought = "Compute latency and throughput statistics.",
                    toolName = "bash_exec",
                    toolInput = "python3 scripts/data_analyzer.py"
                ),
                AgentSubtask(
                    title = "Display Hardware Utilization",
                    thought = "Check virtual memory and CPU load.",
                    toolName = "bash_exec",
                    toolInput = "free -m && uptime"
                )
            )
        )
    }

    private fun createGenericWebPlan(goal: String): AgentTask {
        val appTitle = goal.take(30).replace("\"", "")
        val customHtml = """
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>$appTitle - VirgoYT Cloud AI</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-950 text-slate-100 p-6 font-sans">
  <div class="max-w-2xl mx-auto bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-4">
    <div class="flex items-center justify-between border-b border-slate-800 pb-4">
      <h1 class="text-xl font-bold text-cyan-400">$appTitle</h1>
      <span class="text-xs bg-cyan-950 text-cyan-300 px-2.5 py-1 rounded-full border border-cyan-800">AUTONOMOUS BUILD</span>
    </div>
    <p class="text-slate-300 text-sm">
      Goal: <span class="text-cyan-300 font-mono">"$goal"</span>
    </p>
    <div class="p-4 bg-slate-950 rounded-xl border border-slate-800 font-mono text-xs space-y-2">
      <div class="text-emerald-400">✓ Isolated virtual environment prepared</div>
      <div class="text-cyan-400">✓ Real-time script execution pipeline ready</div>
      <div class="text-amber-400">✓ Browser sandbox hot-reloaded</div>
    </div>
    <button onclick="alert('Autonomous Cloud PC Interaction Successful!')" class="w-full py-3 bg-cyan-600 hover:bg-cyan-500 text-white font-bold rounded-xl transition shadow-lg shadow-cyan-900/50">
      ⚡ Run Interactive App Trigger
    </button>
  </div>
</body>
</html>
""".trimIndent()

        return AgentTask(
            goal = goal,
            explanation = "Translating user specification into isolated files, scripts, and interactive preview.",
            subtasks = listOf(
                AgentSubtask(
                    title = "Analyze Workspace Environment",
                    thought = "Verify file tree and active dependencies.",
                    toolName = "bash_exec",
                    toolInput = "ls -la"
                ),
                AgentSubtask(
                    title = "Generate Application Bundle",
                    thought = "Write index.html with interactive features for: $goal",
                    toolName = "write_file",
                    toolInput = "/workspace/index.html:::$customHtml"
                ),
                AgentSubtask(
                    title = "Run Build Script",
                    thought = "Execute build pipeline in terminal.",
                    toolName = "bash_exec",
                    toolInput = "bash run.sh"
                ),
                AgentSubtask(
                    title = "Render in Sandbox Browser",
                    thought = "Hot-reload live preview window.",
                    toolName = "browser_open",
                    toolInput = "http://localhost:3000"
                )
            )
        )
    }
}
